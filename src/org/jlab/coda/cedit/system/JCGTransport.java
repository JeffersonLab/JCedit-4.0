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

@XmlRootElement(name = "transport")
public class JCGTransport {
    private String  name           = "undefined";
    private String  transClass     = "EmuSocket";
    private String  etName         = "undefined";
    private boolean etCreate       = false;
    private int     etTcpPort      = 23911;
    private int     etUdpPort      = 23912;
    private int     etWait         = 0;
    private String  mAddress       = "239.200.0.0";
    private int     etEventNum     = 1;
    private int     etEventSize    = 4200000; // in Bytes
    private int     inputEtChunkSize    = 2;
    private int     etChunkSize    = 2;
    private String  single         = "false";
    private int     etGroups       = 1;
    private int     etRecvBuf      = 0;
    private int     etSendBuf      = 0;
    private String  etMethodCon    = "mcast";
    private String  etHostName     = "anywhere";
    private String  etSubNet       = "undefined";
    private String  destinationEtCreate = "true";

    private int     emuDirectPort  = 46100;
    private int     emuMaxBuffer   = 1000000; // in Bytes
    private int     emuWait        = 5;
    private String  emuSubNet      = "undefined";
    private boolean emuFatPipe     = false;

    private String  cMsgHost       = "platform";
    private int     cMsgPort       = 45000;
    private String  cMsgNameSpace  = "undefined";
    private String  cMsgSubject    = "undefined";
    private String  cMsgType       = "undefined";

    private String  fileName    = "undefined";
    private String  fileType    = "coda";
    private long    fileSplit   = 20000000000l;
    private int fileInternalBuffer = 100;

    private boolean noLink = false;

    public JCGTransport() {
    }

    public JCGTransport(String name, String transClass, String etName, boolean etCreate, int etTcpPort,
                        int etUdpPort, int etWait, String mAddress, int etEventNum, int etEventSize,
                        int etChunkSize, int inputEtChunkSize, String single, int etGroups, int etRecvBuf, int etSendBuf,
                        String etMethodCon, String etHostName, String etSubNet, String destinationEtCreate, int emuDirectPort,
                        int emuMaxBuffer, int emuWait, String emuSubNet, boolean emuFatPipe, String cMsgHost, int cMsgPort,
                        String cMsgNameSpace, String cMsgSubject, String cMsgType, String fileName,
                        String fileType, long fileSplit, int fileInternalBuffer, boolean noLink) {
        this.name = name;
        this.transClass = transClass;
        this.etName = etName;
        this.etCreate = etCreate;
        this.etTcpPort = etTcpPort;
        this.etUdpPort = etUdpPort;
        this.etWait = etWait;
        this.mAddress = mAddress;
        this.etEventNum = etEventNum;
        this.etEventSize = etEventSize;
        this.etChunkSize = etChunkSize;
        this.inputEtChunkSize = inputEtChunkSize;
        this.single = single;
        this.etGroups = etGroups;
        this.etRecvBuf = etRecvBuf;
        this.etSendBuf = etSendBuf;
        this.etMethodCon = etMethodCon;
        this.etHostName = etHostName;
        this.etSubNet = etSubNet;
        this.destinationEtCreate = destinationEtCreate;
        this.emuDirectPort = emuDirectPort;
        this.emuMaxBuffer = emuMaxBuffer;
        this.emuWait = emuWait;
        this.emuSubNet = emuSubNet;
        this.emuFatPipe = emuFatPipe;
        this.cMsgHost = cMsgHost;
        this.cMsgPort = cMsgPort;
        this.cMsgNameSpace = cMsgNameSpace;
        this.cMsgSubject = cMsgSubject;
        this.cMsgType = cMsgType;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSplit = fileSplit;
        this.fileInternalBuffer = fileInternalBuffer;
        this.noLink = noLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransClass() {
        return transClass;
    }

    public void setTransClass(String transClass) {
        this.transClass = transClass;
    }

    public String getEtName() {
        return etName;
    }

    public void setEtName(String etName) {
        this.etName = etName;
    }

    public boolean isEtCreate() {
        return etCreate;
    }

    public void setEtCreate(boolean etCreate) {
        this.etCreate = etCreate;
    }

    public int getEtWait() {
        return etWait;
    }

    public void setEtWait(int etWait) {
        this.etWait = etWait;
    }

    public int getEtEventNum() {
        return etEventNum;
    }

    public void setEtEventNum(int etEventNum) {
        this.etEventNum = etEventNum;
    }

    public int getEtEventSize() {
        return etEventSize;
    }

    public void setEtEventSize(int etEventSize) {
        this.etEventSize = etEventSize;
    }

    public int getEtSendBuf() {
        return etSendBuf;
    }

    public void setEtSendBuf(int etSendBuf) {
        this.etSendBuf = etSendBuf;
    }

    public int getEtRecvBuf() {
        return etRecvBuf;
    }

    public void setEtRecvBuf(int etRecvBuf) {
        this.etRecvBuf = etRecvBuf;
    }

    public int getEtTcpPort() {
        return etTcpPort;
    }

    public void setEtTcpPort(int etTcpPort) {
        this.etTcpPort = etTcpPort;
    }

    public int getEtUdpPort() {
        return etUdpPort;
    }

    public void setEtUdpPort(int etUdpPort) {
        this.etUdpPort = etUdpPort;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSplit() {
        return fileSplit;
    }

    public void setFileSplit(long fileSplit) {
        this.fileSplit = fileSplit;
    }

    public int getFileInternalBuffer() {
        return fileInternalBuffer;
    }

    public void setFileInternalBuffer(int fileInternalBuffer) {
        this.fileInternalBuffer = fileInternalBuffer;
    }

    public String getEtHostName() {
        return etHostName;
    }

    public void setEtHostName(String etHostName) {
        this.etHostName = etHostName;
    }

    public String getEtSubNet() {
        return etSubNet;
    }

    public void setEtSubNet(String etSubNet) {
        this.etSubNet = etSubNet;
    }

    public String getDestinationEtCreate() {
        return destinationEtCreate;
    }

    public void setDestinationEtCreate(String destinationEtCreate) {
        this.destinationEtCreate = destinationEtCreate;
    }

    public String getEmuSubNet() {
        return emuSubNet;
    }

    public void setEmuSubNet(String emuSubNet) {
        this.emuSubNet = emuSubNet;
    }

    public boolean isEmuFatPipe() {
        return emuFatPipe;
    }

    public void setEmuFatPipe(boolean emuFatPipe) {
        this.emuFatPipe = emuFatPipe;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public int getEtGroups() {
        return etGroups;
    }

    public void setEtGroups(int etGroups) {
        this.etGroups = etGroups;
    }

    public String getEtMethodCon() {
        return etMethodCon;
    }

    public void setEtMethodCon(String etMethodCon) {
        this.etMethodCon = etMethodCon;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isNoLink() {
        return noLink;
    }

    public void setNoLink(boolean noLink) {
        this.noLink = noLink;
    }

    public int getEtChunkSize() {
        return etChunkSize;
    }

    public void setEtChunkSize(int etChunkSize) {
        this.etChunkSize = etChunkSize;
    }

    public int getInputEtChunkSize() {
        return inputEtChunkSize;
    }

    public void setInputEtChunkSize(int inputEtChunkSize) {
        this.inputEtChunkSize = inputEtChunkSize;
    }

    public int getEmuDirectPort() {
        return emuDirectPort;
    }

    public void setEmuDirectPort(int emuDirectPort) {
        this.emuDirectPort = emuDirectPort;
    }

    public int getEmuMaxBuffer() {
        return emuMaxBuffer;
    }

    public void setEmuMaxBuffer(int emuMaxBuffer) {
        this.emuMaxBuffer = emuMaxBuffer;
    }

    public int getEmuWait() {
        return emuWait;
    }

    public void setEmuWait(int emuWait) {
        this.emuWait = emuWait;
    }

    public String getcMsgHost() {
        return cMsgHost;
    }

    public void setcMsgHost(String cMsgHost) {
        this.cMsgHost = cMsgHost;
    }

    public int getcMsgPort() {
        return cMsgPort;
    }

    public void setcMsgPort(int cMsgPort) {
        this.cMsgPort = cMsgPort;
    }

    public String getcMsgNameSpace() {
        return cMsgNameSpace;
    }

    public void setcMsgNameSpace(String cMsgNameSpace) {
        this.cMsgNameSpace = cMsgNameSpace;
    }

    public String getcMsgSubject() {
        return cMsgSubject;
    }

    public void setcMsgSubject(String cMsgSubject) {
        this.cMsgSubject = cMsgSubject;
    }

    public String getcMsgType() {
        return cMsgType;
    }

    public void setcMsgType(String cMsgType) {
        this.cMsgType = cMsgType;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCGTransport that = (JCGTransport) o;

//        if (etCreate != that.isEtCreate()) return false;
        if (etEventNum != that.getEtEventNum()) return false;
        if (etEventSize != that.getEtEventSize()) return false;
        if (etChunkSize != that.getEtChunkSize()) return false;
        if (inputEtChunkSize != that.getInputEtChunkSize()) return false;
        if (etGroups != that.getEtGroups()) return false;
        if (etRecvBuf != that.getEtRecvBuf()) return false;
        if (etSendBuf != that.getEtSendBuf()) return false;
        if (etTcpPort != that.getEtTcpPort()) return false;
        if (etUdpPort != that.getEtUdpPort()) return false;
        if (etWait != that.getEtWait()) return false;
        if (etHostName != null ? !etHostName.equals(that.getEtHostName()) : that.getEtHostName() != null) return false;
        if (etSubNet != null ? !etSubNet.equals(that.getEtSubNet()) : that.getEtSubNet() != null) return false;
        if (destinationEtCreate != null ? !destinationEtCreate.equals(that.getDestinationEtCreate()) : that.getDestinationEtCreate() != null) return false;
        if (etMethodCon != null ? !etMethodCon.equals(that.getEtMethodCon()) : that.getEtMethodCon() != null) return false;
        if (etName != null ? !etName.equals(that.getEtName()) : that.getEtName() != null) return false;
        if (mAddress != null ? !mAddress.equals(that.getmAddress()) : that.getmAddress() != null) return false;

        if (emuWait != that.getEmuWait()) return false;
        if (emuDirectPort != that.getEmuDirectPort()) return false;
        if (emuMaxBuffer != that.getEmuMaxBuffer()) return false;
        if (emuSubNet != null ? !emuSubNet.equals(that.getEmuSubNet()) : that.getEmuSubNet() != null) return false;
        if (emuFatPipe != that.isEmuFatPipe()) return false;

        if (!cMsgHost.equals(getcMsgHost())) return false;
        if (!cMsgNameSpace.equals(getcMsgNameSpace())) return false;
        if (cMsgPort != that.getcMsgPort()) return false;
        if (!cMsgSubject.equals(getcMsgSubject())) return false;
        if (!cMsgType.equals(getcMsgType())) return false;

        if (fileSplit != that.getFileSplit()) return false;
        if (fileInternalBuffer != that.getFileInternalBuffer()) return false;
        if (noLink != that.isNoLink()) return false;
        if (fileName != null ? !fileName.equals(that.getFileName()) : that.getFileName() != null) return false;
        if (fileType != null ? !fileType.equals(that.getFileType()) : that.getFileType() != null) return false;
        if (name != null ? !name.equals(that.getName()) : that.getName() != null) return false;
        if (transClass != null ? !transClass.equals(that.getTransClass()) : that.getTransClass() != null) return false;
        return single.equals(that.getSingle());

    }

    @Override
    public String toString() {
        return "JCGTransport{" +
                "name='" + name + '\'' +
                ", \ntransClass='" + transClass + '\'' +
                ", \netName='" + etName + '\'' +
                ", \netCreate=" + etCreate +
                ", \netTcpPort=" + etTcpPort +
                ", \netUdpPort=" + etUdpPort +
                ", \netWait=" + etWait +
                ", \nmAddress='" + mAddress + '\'' +
                ", \netEventNum=" + etEventNum +
                ", \netEventSize=" + etEventSize +
                ", \netGroups=" + etGroups +
                ", \netRecvBuf=" + etRecvBuf +
                ", \netSendBuf=" + etSendBuf +
                ", \netMethodCon='" + etMethodCon + '\'' +
                ", \netHostName='" + etHostName + '\'' +
                ", \netSubNet='" + etSubNet + '\'' +
                ", \ndestinationEtCreate='" + destinationEtCreate + '\'' +
                ", \nfileName='" + fileName + '\'' +
                ", \nfileType='" + fileType + '\'' +
                ", \nfileSplit=" + fileSplit +
                ", \nfileInternalBf=" + fileInternalBuffer +
                ", \nnoLink=" + noLink +
                '}';
    }
}
