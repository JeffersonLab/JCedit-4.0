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
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "link")
public class JCGLink {

    private String             sourceEndian              = "big";
    private int                sourceCapacity            = 1;
    private int                sourceGroup               = 1;
    private String             sourceStationName         = "";
    private int                sourcePosition            = 1;
    private boolean            sourceIdFilter            = true;
    private int                sourceSendBuffer          = 0;
    private int                sourceRecvBuffer          = 0;
    private boolean            sourceNoDelay             = false;
    private int                sourceOThreads            = 1;
    private int                sourceWThreads            = 1;
    private String             sourceTransportName       = "";
    private String             sourceModuleName          = "Main";
    private String             sourceComponentName       = "";
    private String             sourceComponentType       = "";

    private String             destinationEndian         = "big";
    private int                destinationCapacity       = 1;
    private int                destinationGroup          = 1;
    private String             destinationStationName    = "";
    private int                destinationPosition       = 1;
    private String             destinationIdFilter       = "on";
    private int                destinationSendBuffer     = 0;
    private int                destinationRecvBuffer     = 0;
    private boolean            destinationNoDelay        = false;
    private int                destinationIThreads       = 1;
    private String             destinationTransportName  = "";
    private String             destinationModuleName     = "Main";
    private String             destinationComponentName  = "";
    private String             destinationComponentType  = "";

    private String             name                      = "undefined";
    private double             startX;
    private double             endX;
    private double             startY;
    private double             endY;

    public JCGLink() {
    }

    public JCGLink(String sourceEndian, int sourceCapacity, int sourceGroup, String sourceStationName,
                   int sourcePosition, boolean sourceIdFilter, int sourceSendBuffer, int sourceRecvBuffer,
                   boolean sourceNoDelay, int sourceOThreads, int sourceWThreads, String sourceTransportName,
                   String sourceModuleName, String sourceComponentName, String sourceComponentType,
                   String destinationEndian, int destinationCapacity, int destinationGroup,
                   String destinationStationName, int destinationPosition, String destinationIdFilter,
                   int destinationSendBuffer, int destinationRecvBuffer, boolean destinationNoDelay,
                   int destinationIThreads, String destinationTransportName, String destinationModuleName,
                   String destinationComponentName, String destinationComponentType, String name,
                   double startX, double endX, double startY, double endY) {
        this.sourceEndian = sourceEndian;
        this.sourceCapacity = sourceCapacity;
        this.sourceGroup = sourceGroup;
        this.sourceStationName = sourceStationName;
        this.sourcePosition = sourcePosition;
        this.sourceIdFilter = sourceIdFilter;
        this.sourceSendBuffer = sourceSendBuffer;
        this.sourceRecvBuffer = sourceRecvBuffer;
        this.sourceNoDelay = sourceNoDelay;
        this.sourceOThreads = sourceOThreads;
        this.sourceWThreads = sourceWThreads;
        this.sourceTransportName = sourceTransportName;
        this.sourceModuleName = sourceModuleName;
        this.sourceComponentName = sourceComponentName;
        this.sourceComponentType = sourceComponentType;
        this.destinationEndian = destinationEndian;
        this.destinationCapacity = destinationCapacity;
        this.destinationGroup = destinationGroup;
        this.destinationStationName = destinationStationName;
        this.destinationPosition = destinationPosition;
        this.destinationIdFilter = destinationIdFilter;
        this.destinationSendBuffer = destinationSendBuffer;
        this.destinationRecvBuffer = destinationRecvBuffer;
        this.destinationNoDelay = destinationNoDelay;
        this.destinationIThreads = destinationIThreads;
        this.destinationTransportName = destinationTransportName;
        this.destinationModuleName = destinationModuleName;
        this.destinationComponentName = destinationComponentName;
        this.destinationComponentType = destinationComponentType;
        this.name = name;
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceEndian() {
        return sourceEndian;
    }

    public void setSourceEndian(String sourceEndian) {
        this.sourceEndian = sourceEndian;
    }

    public int getSourceCapacity() {
        return sourceCapacity;
    }

    public void setSourceCapacity(int sourceCapacity) {
        this.sourceCapacity = sourceCapacity;
    }

    public int getSourceGroup() {
        return sourceGroup;
    }

    public void setSourceGroup(int sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public void setSourceStationName(String sourceStationName) {
        this.sourceStationName = sourceStationName;
    }

    public int getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(int sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public boolean isSourceIdFilter() {
        return sourceIdFilter;
    }

    public void setSourceIdFilter(boolean sourceIdFilter) {
        this.sourceIdFilter = sourceIdFilter;
    }

    public int getSourceSendBuffer() {
        return sourceSendBuffer;
    }

    public void setSourceSendBuffer(int sourceSendBuffer) {
        this.sourceSendBuffer = sourceSendBuffer;
    }

    public int getSourceRecvBuffer() {
        return sourceRecvBuffer;
    }

    public void setSourceRecvBuffer(int sourceRecvBuffer) {
        this.sourceRecvBuffer = sourceRecvBuffer;
    }

    public boolean isSourceNoDelay() {
        return sourceNoDelay;
    }

    public void setSourceNoDelay(boolean sourceNoDelay) {
        this.sourceNoDelay = sourceNoDelay;
    }

    public int getSourceOThreads() {
        return sourceOThreads;
    }

    public void setSourceOThreads(int sourceOThreads) {
        this.sourceOThreads = sourceOThreads;
    }

    public int getSourceWThreads() {
        return sourceWThreads;
    }

    public void setSourceWThreads(int sourceWThreads) {
        this.sourceWThreads = sourceWThreads;
    }

    public String getDestinationEndian() {
        return destinationEndian;
    }

    public void setDestinationEndian(String destinationEndian) {
        this.destinationEndian = destinationEndian;
    }

    public int getDestinationCapacity() {
        return destinationCapacity;
    }

    public void setDestinationCapacity(int destinationCapacity) {
        this.destinationCapacity = destinationCapacity;
    }

    public int getDestinationGroup() {
        return destinationGroup;
    }

    public void setDestinationGroup(int destinationGroup) {
        this.destinationGroup = destinationGroup;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }

    public void setDestinationStationName(String destinationStationName) {
        this.destinationStationName = destinationStationName;
    }

    public int getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(int destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    public String isDestinationIdFilter() {
        return destinationIdFilter;
    }

    public void setDestinationIdFilter(boolean destinationIdFilter) {
        if(destinationIdFilter){
            this.destinationIdFilter = "on";
        } else {
            this.destinationIdFilter = "off";
        }
    }

    public int getDestinationSendBuffer() {
        return destinationSendBuffer;
    }

    public void setDestinationSendBuffer(int destinationSendBuffer) {
        this.destinationSendBuffer = destinationSendBuffer;
    }

    public int getDestinationRecvBuffer() {
        return destinationRecvBuffer;
    }

    public void setDestinationRecvBuffer(int destinationRecvBuffer) {
        this.destinationRecvBuffer = destinationRecvBuffer;
    }

    public boolean isDestinationNoDelay() {
        return destinationNoDelay;
    }

    public void setDestinationNoDelay(boolean destinationNoDelay) {
        this.destinationNoDelay = destinationNoDelay;
    }

    public int getDestinationIThreads() {
        return destinationIThreads;
    }

    public void setDestinationIThreads(int destinationIThreads) {
        this.destinationIThreads = destinationIThreads;
    }

    public String getSourceTransportName() {
        return sourceTransportName;
    }

    public void setSourceTransportName(String sourceTransportName) {
        this.sourceTransportName = sourceTransportName;
    }

    public String getDestinationTransportName() {
        return destinationTransportName;
    }

    public void setDestinationTransportName(String destinationTransportName) {
        this.destinationTransportName = destinationTransportName;
    }

    public String getSourceComponentName() {
        return sourceComponentName;
    }

    public void setSourceComponentName(String sourceComponentName) {
        this.sourceComponentName = sourceComponentName;
    }

    public String getDestinationComponentName() {
        return destinationComponentName;
    }

    public void setDestinationComponentName(String destinationComponentName) {
        this.destinationComponentName = destinationComponentName;
    }

    public String getDestinationModuleName() {
        return destinationModuleName;
    }

    public void setDestinationModuleName(String destinationModuleName) {
        this.destinationModuleName = destinationModuleName;
    }

    public String getSourceModuleName() {
        return sourceModuleName;
    }

    public void setSourceModuleName(String sourceModuleName) {
        this.sourceModuleName = sourceModuleName;
    }

    public String getDestinationComponentType() {
        return destinationComponentType;
    }

    public void setDestinationComponentType(String destinationComponentType) {
        this.destinationComponentType = destinationComponentType;
    }

    public String getSourceComponentType() {

        return sourceComponentType;
    }

    public void setSourceComponentType(String sourceComponentType) {
        this.sourceComponentType = sourceComponentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JCGLink jcgLink = (JCGLink) o;

        if (destinationCapacity != jcgLink.getDestinationCapacity()) return false;
        if (destinationGroup != jcgLink.getDestinationGroup()) return false;
        if (destinationIThreads != jcgLink.getDestinationIThreads()) return false;
        if (destinationNoDelay != jcgLink.isDestinationNoDelay()) return false;
        if (destinationPosition != jcgLink.getDestinationPosition()) return false;
        if (destinationRecvBuffer != jcgLink.getDestinationRecvBuffer()) return false;
        if (destinationSendBuffer != jcgLink.getDestinationSendBuffer()) return false;
//        if (Double.compare(jcgLink.endX, endX) != 0) return false;
//        if (Double.compare(jcgLink.endY, endY) != 0) return false;
        if (sourceCapacity != jcgLink.getSourceCapacity()) return false;
        if (sourceGroup != jcgLink.getSourceGroup()) return false;
        if (sourceIdFilter != jcgLink.isSourceIdFilter()) return false;
        if (sourceNoDelay != jcgLink.isSourceNoDelay()) return false;
        if (sourceOThreads != jcgLink.getSourceOThreads()) return false;
        if (sourcePosition != jcgLink.getSourcePosition()) return false;
        if (sourceRecvBuffer != jcgLink.getSourceRecvBuffer()) return false;
        if (sourceSendBuffer != jcgLink.getSourceSendBuffer()) return false;
        if (sourceWThreads != jcgLink.getSourceWThreads()) return false;
//        if (Double.compare(jcgLink.startX, startX) != 0) return false;
//        if (Double.compare(jcgLink.startY, startY) != 0) return false;
        if (destinationComponentName != null ? !destinationComponentName.equals(jcgLink.getDestinationComponentName()) : jcgLink.getDestinationComponentName() != null)
            return false;
        if (destinationEndian != null ? !destinationEndian.equals(jcgLink.getDestinationEndian()) : jcgLink.getDestinationEndian() != null)
            return false;
        if (destinationIdFilter != null ? !destinationIdFilter.equals(jcgLink.isDestinationIdFilter()) : jcgLink.isDestinationIdFilter() != null)
            return false;

        if (destinationModuleName != null ? !destinationModuleName.equals(jcgLink.getDestinationModuleName()) : jcgLink.getDestinationModuleName() != null)
            return false;
        if (destinationStationName != null ? !destinationStationName.equals(jcgLink.getDestinationStationName()) : jcgLink.getDestinationStationName() != null)
            return false;

//        if (destinationTransportName != null ? !destinationTransportName.equals(jcgLink.getDestinationTransportName()) : jcgLink.getDestinationTransportName() != null)
//            return false;
        if (name != null ? !name.equals(jcgLink.getName()) : jcgLink.getName() != null) return false;
        if (sourceComponentName != null ? !sourceComponentName.equals(jcgLink.getSourceComponentName()) : jcgLink.getSourceComponentName() != null)
            return false;
        if (sourceEndian != null ? !sourceEndian.equals(jcgLink.getSourceEndian()) : jcgLink.getSourceEndian() != null)
            return false;
        if (sourceModuleName != null ? !sourceModuleName.equals(jcgLink.getSourceModuleName()) : jcgLink.getSourceModuleName() != null)
            return false;
        return sourceStationName != null ? sourceStationName.equals(jcgLink.getSourceStationName()) : jcgLink.getSourceStationName() == null;//        if (sourceTransportName != null ? !sourceTransportName.equals(jcgLink.getSourceTransportName()) : jcgLink.getSourceTransportName() != null)
//            return false;

    }

    @Override
    public String toString() {
        return "JCGLink{" +
                "sourceEndian='" + sourceEndian + '\'' +
                ", sourceCapacity=" + sourceCapacity +
                ", sourceGroup=" + sourceGroup +
                ", sourceStationName='" + sourceStationName + '\'' +
                ", sourcePosition=" + sourcePosition +
                ", sourceIdFilter=" + sourceIdFilter +
                ", sourceSendBuffer=" + sourceSendBuffer +
                ", sourceRecvBuffer=" + sourceRecvBuffer +
                ", sourceNoDelay=" + sourceNoDelay +
                ", sourceOThreads=" + sourceOThreads +
                ", sourceWThreads=" + sourceWThreads +
                ", sourceTransportName='" + sourceTransportName + '\'' +
                ", sourceModuleName='" + sourceModuleName + '\'' +
                ", sourceComponentName='" + sourceComponentName + '\'' +
                ", destinationEndian='" + destinationEndian + '\'' +
                ", destinationCapacity=" + destinationCapacity +
                ", destinationGroup=" + destinationGroup +
                ", destinationStationName='" + destinationStationName + '\'' +
                ", destinationPosition=" + destinationPosition +
                ", destinationIdFilter='" + destinationIdFilter + '\'' +
                ", destinationSendBuffer=" + destinationSendBuffer +
                ", destinationRecvBuffer=" + destinationRecvBuffer +
                ", destinationNoDelay=" + destinationNoDelay +
                ", destinationIThreads=" + destinationIThreads +
                ", destinationTransportName='" + destinationTransportName + '\'' +
                ", destinationModuleName='" + destinationModuleName + '\'' +
                ", destinationComponentName='" + destinationComponentName + '\'' +
                ", name='" + name + '\'' +
                ", startX=" + startX +
                ", endX=" + endX +
                ", startY=" + startY +
                ", endY=" + endY +
                '}';
    }
}
