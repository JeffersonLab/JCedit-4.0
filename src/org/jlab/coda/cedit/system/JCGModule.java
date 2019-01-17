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

package org.jlab.coda.cedit.system;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "module")
public class JCGModule {
    private String name ;

    private int id ;

    private String source          = "modules.jar";

    private String userSource      = "user_modules.jar";

    // the name of the class defining this module and must be loaded
    private String ebModuleClass  = "EventBuilding";
    private String rocModuleClass = "RocSimulation";
    private String tsModuleClass = "TsSimulation";
    private String erModuleClass  = "EventRecording";
    private String fcsModuleClass  = "FarmController";

    private String singleEventMode = "off";


    // sets the trigger type - can only be 4 bits (0-15)
    private int triggerType        = 15;

    // sets number of events per ROC raw record (255 max)
    private int blockSize          = 1;

    // sets number of ROC raw records per data transport record (255 max)
    private int numberRecords      = 1;

    private int threads            = 1;

    private boolean  runData              = false;
    private boolean  tsCheck              = true;
    private boolean  sparsify             = false;
    private int      tsSlop               = 2;

    @XmlElementWrapper(name = "channels")
    @XmlElement(name="channel")
    private Set<JCGChannel> channels = Collections.synchronizedSet(new HashSet<JCGChannel>());


    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public Set<JCGChannel> getChnnels() {
        return channels;
    }

    public void setChnnels(Set<JCGChannel> channels) {
        this.channels = channels;
    }

    public void addChnnel(JCGChannel c){
        if(!channels.contains(c)){
            channels.add(c);
        }
    }

    public String getModuleClass(String componentType) {
        if(componentType.equals(ACodaType.ROC.name()) ||
                componentType.equals(ACodaType.GT.name())){
            return rocModuleClass;
        } else if (componentType.equals(ACodaType.TS.name())) {
            return tsModuleClass;
        }else if (componentType.equals(ACodaType.ER.name())) {
            return erModuleClass;
        }  else if (componentType.equals(ACodaType.FCS.name())) {
            return fcsModuleClass;
        } else {
            return ebModuleClass;
        }
    }

    public void setEbModuleClass(String moduleClass) {
        this.ebModuleClass = moduleClass;
    }

    public void setRocModuleClass(String moduleClass) {
        this.rocModuleClass = moduleClass;
    }

    public void setERModuleClass(String moduleClass) {
        this.erModuleClass = moduleClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberRecords() {
        return numberRecords;
    }

    public void setNumberRecords(int numberRecords) {
        this.numberRecords = numberRecords;
    }

    public String getSingleEventMode() {
        return singleEventMode;
    }

    public void setSingleEventMode(String singleEventMode) {
        this.singleEventMode = singleEventMode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }


    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRunData() {
        return runData;
    }

    public void setRunData(boolean runData) {
        this.runData = runData;
    }

    public boolean isSparsify() {
        return sparsify;
    }

    public void setSparsify(boolean sparsify) {
        this.sparsify = sparsify;
    }

    public boolean isTsCheck() {
        return tsCheck;
    }

    public void setTsCheck(boolean tsCheck) {
        this.tsCheck = tsCheck;
    }

    public int getTsSlop() {
        return tsSlop;
    }

    public void setTsSlop(int tsSlop) {
        this.tsSlop = tsSlop;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCGModule jcgModule = (JCGModule) o;

        if (blockSize != jcgModule.blockSize) return false;
        if (id != jcgModule.id) return false;
        if (numberRecords != jcgModule.numberRecords) return false;

        if (runData != jcgModule.runData) return false;
        if (sparsify != jcgModule.sparsify) return false;
        if (threads != jcgModule.threads) return false;
        if (triggerType != jcgModule.triggerType) return false;
        if (tsCheck != jcgModule.tsCheck) return false;
        if (tsSlop != jcgModule.tsSlop) return false;
        if (channels != null ? !channels.equals(jcgModule.channels) : jcgModule.channels != null) return false;
        if (ebModuleClass != null ? !ebModuleClass.equals(jcgModule.ebModuleClass) : jcgModule.ebModuleClass != null)
            return false;
        if (erModuleClass != null ? !erModuleClass.equals(jcgModule.erModuleClass) : jcgModule.erModuleClass != null)
            return false;
        if (name != null ? !name.equals(jcgModule.name) : jcgModule.name != null) return false;
        if (rocModuleClass != null ? !rocModuleClass.equals(jcgModule.rocModuleClass) : jcgModule.rocModuleClass != null)
            return false;
        if (singleEventMode != null ? !singleEventMode.equals(jcgModule.singleEventMode) : jcgModule.singleEventMode != null)
            return false;
        if (source != null ? !source.equals(jcgModule.source) : jcgModule.source != null) return false;
        if (userSource != null ? !userSource.equals(jcgModule.userSource) : jcgModule.userSource != null) return false;

        return true;
    }

}
