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

@XmlRootElement(name = "channel")
public class JCGChannel {
    private String name;
    private String transportName;
    private JCGTransport transport;
    private int    id            = 0;
    private String endian        = "big";
    private int    capacity      = 40;
    private String blockNumCheck = "no";
    private int    revBuf        = 0;
    private int    sendBuf       = 0;
    private String noDelay       = "false";
    private int    group         = 1;
    private int    chunk         = 100;
    private String stationName   = "GRAND_CENTRAL";
    private int    position      = 1;
    private String idFilter      = "off";
    private int    ithreads      = 3;
    private int    othreads      = 1;
    private int    wthreads      = 2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JCGTransport getTransport() {
        return transport;
    }

    public void setTransport(JCGTransport transport) {
        this.transport = transport;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndian() {
        return endian;
    }

    public void setEndian(String endian) {
        this.endian = endian;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBlockNumCheck() {
        return blockNumCheck;
    }

    public void setBlockNumCheck(String blockNumCheck) {
        this.blockNumCheck = blockNumCheck;
    }

    public int getRevBuf() {
        return revBuf;
    }

    public void setRevBuf(int revBuf) {
        this.revBuf = revBuf;
    }

    public int getSendBuf() {
        return sendBuf;
    }

    public void setSendBuf(int sendBuf) {
        this.sendBuf = sendBuf;
    }

    public String getNoDelay() {
        return noDelay;
    }

    public void setNoDelay(String noDelay) {
        this.noDelay = noDelay;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getIdFilter() {
        return idFilter;
    }

    public void setIdFilter(String idFilter) {
        this.idFilter = idFilter;
    }

    public int getIthreads() {
        return ithreads;
    }

    public void setIthreads(int ithreads) {
        this.ithreads = ithreads;
    }

    public int getOthreads() {
        return othreads;
    }

    public void setOthreads(int othreads) {
        this.othreads = othreads;
    }

    public int getWthreads() {
        return wthreads;
    }

    public void setWthreads(int wthreads) {
        this.wthreads = wthreads;
    }

    @Override
    public String toString() {
        return "JCGChannel{" +
                "name='" + name + '\'' +
                ", transportName='" + transportName + '\'' +
                ", transport=" + transport +
                ", id=" + id +
                ", endian='" + endian + '\'' +
                ", capacity=" + capacity +
                ", blockNumCheck='" + blockNumCheck + '\'' +
                ", revBuf=" + revBuf +
                ", sendBuf=" + sendBuf +
                ", noDelay='" + noDelay + '\'' +
                ", group=" + group +
                ", chunk=" + chunk +
                ", stationName='" + stationName + '\'' +
                ", position=" + position +
                ", idFilter='" + idFilter + '\'' +
                ", ithreads=" + ithreads +
                ", othreads=" + othreads +
                ", wthreads=" + wthreads +
                '}';
    }
}
