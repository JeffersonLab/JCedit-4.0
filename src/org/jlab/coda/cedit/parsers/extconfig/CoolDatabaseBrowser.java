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

import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.vthread.VMethod;
import org.jlab.coda.cedit.system.vthread.VParallelExec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CoolDatabaseBrowser {


    public CoolDatabaseBrowser(){
        System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory","com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    // Cool  top level configuration files directory

    private JCGSetup stp = JCGSetup.getInstance();


    private String getFilePath(){
        return stp.getCoolHome() + File.separator +
                stp.getExpid() + File.separator +
                "config" + File.separator +
                "Control" + File.separator;
    }

    /**
     * Returns serialized components absolute path
     * @param runType  configuration
     * @param compName component name
     * @return File object
     */
    private File getSerializedCompPath(String runType, String compName){
        String fp = getFilePath() + runType + File.separator + "Options" + File.separator;
        String fileName = fp + "."+compName + "_p.xml";
        File f = new  File(fileName);

        if (f.exists()){
            return new File(f.getAbsolutePath());
        }
        return null;
    }

    /**
     * <p>
     *     Returns last modified date of the component configuration file
     * </p>
     * @param runType configuration
     * @param compName component name
     * @return last modified date
     */
    public long compLastModified(String runType, String compName){
        long res = -1;
        String fp = getFilePath() + runType + File.separator + "Options" + File.separator;
        String fileName = fp + "."+compName + "_p.xml";
        File f = new  File(fileName);

        if (f.exists()){
            res = f.lastModified();
        }
        return res;
    }


    /**
     * Map all components in the runType specific directory of the database
     * @param runType or CODA configuration
     * @return ArrayList of {@link JCGComponent} objects
     */
//    public Map<String,JCGComponent> listComponents(String runType){
//        Map<String,JCGComponent> m = new HashMap<>();
//        String fp = getFilePath() + runType + File.separator + "Options" + File.separator;
//
//        File dir = new File(fp);
//        for(File f:dir.listFiles(new FileListFilter(".","_p.xml"))) {
//            JCGComponent c =  getJCGComponent(new File(f.getAbsolutePath()));
//            if(c!=null){
//                m.put(c.getName(), c);
//            }
//        }
//        return m;
//    }

    public Map<String,JCGComponent> JLC(String runType)
            throws ExecutionException, InterruptedException {
        Map<String,JCGComponent> result = new HashMap<>();
        VParallelExec parallelExec = new VParallelExec();

        String fp = getFilePath() + runType + File.separator + "Options" + File.separator;

        File dir = new File(fp);

        for(File f:dir.listFiles(new FileListFilter(".","_p.xml"))) {
            File ap = new File(f.getAbsolutePath());
            parallelExec.addMethod(new JCGDeSerializer(ap));
        }
         List<Object> lr = parallelExec.runp();
        if(lr!=null && !lr.isEmpty()) {
            for (Object c : lr) {
              JCGComponent jc = (JCGComponent)c;
                result.put(jc.getName(), jc);
            }
        }
        return result;
    }

    /**
     * Parses Java serialized file ( JAXB xml file) and returns object
     * @param f the absolute path to the serialized file
     * @return {@link JCGComponent}
     */
    private JCGComponent getJCGComponent(File f){
        JCGComponent comp = null;
        if (f.exists()){
            JAXBContext context;
            try {
                context = JAXBContext.newInstance(JCGComponent.class);
                Unmarshaller um = context.createUnmarshaller();
                comp = (JCGComponent) um.unmarshal(f);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return comp;

    }

    public void JLX (String runType, List<JCGComponent> components, boolean removeConfDirFirst)
            throws ExecutionException, InterruptedException {
        VParallelExec parallelExec = new VParallelExec();
        Set<String> supComps = new HashSet<>();
        JCGComponent sup = null;
        updateCoolDir(runType, removeConfDirFirst);

        // writes emu and roc specific config files
        LLConfigWriter cw = new LLConfigWriter(runType,components);
        cw.process();

        for(JCGComponent c:components) {
            if(c.getName().equals(runType)) {
                sup = c;
            }
            if(!c.getName().equals(runType)) {
                parallelExec.addMethod(new JCGSerializer(runType, c, null, false));
                supComps.add(c.getName());
            }
        }
        if(sup!=null) {
            parallelExec.addMethod(new JCGSerializer(runType, sup, supComps, removeConfDirFirst));
        }
        parallelExec.runp();


    }

    private void xmlDumpComponent(String runType, JCGComponent cmp){
        JCGSetup stp = JCGSetup.getInstance();
        String filePath = stp.getCoolHome() + File.separator +
                stp.getExpid() + File.separator +
                "config" + File.separator +
                "Control" + File.separator +
                runType + File.separator +
                "Options" + File.separator;

        JAXBContext context;
        try {
            context = JAXBContext.newInstance(JCGComponent.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            FileOutputStream fos = new FileOutputStream(new File(filePath+"."+cmp.getName()+"_p.xml"));
            m.marshal(cmp, fos);
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateCoolDir(String runType, boolean removeFirst){
        File dir;
        // create expid dir
        dir = new File(stp.getCoolHome()+File.separator+stp.getExpid());
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create expid/jcedit dir
        dir = new File(stp.getCoolHome()+File.separator+stp.getExpid()+File.separator+"jcedit");
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create expid/config dir
        dir = new File(stp.getCoolHome()+File.separator+stp.getExpid()+File.separator+"config");
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create expid/config/Control dir
        dir = new File(JCTools.defineExpIDDire());
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create runType sub-dir in the Control dir if not existed.
        dir = new File(JCTools.defineExpIDDire()+File.separator+runType);


        if(removeFirst)  JCTools.deleteDir(dir);

        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create Components sub-dir in the runType dir if not existed.
        dir = new File(JCTools.defineExpIDDire()+File.separator+runType+File.separator+"Components");
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create Options sub-dir in the runType dir if not existed.
        dir = new File(JCTools.defineExpIDDire()+File.separator+runType+File.separator+"Options");
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create Links sub-dir in the runType dir if not existed.
        dir = new File(JCTools.defineExpIDDire()+File.separator+runType+File.separator+"Links");
        if(!JCTools.fCheckCreate(dir))System.exit(1);

        // create Processes sub-dir in the runType dir if not existed.
        dir = new File(JCTools.defineExpIDDire()+File.separator+runType+File.separator+"Processes");
        if(!JCTools.fCheckCreate(dir))System.exit(1);


    }
    private void coolDumpSupervisor(JCGComponent c, Set<String> components, String newRunType){
        if(c!=null && !c.getName().equals("")) {
            // if user requests name change
            if (!c.getName().equals(newRunType)) c.setName(newRunType);

            // recreate main configuration rdf
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + c.getName() + File.separator + c.getName() + ".rdf"));
                out.write("<rdf:RDF\n");
                out.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                out.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                out.write(">\n");
                out.write("\n");
                out.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/" + c.getName() + "#" + c.getName() + "\">\n");

                for (String cn : components) {
                    if (!c.getName().equals(cn)) {
                        out.write("\n");
                        out.write("   <cool:include rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/Components/" + cn + ".rdf\"/>\n");
                        out.write("   <cool:hasComponent rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/Components/" + cn + "#" + cn + "\"/>\n");
                        out.write("\n");
                    }
                }


                // now crete rdf with new process descriptions for the existing foreign runType
                for (JCGProcess prc : c.getPrcesses()) {

                    // update process name, replacing old supervisor name with the new one
//                prc.setName(prc.getName().replace(oldRunType,c.getName()));
                    out.write("\n");
                    out.write("   <cool:include rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/Processes/" + prc.getName() + ".rdf\"/>\n");
                    out.write("   <cool:hasProcess rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/Processes/" + prc.getName() + "#" + prc.getName() + "\"/>\n\n");
                    out.write("\n");

                    try {
                        BufferedWriter opout = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + c.getName() + File.separator + "Processes" + File.separator + prc.getName() + ".rdf"));
                        opout.write("<rdf:RDF\n");
                        opout.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                        opout.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                        opout.write(">\n");
                        opout.write("\n");
                        opout.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + c.getName() + "/Processes/" + prc.getName() + "#" + prc.getName() + "\">\n");
                        opout.write("\n");
                        // name
                        opout.write("   <cool:hasName>" + prc.getName() + "</cool:hasName>\n");
                        // sync
                        if (prc.isSync()) {
                            opout.write("   <cool:isSync>true</cool:isSync>\n");
                        } else {
                            opout.write("   <cool:isSync>false</cool:isSync>\n");
                        }
                        if (prc.isPeriodic()) {
                            int period = prc.getPeriod();
                            opout.write("   <cool:hasPeriodicity>-1</cool:hasPeriodicity>\n");
                            opout.write("   <cool:hasDelay>" + period + "</cool:hasDelay>\n");
                        }
                        // before or after
                        String tmpState = null;
                        String tmpTransition = prc.getTransition();
                        switch (tmpTransition) {
                            case "download":
                                tmpState = "downloaded";
                                break;
                            case "prestart":
                                tmpState = "prestarted";
                                break;
                            case "go":
                                tmpState = "active";
                                break;
                            case "end":
                                tmpState = "ended";
                                break;
                            case "reset":
                                tmpState = "reseted";
                                prc.setBefore(true);
                                prc.setAfter(false);
                                break;
                            case "pause":
                                tmpState = "paused";
                                prc.setBefore(true);
                                prc.setAfter(false);
                                break;
                            case "resume":
                                tmpState = "resumed";
                                prc.setBefore(true);
                                prc.setAfter(false);
                                break;
                        }

                        if (tmpState != null) {
                            if (prc.isBefore()) {
                                opout.write("   <cool:before>" + tmpState + "</cool:before>\n");
                            } else if (prc.isAfter()) {
                                opout.write("   <cool:after>" + tmpState + "</cool:after>\n");
                            }
                        }
                        // send package includes
                        if (!prc.getSendSubject().equals("") && !prc.getSendType().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:sends> drf:resource = \"#" + prc.getName() + "_sendPackage\"/>\n");
                        }
                        // received package includes
                        if (!prc.getReceiveSubject().equals("") && !prc.getReceiveType().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:receives> drf:resource = \"#" + prc.getName() + "_receivePackage\"/>\n");

                            // initiator
                            if (prc.isInitiator()) {
                                opout.write("   <cool:isInitiator>true</cool:isInitiator>\n");
                            } else {
                                opout.write("   <cool:isInitiator>false</cool:isInitiator>\n");
                            }
                        }
                        // script includes
                        if (!prc.getScriptCommand().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:runs rdf:resource =\"#" + prc.getName() + "_script\"/>\n");
                        }
                        opout.write("\n");
                        opout.write("</rdf:Description>\n");
                        opout.write("\n");

                        // send package description
                        if (!prc.getSendSubject().equals("") && !prc.getSendType().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:Package rdf:ID=\"" + prc.getName() + "_sendPackage\">\n");
                            opout.write("   <cool:hasName>" + prc.getName() + "_sendPackage</cool:hasName>\n");
                            opout.write("   <cool:hasSendSubject>" + prc.getSendSubject() + "</cool:hasSendSubject>\n");
                            opout.write("   <cool:hasSendType>" + prc.getSendType() + "</cool:hasSendType>\n");
                            opout.write("   <cool:hasSendText>" + prc.getSendText() + "</cool:hasSendText>\n");
                            if (prc.isSendRc()) {
                                opout.write("   <cool:isForRcClient>true</cool:isForRcClient>\n");
                            }
                            opout.write("   </cool:Package>\n");
                        }

                        // received package description
                        if (!prc.getReceiveSubject().equals("") && !prc.getReceiveType().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:Package rdf:ID=\"" + prc.getName() + "_receivePackage\">\n");
                            opout.write("   <cool:hasName>" + prc.getName() + "_receivePackage</cool:hasName>\n");
                            opout.write("   <cool:hasReceivedSubject>" + prc.getReceiveSubject() + "</cool:hasReceivedSubject>\n");
                            opout.write("   <cool:hasReceivedType>" + prc.getReceiveType() + "</cool:hasReceivedType>\n");
                            opout.write("   <cool:hasReceivedText>" + prc.getReceiveText() + "</cool:hasReceivedText>\n");
                            if (prc.isReceiveRc()) {
                                opout.write("   <cool:isForRcClient>true</cool:isForRcClient>\n");
                            }
                            opout.write("   </cool:Package>\n");
                        }

                        // script description
                        if (!prc.getScriptCommand().equals("")) {
                            opout.write("\n");
                            opout.write("   <cool:Script rdf:ID=\"" + prc.getName() + "_script\">\n");
                            opout.write("   <cool:hasName>" + prc.getName() + "_script</cool:hasName>\n");
                            opout.write("   <cool:hasCommandString>" + prc.getScriptCommand() + "</cool:hasCommandString>\n");
                            if (prc.getExitCode() != 777) {
                                opout.write("   <cool:hasExitCode>" + prc.getExitCode() + "</cool:hasExitCode>\n");
                            }
                            if (prc.isSync()) {
                                opout.write("   <cool:isSynchronous>true</cool:isSynchronous>\n");
                            } else {
                                opout.write("   <cool:isSynchronous>false</cool:isSynchronous>\n");
                            }
                            opout.write("   </cool:Script>\n");
                        }
                        opout.write("\n");
                        opout.write("</rdf:RDF>\n");
                        opout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                out.write("</rdf:Description>\n");
                out.write("</rdf:RDF>\n");
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates component Cool-RDF description file
     * @param runType configuration name
     * @param gc {@link JCGComponent} object
     */
    private void coolDumpComponent(String runType, JCGComponent gc){
        if(gc!=null && !gc.getName().equals("")) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + runType + File.separator + "Components" + File.separator + gc.getName() + ".rdf"));
                out.write("<rdf:RDF\n");
                out.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                out.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                out.write(">\n");
                out.write("\n");

                // name
                out.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Components/" + gc.getName() + "#" + gc.getName() + "\">\n");
                out.write("\n");
                out.write("   <cool:hasName>" + gc.getName() + "</cool:hasName>\n");


                // id
                out.write("   <cool:hasID>" + gc.getId() + "</cool:hasID>\n");

                // type
                out.write("   <cool:hasType>" + gc.getType() + "</cool:hasType>\n");

                // embedded code
                if (gc.createCode().length() > 0)
                    out.write("   <cool:hasCode>" + gc.createCode().toString() + "</cool:hasCode>\n");

                // priority
                out.write("   <cool:hasPriority>" + gc.getPriority() + "</cool:hasPriority>\n");

                // isCodaComponent
                out.write("   <cool:isCodaComponent>" + gc.isCodaComponent() + "</cool:isCodaComponent>\n");

                for (JCGLink link : gc.getLnks()) {
                    if(!link.getSourceComponentName().equals(gc.getName())) {
                        // streamID = linked component name
                        out.write("   <cool:linkedTo>" + link.getSourceComponentName() + "</cool:linkedTo>\n");
                        out.write("   <cool:linkedToType>" + link.getSourceComponentType() + "</cool:linkedToType>\n");
                    }
                }

                // does this component represents Coda2 client
                if (gc.isCodaVersion2()) {
//                    out.write("   <cool:representsCoda2Component>true</cool:representsCoda2Component>\n");
                    out.write("   <cool:streamingComponent>true</cool:streamingComponent>\n"); // vg 01.26.22
                    // .... component communication plugin
                    out.write("\n");
                    out.write("   <cool:include rdf:resource=\"" + JCGSetup.AFECSDB_HTTP_BASE + "Plugin/DPPlugin.rdf\"/>\n");
                    out.write("   <cool:hasCommunicationPlugin rdf:resource=\"" + JCGSetup.AFECSDB_HTTP_BASE + "Plugin/DPPlugin#DPPlugin\"/>\n");
                    out.write("\n");
                }
                if (!gc.getUserConfig().equals("") && !gc.getUserConfig().equals("undefined")) {
                    out.write("   <cool:hasUserConfig>" + gc.getUserConfig() + "</cool:hasUserConfig>\n");
                }

                // config file
                out.write("\n");
                out.write("   <cool:include rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Options/" + gc.getName() + "_option.rdf\"/>\n");
                out.write("   <cool:hasOption rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Options/" + gc.getName() + "_option#" + gc.getName() + "_option\"/>\n\n");
                out.write("\n");

                try {
                    BufferedWriter opout = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + runType + File.separator + "Options" + File.separator + gc.getName() + "_option.rdf"));
                    opout.write("<rdf:RDF\n");
                    opout.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                    opout.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                    opout.write(">\n");
                    opout.write("\n");
                    opout.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Options/" + gc.getName() + "_option#" + gc.getName() + "_option\">\n");
                    opout.write("\n");
                    opout.write("   <cool:hasName>" + gc.getName() + "_option</cool:hasName>\n");

                    if (gc.getType().equals(ACodaType.ROC.name()) ||
                            gc.getType().equals(ACodaType.USR.name()) ||
                            gc.getType().equals(ACodaType.GT.name()) ||
                            gc.getType().equals(ACodaType.FPGA.name()) ||
                            gc.getType().equals(ACodaType.TS.name())) {
                        opout.write("   <cool:hasConfigFile>" + gc.getName() + ".dat</cool:hasConfigFile>\n");


                    } else {
                        opout.write("   <cool:hasConfigFile>" + gc.getName() + ".xml</cool:hasConfigFile>\n");
                    }
                    opout.write("\n");
                    opout.write("</rdf:Description>\n");
                    opout.write("</rdf:RDF>\n");
                    opout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // links
                if (gc.getLnks() != null && !gc.getLnks().isEmpty()) {

                    for (JCGLink link : gc.getLnks()) {

                        out.write("\n");
                        out.write("   <cool:include rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Links/" + link.getName() + ".rdf\"/>\n");
                        out.write("   <cool:usesLink rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Links/" + link.getName() + "#" + link.getName() + "\"/>\n\n");
                        out.write("\n");

                        try {
                            BufferedWriter opout = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + runType + File.separator + "Links" + File.separator + link.getName() + ".rdf"));
                            opout.write("<rdf:RDF\n");
                            opout.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                            opout.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                            opout.write(">\n");
                            opout.write("\n");
                            opout.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Links/" + link.getName() + "#" + link.getName() + "\">\n");
                            opout.write("\n");
                            opout.write("   <cool:hasName>" + link.getName() + "</cool:hasName>\n");
                            opout.write("   <cool:hasDescription>Missing</cool:hasDescription>\n");
                            opout.write("   <cool:sourceComponentName>" + link.getSourceComponentName() + "</cool:sourceComponentName>\n");
                            opout.write("   <cool:destinationComponentName>" + link.getDestinationComponentName() + "</cool:destinationComponentName>\n");
                            opout.write("\n");
                            opout.write("   <cool:startX>" + link.getStartX() + "</cool:startX>\n");
                            opout.write("   <cool:startY>" + link.getStartY() + "</cool:startY>\n");
                            opout.write("   <cool:endX>" + link.getEndX() + "</cool:endX>\n");
                            opout.write("   <cool:endY>" + link.getEndY() + "</cool:endY>\n");
                            opout.write("\n");
                            opout.write("</rdf:Description>\n");
                            opout.write("</rdf:RDF>\n");
                            opout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // processes
                if (gc.getPrcesses() != null && !gc.getPrcesses().isEmpty()) {

                    for (JCGProcess prc : gc.getPrcesses()) {
                        out.write("\n");
                        out.write("   <cool:include rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Processes/" + prc.getName() + ".rdf\"/>\n");
                        out.write("   <cool:hasProcess rdf:resource=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Processes/" + prc.getName() + "#" + prc.getName() + "\"/>\n\n");
                        out.write("\n");

                        try {
                            BufferedWriter opout = new BufferedWriter(new FileWriter(JCTools.defineExpIDDire() + File.separator + runType + File.separator + "Processes" + File.separator + prc.getName() + ".rdf"));
                            opout.write("<rdf:RDF\n");
                            opout.write("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n");
                            opout.write("xmlns:cool=\"" + JCGSetup.COOL_CORE + "\"\n");
                            opout.write(">\n");
                            opout.write("\n");
                            opout.write("<rdf:Description rdf:about=\"" + JCGSetup.COOL_HTTP_BASE + "Control/" + runType + "/Processes/" + prc.getName() + "#" + prc.getName() + "\">\n");
                            opout.write("\n");
                            // name
                            opout.write("   <cool:hasName>" + prc.getName() + "</cool:hasName>\n");
                            // sync
                            if (prc.isSync()) {
                                opout.write("   <cool:isSync>true</cool:isSync>\n");
                            } else {
                                opout.write("   <cool:isSync>false</cool:isSync>\n");
                            }
                            if (prc.isPeriodic()) {
                                int period = prc.getPeriod();
                                opout.write("   <cool:hasPeriodicity>-1</cool:hasPeriodicity>\n");
                                opout.write("   <cool:hasDelay>" + period + "</cool:hasDelay>\n");
                            }

                            // before or after
                            String tmpState = null;
                            String tmpTransition = prc.getTransition();
                            switch (tmpTransition) {
                                case "download":
                                    tmpState = "downloaded";
                                    break;
                                case "prestart":
                                    tmpState = "prestarted";
                                    break;
                                case "go":
                                    tmpState = "active";
                                    break;
                                case "end":
                                    tmpState = "ended";
                                    break;
                            }
                            if (tmpState != null) {
                                if (prc.isBefore()) {
                                    opout.write("   <cool:before>" + tmpState + "</cool:before>\n");
                                } else if (prc.isAfter()) {
                                    opout.write("   <cool:after>" + tmpState + "</cool:after>\n");
                                }
                            }
                            // send package includes
                            if (!prc.getSendSubject().equals("") && !prc.getSendType().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:sends> drf:resource = \"#" + prc.getName() + "_sendPackage\"/>\n");
                            }
                            // received package includes
                            if (!prc.getReceiveSubject().equals("") && !prc.getReceiveType().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:receives> drf:resource = \"#" + prc.getName() + "_receivePackage\"/>\n");

                                // initiator
                                if (prc.isInitiator()) {
                                    opout.write("   <cool:isInitiator>true</cool:isInitiator>\n");
                                } else {
                                    opout.write("   <cool:isInitiator>false</cool:isInitiator>\n");
                                }
                            }
                            // script includes
                            if (!prc.getScriptCommand().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:runs rdf:resource =\"#" + prc.getName() + "_script\"/>\n");
                            }
                            opout.write("\n");
                            opout.write("</rdf:Description>\n");
                            opout.write("\n");

                            // send package description
                            if (!prc.getSendSubject().equals("") && !prc.getSendType().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:Package rdf:ID=\"" + prc.getName() + "_sendPackage\">\n");
                                opout.write("   <cool:hasName>" + prc.getName() + "_sendPackage</cool:hasName>\n");
                                opout.write("   <cool:hasSendSubject>" + prc.getSendSubject() + "</cool:hasSendSubject>\n");
                                opout.write("   <cool:hasSendType>" + prc.getSendType() + "</cool:hasSendType>\n");
                                opout.write("   <cool:hasSendText>" + prc.getSendText() + "</cool:hasSendText>\n");
                                if (prc.isSendRc()) {
                                    opout.write("   <cool:isForRcClient>true</cool:isForRcClient>\n");
                                }
                                opout.write("   </cool:Package>\n");
                            }

                            // received package description
                            if (!prc.getReceiveSubject().equals("") && !prc.getReceiveType().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:Package rdf:ID=\"" + prc.getName() + "_receivePackage\">\n");
                                opout.write("   <cool:hasName>" + prc.getName() + "_receivePackage</cool:hasName>\n");
                                opout.write("   <cool:hasReceivedSubject>" + prc.getReceiveSubject() + "</cool:hasReceivedSubject>\n");
                                opout.write("   <cool:hasReceivedType>" + prc.getReceiveType() + "</cool:hasReceivedType>\n");
                                opout.write("   <cool:hasReceivedText>" + prc.getReceiveText() + "</cool:hasReceivedText>\n");
                                if (prc.isReceiveRc()) {
                                    opout.write("   <cool:isForRcClient>true</cool:isForRcClient>\n");
                                }
                                opout.write("   </cool:Package>\n");
                            }

                            // script description
                            if (!prc.getScriptCommand().equals("")) {
                                opout.write("\n");
                                opout.write("   <cool:Script rdf:ID=\"" + prc.getName() + "_script\">\n");
                                opout.write("   <cool:hasName>" + prc.getName() + "_script</cool:hasName>\n");
                                opout.write("   <cool:hasCommandString>" + prc.getScriptCommand() + "</cool:hasCommandString>\n");
                                if (prc.getExitCode() != 777) {
                                    opout.write("   <cool:hasExitCode>" + prc.getExitCode() + "</cool:hasExitCode>\n");
                                }
                                if (prc.isSync()) {
                                    opout.write("   <cool:isSynchronous>true</cool:isSynchronous>\n");
                                } else {
                                    opout.write("   <cool:isSynchronous>false</cool:isSynchronous>\n");
                                }
                                opout.write("   </cool:Script>\n");
                            }

                            opout.write("\n");
                            opout.write("</rdf:RDF>\n");
                            opout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                // x,y,w,h
                out.write("\n");
                out.write("   <cool:XCo>" + gc.getX() + "</cool:XCo>\n");
                out.write("   <cool:YCo>" + gc.getY() + "</cool:YCo>\n");
                out.write("   <cool:WCo>" + gc.getW() + "</cool:WCo>\n");
                out.write("   <cool:HCo>" + gc.getH() + "</cool:HCo>\n");


                out.write("\n");
                out.write("</rdf:Description>\n");
                out.write("</rdf:RDF>\n");
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    class FileListFilter implements FilenameFilter {
        private String name;

        private String extension;

        public FileListFilter(String name, String extension) {
            this.name = name;
            this.extension = extension;
        }

        public boolean accept(File directory, String filename) {
            boolean fileOK = true;

            if (name != null) {
                fileOK &= filename.startsWith(name);
            }

            if (extension != null) {
                fileOK &= filename.endsWith(extension);
            }
            return fileOK;
        }
    }

    class JCGDeSerializer implements VMethod<JCGComponent> {

        File ap;
        public JCGDeSerializer(File f){
            ap = f;
        }
        @Override
        public JCGComponent execute() {
            return getJCGComponent(ap);
        }
    }

    class JCGSerializer implements VMethod<String> {

        String runType;
        JCGComponent component;
        Set<String> supervisorComponents;
        boolean removeConfDir = false;
        public JCGSerializer(String runType, JCGComponent c, Set<String> cmps, boolean removeFirst){
            this.runType = runType;
            component = c;
            removeConfDir = removeFirst;
            supervisorComponents = cmps;

        }

        @Override
        public String execute() {
            if(component.getName().equals(runType)){
                coolDumpSupervisor(component, supervisorComponents, runType);
            } else {
                coolDumpComponent(runType, component);
            }
            xmlDumpComponent(runType, component);
            return "done";
        }
    }



}
