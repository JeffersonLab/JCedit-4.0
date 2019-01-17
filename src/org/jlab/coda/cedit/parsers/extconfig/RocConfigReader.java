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

package org.jlab.coda.cedit.parsers.extconfig;


import org.jlab.coda.cedit.system.JCGCompConfig;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGSetup;
import org.jlab.coda.cedit.system.JCGConcept;

import java.io.*;
import java.util.Set;
import java.util.StringTokenizer;

public class RocConfigReader {

    private String  fileName;

    private JCGComponent component;
    private JCGSetup stp = JCGSetup.getInstance();


    public RocConfigReader(String runType, String compName){
        fileName = stp.getCoolHome()+ File.separator+
                stp.getExpid()+File.separator+
                "config"+File.separator+
                "Control"+File.separator +
                runType+File.separator+
                "Options"+File.separator+
                compName+".dat";
    }

    public RocConfigReader(String runType, JCGComponent comp){
        this.component = comp;
        fileName = stp.getCoolHome()+ File.separator+
                stp.getExpid()+File.separator+
                "config"+File.separator+
                "Control"+File.separator +
                runType+File.separator+
                "Options"+File.separator+
                component.getName()+".dat";
    }

    public boolean isConfigExists(){
        return new File(fileName).exists();
    }

    public long getLastModified(){
        long lm = 0;
        File f = new File(fileName);
        if(f.exists()){
            lm = f.lastModified();
        }
        return lm;
    }

    public String getFileName(){
        return fileName;
    }

    public JCGCompConfig parseConfig(){
        JCGCompConfig c = new JCGCompConfig();

        // open and parse roc specific config file
        try {
            BufferedReader brd = new BufferedReader(new FileReader(fileName));
            String line;
            while((line = brd.readLine())!=null){
                StringTokenizer st = new StringTokenizer(line,"=");
                if(st.countTokens()==2){
                    c.addConcept(st.nextToken().trim(), st.nextToken().trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return c;
    }

    public static void main(String[] args){
        RocConfigReader rd = new RocConfigReader(args[0], args[1]);
        System.out.println("Configuration for "+args[1]+": file = "+rd.getFileName()+" exists = "+rd.isConfigExists());
        System.out.println("File was last modified = "+rd.getLastModified());
        if(rd.isConfigExists()){
            JCGCompConfig cf = rd.parseConfig();
            Set<JCGConcept> rc = cf.getConfigData();
            for(JCGConcept s:rc){
                System.out.println(s.getName()+" = "+s.getValue());
            }
        }
    }
}
