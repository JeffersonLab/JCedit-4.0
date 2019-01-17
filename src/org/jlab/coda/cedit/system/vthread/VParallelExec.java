/*
 *   Copyright (c) 2016.  Jefferson Lab (JLab). All rights reserved. Permission
 *   to use, copy, modify, and distribute  this software and its documentation for
 *   educational, research, and not-for-profit purposes, without fee and without a
 *   signed licensing agreement.
 *
 *   IN NO EVENT SHALL JLAB BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL
 *   INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING
 *   OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF JLAB HAS
 *   BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *   JLAB SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. THE CLARA SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF ANY,
 *   PROVIDED HEREUNDER IS PROVIDED "AS IS". JLAB HAS NO OBLIGATION TO PROVIDE
 *   MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 *   This software was developed under the United States Government license.
 *   For more information contact author at gurjyan@jlab.org
 *   Department of Experimental Nuclear Physics, Jefferson Lab.
 */

package org.jlab.coda.cedit.system.vthread;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class VParallelExec<E> {


    ExecutorService executorService;

    // user methods that needs to be run in parallel
    Set<VMethod<E>> methods = new HashSet<>();

    // callable objects that run user methods
    Set<Callable<Object>> callables = new HashSet<>();

    private int coreCount;

    public VParallelExec(){
     coreCount = Runtime.getRuntime().availableProcessors();
    }

    public List<Object> runp() throws InterruptedException, ExecutionException {
        if(!methods.isEmpty()){
            List<Object> result = new ArrayList<>();
            executorService = Executors.newFixedThreadPool(coreCount);
            for(final VMethod v:methods){
                callables.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        return v.execute();
                    }
                });
            }
            for (Future<Object> f:executorService.invokeAll(callables)){
                 result.add(f.get());
            }
            executorService.shutdown();
            return result;
        }
        return null;
    }

    public void addMethod(VMethod<E> vp){
        methods.add(vp);
    }

}
