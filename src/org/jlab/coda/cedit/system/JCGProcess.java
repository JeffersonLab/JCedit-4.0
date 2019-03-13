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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "process")
public class JCGProcess {
    private String  name           = "undefined";
    private boolean isSync;
    private boolean isPeriodic;
    private int     period;
    private String  transition     = "";
    private boolean isBefore;
    private boolean isAt;
    private boolean isAfter;

    private String  scriptCommand  = "";
    private int     exitCode       = 777;

    private String  sendSubject    = "";
    private String  sendType       = "";
    private String  sendText       = "";
    private boolean isSendRc;

    private String  receiveSubject = "";
    private String  receiveType    = "";
    private String  receiveText    = "";
    private boolean isReceiveRc;

    private boolean isInitiator;

    public JCGProcess() {
    }

    public JCGProcess(String name, boolean isSync, boolean isPeriodic, int period, String transition,
                      boolean isBefore, boolean isAt, boolean isAfter, String scriptCommand,
                      int exitCode, String sendSubject, String sendType, String sendText,
                      boolean isSendRc, String receiveSubject, String receiveType,
                      String receiveText, boolean isReceiveRc, boolean isInitiator) {
        this.name = name;
        this.isSync = isSync;
        this.isPeriodic = isPeriodic;
        this.period = period;
        this.transition = transition;
        this.isBefore = isBefore;
        this.isAt = isAt;
        this.isAfter = isAfter;
        this.scriptCommand = scriptCommand;
        this.exitCode = exitCode;
        this.sendSubject = sendSubject;
        this.sendType = sendType;
        this.sendText = sendText;
        this.isSendRc = isSendRc;
        this.receiveSubject = receiveSubject;
        this.receiveType = receiveType;
        this.receiveText = receiveText;
        this.isReceiveRc = isReceiveRc;
        this.isInitiator = isInitiator;
    }


    public boolean isPeriodic() {
        return isPeriodic;
    }

    public void setPeriodic(boolean periodic) {
        isPeriodic = periodic;
    }

    public boolean isReceiveRc() {
        return isReceiveRc;
    }

    public void setReceiveRc(boolean receiveRc) {
        isReceiveRc = receiveRc;
    }

    public boolean isSendRc() {
        return isSendRc;
    }

    public void setSendRc(boolean sendRc) {
        isSendRc = sendRc;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getReceiveSubject() {
        return receiveSubject;
    }

    public void setReceiveSubject(String receiveSubject) {
        this.receiveSubject = receiveSubject;
    }

    public String getReceiveText() {
        return receiveText;
    }

    public void setReceiveText(String receiveText) {
        this.receiveText = receiveText;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getScriptCommand() {
        return scriptCommand;
    }

    public void setScriptCommand(String scriptCommand) {
        this.scriptCommand = scriptCommand;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getSendSubject() {
        return sendSubject;
    }

    public void setSendSubject(String sendSubject) {
        this.sendSubject = sendSubject;
    }

    public String getSendText() {
        return sendText;
    }

    public void setSendText(String sendText) {
        this.sendText = sendText;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public boolean isAfter() {
        return isAfter;
    }

    public void setAfter(boolean after) {
        isAfter = after;
    }

    public boolean isAt() {
        return isAt;
    }

    public void setAt(boolean at) {
        isAt = at;
    }

    public boolean isBefore() {
        return isBefore;
    }

    public void setBefore(boolean before) {
        isBefore = before;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public boolean isInitiator() {
        return isInitiator;
    }

    public void setInitiator(boolean initiator) {
        isInitiator = initiator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCGProcess that = (JCGProcess) o;

        if (isAfter != that.isAfter()) return false;
        if (isAt != that.isAt()) return false;
        if (isBefore != that.isBefore()) return false;
//        if (isInitiator != that.isInitiator()) return false;
        if (isPeriodic != that.isPeriodic()) return false;
        if (isReceiveRc != that.isReceiveRc()) return false;
        if (isSendRc != that.isSendRc()) return false;
        if (isSync != that.isSync()) return false;
        if (period != that.getPeriod()) return false;
        if (name != null ? !name.equals(that.getName()) : that.getName() != null) return false;
//        if (receiveSubject != null ? !receiveSubject.equals(that.getReceiveSubject()) : that.getReceiveSubject() != null)
//            return false;
//        if (receiveText != null ? !receiveText.equals(that.getReceiveText()) : that.getReceiveText() != null) return false;
//        if (receiveType != null ? !receiveType.equals(that.getReceiveType()) : that.getReceiveType() != null) return false;
        if (scriptCommand != null ? !scriptCommand.equals(that.getScriptCommand()) : that.getScriptCommand() != null)
            return false;
        if (sendSubject != null ? !sendSubject.equals(that.getSendSubject()) : that.getSendSubject() != null) return false;
        if (sendText != null ? !sendText.equals(that.getSendText()) : that.getSendText() != null) return false;
        if (sendType != null ? !sendType.equals(that.getSendType()) : that.getSendType() != null) return false;
        if (transition != null ? !transition.equals(that.getTransition()) : that.getTransition() != null) return false;
        return exitCode == that.getExitCode();
    }

    @Override
    public String toString() {
        return "JCGProcess{" +
                "name='" + name + '\'' +
                ", isSync=" + isSync +
                ", isPeriodic=" + isPeriodic +
                ", period=" + period +
                ", transition='" + transition + '\'' +
                ", isBefore=" + isBefore +
                ", isAt=" + isAt +
                ", isAfter=" + isAfter +
                ", scriptCommand='" + scriptCommand + '\'' +
                ", exitCode=" + exitCode +
                ", sendSubject='" + sendSubject + '\'' +
                ", sendType='" + sendType + '\'' +
                ", sendText='" + sendText + '\'' +
                ", isSendRc=" + isSendRc +
                ", receiveSubject='" + receiveSubject + '\'' +
                ", receiveType='" + receiveType + '\'' +
                ", receiveText='" + receiveText + '\'' +
                ", isReceiveRc=" + isReceiveRc +
                ", isInitiator=" + isInitiator +
                '}';
    }
}
