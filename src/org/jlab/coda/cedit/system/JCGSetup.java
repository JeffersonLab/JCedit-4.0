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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

public class JCGSetup  implements Serializable {
    private static JCGSetup ourInstance = new JCGSetup();
    private String      coolHome;
    private String      expid     = "undefined";

    private HashMap<String, CSessionInfo> SessionDir;

    // COOL language definition (taxonomy) cool.rdfs file url
    public static final String COOL_HTTP_BASE       = "http://COOLHOME/";
    public static final String AFECSDB_HTTP_BASE    = "http://AFECSHOMEDB/";
    public static final String COOL_CORE            = AFECSDB_HTTP_BASE+"schema/cool#";

    public static JCGSetup getInstance() {
        return ourInstance;
    }

    private JCGSetup() {

        coolHome = System.getenv("COOL_HOME");
        if(coolHome == null) {
            System.out.println("Severe Error: Afecs database directory ($COOL_HOME) is not defined.");
            System.exit(1);
        }

        expid = System.getenv("EXPID");
        if(expid == null) {
            expid = "undefined";
        } else {
            if(createCoolDatabase(expid)<0) System.exit(0);
        }

        SessionDir = new HashMap<>();

        // read the sessions file
        readSessionDatabase();

    }

    public boolean isCoolDatabaseExist(String expid){
        return new File(coolHome +File.separator+expid+ File.separator+"setup.xml").exists();
    }

    public int createCoolDatabase(String expid){
        boolean ka;
        boolean success;

        String coolExp = coolHome +File.separator+expid;

        // see if setup file exists in the COOL_HOME
        ka = (new File(coolExp + File.separator+"setup.xml").exists());
        if(!ka){
            if(JCTools.popConfirmationDialog("Can not find a COOL database for \nEXPID = "+
                    expid +"\nin dir = "+coolHome,
                    "Do you wish to create one?",false)!=0){
                return -1;
            } else {
                success = (new File(coolExp)).mkdirs();
                if (!success) {
                    System.out.println("Error: "+coolExp+" dir exist and does not have a proper Afecs database structure. Exiting...");
                    return -1;
                }

            }
        }

        // create ddb dir  in cool_home/expid/ddb if it does not exist
        File df =  new File(coolExp+File.separator+"ddb");
        ka = df.exists();
        if(!ka){
            success = df.mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"ddb dir");
                return -1;
            }
        }

        // create log dir  in cool_home/expid/log if it does not exist
        File lf =  new File(coolExp+File.separator+"log");
        ka = lf.exists();
        if(!ka){
            success = lf.mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"log dir");
                return -1;
            }
        }

        // create config dir  in cool_home/expid/user if it does not exist
        File sf = new File(coolExp+File.separator+"user");
        ka = sf.exists();
        if(!ka){
            success = sf.mkdirs();
            try {
                Runtime.getRuntime().exec("chmod a+rwx "+coolExp+File.separator+"user");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"user dir");
                return -1;
            }
        }

        // create config dir  in cool_home/expid/user if it does not exist
        sf = new File(coolExp+File.separator+"user"+File.separator+"rtv");
        ka = sf.exists();
        if(!ka){
            success = sf.mkdirs();
            try {
                Runtime.getRuntime().exec("chmod a+rwx "+coolExp+File.separator+"user"+File.separator+"rtv");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"user/rtv dir");
                return -1;
            }
        }

        // create config dir  in cool_home/expid/jcedit if it does not exist
        ka = (new File(coolExp+File.separator+"jcedit")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+"jcedit")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"jcedit dir");
                return -1;
            }
        }

        // create .afecs_account dir in cool_home/expid if it does not exist
        ka = (new File(coolExp+File.separator+".afecs_account")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+".afecs_account")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+".afecs_account dir");
                return -1;
            }
        }

        // create config dir  in cool_home/expid if it does not exist
        ka = (new File(coolExp+File.separator+"config")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+"config")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"config dir");
                return -1;
            }
        }

        // create Control dir  in cool_home/expid/config if it does not exist
        ka = (new File(coolExp+File.separator+"config"+File.separator+"Control")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+"config"+File.separator+"Control")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"config"+File.separator+"Control"+" dir");
                return -1;
            }
        }


        // create docs dir  in cool_home/expid if it does not exist
        ka = (new File(coolExp+File.separator+"docs")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+"docs")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"docs dir");
                return -1;
            }
        }

        // create dalogArchive dir  in cool_home/expid if it does not exist
        ka = (new File(coolExp+File.separator+"dalogArchive")).exists();
        if(!ka){
            success = (new File(coolExp+File.separator+"dalogArchive")).mkdirs();
            if (!success) {
                System.out.println("Error: Failed to create "+coolExp+File.separator+"dalogArchive dir");
                return -1;
            }
        }
//            }
        createSetupXml(expid);


//        }
        return 0;
    }

    public boolean createSetupXml(String expid){
        boolean stat = true;
//        String dbUrl = System.getenv("MSQL_TCP_HOST");
//        String dbDriver = "undefined";
//        if(dbUrl == null){
////            System.out.println("Warning: CODA2 SQL db host is not defined.");
//            dbUrl = "undefined";
//        } else {
//            dbUrl    = "jdbc:msql://"+dbUrl+":8101/"+expid;
//            dbDriver = "com.imaginary.sql.msql.MsqlDriver";
//        }

        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(coolHome +File.separator+expid+File.separator+"setup.xml"));
            out.write("<afecs>\n");
            out.write("    <!--...................................................-->\n");
            out.write("    <!--..     COOL_HOME                                ..-->\n");
            out.write("    <!--..     EXPID                                     ..-->\n");
            out.write("    <!--..N.B. Above environmental variables must be set ..-->\n");
            out.write("    <!--...................................................-->\n\n");

            out.write("    <!--...................................................-->\n");
            out.write("    <!--..     Platform settings                         ..-->\n");
            out.write("    <!--...................................................-->\n");
            out.write("    <description>Afecs Platform</description>\n");
            out.write("    <platformHost>localhost</platformHost>\n");
            out.write("    <!--<tcpPort>45000</tcpPort>-->\n");
            out.write("    <!--<udpPort>45000</udpPort>-->\n\n");

            out.write("    <!--...................................................-->\n");
            out.write("    <!--..     RC domain settings                        ..-->\n");
            out.write("    <!--...................................................-->\n");
            out.write("    <!--<rcUdpPort>45200</rcUdpPort>-->\n");
            out.write("    <rcDescription>Afecs Platform RC Domain</rcDescription>\n\n");

//            if(!dbUrl.equals("undefined")){
//                out.write("    <!--...................................................-->\n");
//                out.write("    <!--..     coda2 db settings                         ..-->\n");
//                out.write("    <!--...................................................-->\n");
//                out.write("    <dbUrl>"+dbUrl+"</dbUrl>\n");
//                out.write("    <dbDriver>"+dbDriver+"</dbDriver>\n");
//                out.write("    <!--<dbUser>gurjyan</dbUser>-->\n");
//                out.write("    <!--<dbPasswd>...</dbpasswd>-->\n");
//            }
            out.write("</afecs>\n");
            out.close();
        } catch(IOException e){
            stat = false;
            System.out.println("Error: creating setup.xml.");
            System.out.println("Error: Can not run without setup.xml.");
            System.exit(0);
        }
        return stat;
    }


    private boolean readSessionDatabase(){
        boolean stat = true;
        if(SessionDir!=null) SessionDir.clear();
        if(getCoolHome()==null){
            System.out.println("Error: $COOL_HOME is not defined.");
            return false;
        }
        try {

            File sf = new File(getCoolHome()+File.separator+ getExpid()+File.separator+"ddb"+File.separator+"controlSessions.xml");
            if(sf.exists()){
                // Create a builder factory
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setValidating(false);

                // Create the builder and parse the file
                Document doc = factory.newDocumentBuilder().parse(sf);
                doc.getDocumentElement().normalize();

                NodeList nSession = doc.getElementsByTagName("session");
                for(int i=0; i<nSession.getLength();i++){
                    NodeList nl = nSession.item(i).getChildNodes();
                    CSessionInfo ci = new CSessionInfo();
                    for(int j=0;j<nl.getLength();j++){
                        Node n = nl.item(j);
                        if(n.getNodeType() == Node.ELEMENT_NODE){
                            Element ne= (Element)n;
                            NodeList nnl;
                            if(n.getNodeName().equals("name")){
                                nnl = ne.getChildNodes();
                                if(nnl!=null && nnl.getLength()>0){
                                    ci.setName(nnl.item(0).getNodeValue().trim());
                                }
                            } else if(n.getNodeName().equals("config")){
                                nnl = ne.getChildNodes();
                                if(nnl!=null && nnl.getLength()>0){
                                    ci.setConfigName(nnl.item(0).getNodeValue().trim());
                                }
                            } else if(n.getNodeName().equals("runnumber")){
                                nnl = ne.getChildNodes();
                                if(nnl!=null && nnl.getLength()>0){

                                    try{
                                        ci.setRunNumber(Integer.parseInt(nnl.item(0).getNodeValue().trim()));
                                    } catch(NumberFormatException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    SessionDir.put(ci.getName(),ci);
                }
            }
        } catch (SAXException e) {
            System.out.println("Error: Not valid xml input.");
            stat = false;
        } catch (ParserConfigurationException e) {
            System.out.println("Error: DOM parser configuration exception.");
            stat = false;
        } catch (IOException e) {
            System.out.println("Warning: Can not find sessions database file.");
            stat = false;
        }
        return stat;
    }

    private  boolean writeSessionsFile(){
        boolean stat = true;
        if(getCoolHome()==null){
            System.out.println("Error: System constant afecsHome is not defined.");
            return false;
        }
        try{
            File sf = new File(getCoolHome()+File.separator+ getExpid()+File.separator+"ddb"+File.separator+"controlSessions.xml");

            BufferedWriter out = new BufferedWriter(new FileWriter(sf));
            out.write("<control>\n");
            for(String cn: SessionDir.keySet()){
                out.write("   <session>\n");
                out.write("      <name>"+SessionDir.get(cn).getName()+"</name>\n");
                out.write("      <config>"+SessionDir.get(cn).getConfigName()+"</config>\n");
                out.write("      <runnumber>"+SessionDir.get(cn).getRunNumber()+"</runnumber>\n");
                out.write("   </session>\n");
            }
            out.write("</control>\n");
            out.close();
        } catch(IOException e){
            stat = false;
            e.printStackTrace();
        }
        return stat;
    }


    public String getCoolHome() {
        return coolHome;
    }

    public String getExpid() {
        return expid;
    }

    public void setExpid(String exp){
        expid = exp;
        // read the sessions file
        readSessionDatabase();
    }

    public String[] getSessionNames(){
        return SessionDir.keySet().toArray(new String[SessionDir.keySet().size()]);
    }

    public boolean isSessionExist(String sName){
        return SessionDir.containsKey(sName);
    }

    public boolean addSession(String sName){
        SessionDir.put(sName, new CSessionInfo(sName));
        return writeSessionsFile();
    }

    public boolean removeSession(String sName){
        if(SessionDir.containsKey(sName)){
            SessionDir.remove(sName);
            return writeSessionsFile();
        } else {
            return true;
        }
    }

    public boolean isInteger(String s){
        boolean b = true;
        try{
            Integer.parseInt(s);
        } catch (NumberFormatException e){
            b = false;
        }
        return b;
    }

}
