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

package org.jlab.coda.cedit.util;

import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGTransport;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JCUtil {

    private static Matcher matcher;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        private static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    /**
     * Validate ip address with regular expression
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    public static boolean IP_validate(final String ip){
        matcher = pattern.matcher(ip);
        return matcher.matches();
    }


    public static String getDestCompName(JCGLink l){
        StringTokenizer st = new StringTokenizer(l.getName(),"_");
        st.nextToken();
        return st.nextToken();
    }
    
    public static JCGTransport deepCpTransport(String newName, boolean etCreate, JCGTransport t){
        JCGTransport sourceTransport = new JCGTransport();
        sourceTransport.setName(newName);
        sourceTransport.setEtCreate(etCreate);
        sourceTransport.setTransClass(t.getTransClass());
        sourceTransport.setEtEventNum(t.getEtEventNum());
        sourceTransport.setEtEventSize(t.getEtEventSize());
        sourceTransport.setEtGroups(t.getEtGroups());
        sourceTransport.setEtHostName(t.getEtHostName());
        sourceTransport.setEtSubNet(t.getEtSubNet());
        sourceTransport.setEtMethodCon(t.getEtMethodCon());
        sourceTransport.setEtName(t.getEtName());
        sourceTransport.setEtRecvBuf(t.getEtRecvBuf());
        sourceTransport.setEtSendBuf(t.getEtSendBuf());
        sourceTransport.setEtTcpPort(t.getEtTcpPort());
        sourceTransport.setEtUdpPort(t.getEtUdpPort());
        sourceTransport.setEtWait(t.getEtWait());
        sourceTransport.setFileName(t.getFileName());
        sourceTransport.setFileSplit(t.getFileSplit());
        sourceTransport.setFileType(t.getFileType());
        sourceTransport.setmAddress(t.getmAddress());
        sourceTransport.setNoLink(t.isNoLink());

        sourceTransport.setEmuDirectPort(t.getEmuDirectPort());
        sourceTransport.setEmuMaxBuffer(t.getEmuMaxBuffer());
        sourceTransport.setEmuWait(t.getEmuWait());

        sourceTransport.setcMsgHost(t.getcMsgHost());
        sourceTransport.setcMsgNameSpace(t.getcMsgNameSpace());
        sourceTransport.setcMsgPort(t.getcMsgPort());
        sourceTransport.setcMsgSubject(t.getcMsgSubject());
        sourceTransport.setcMsgType(t.getcMsgType());
        return sourceTransport;
    }

    public static void sleep(int i){
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
