
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

package org.jlab.coda.cedit.parsers.coolparser;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdql.*;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class JCParser {
    // Jena model
    private Model GModel;

    // List of the included cool configuration file jena models
    private HashMap<String,Model> includeModels = new HashMap<>();

    private JCGSetup stp = JCGSetup.getInstance();

    private ArrayList<FileInputStream> fStreams = new ArrayList<>();

    private Map<String,JCGComponent> componentMap;


    /**
     * Constructor
     * @param fileName of the rdf/cool configuration file
     * @param debug if true prints statements of the cool description
     * @return stat status of construction
     */
    public boolean openFile(String fileName, boolean debug){
        boolean stat;
        stat = createModel(getCoolHome()+"Control"+File.separator+fileName) && generateFinalModel();
        if(stat && debug) printStatements(GModel);
        return stat;
    }

    private String getCoolHome(){
        return stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+"config"+File.separator;
    }

    /**
     * @param name of the control
     */
    public void parseControl(String name){
        JCGComponent duper = new JCGComponent();
        duper.setName(name);
        // parse and add described processes for the control to the supervisor agent
        Set<JCGProcess> processlist = parseProcesses(JCGSetup.COOL_HTTP_BASE+"Control"+
                File.separator+name+
                File.separator+name+"#"+name,"hasProcess");

        if(processlist!=null && !processlist.isEmpty()) {
            duper.setPrcesses(processlist);
        }
        componentMap = parseComponent(JCGSetup.COOL_HTTP_BASE+"Control"+
                  File.separator+name+File.separator+name+"#"+name,"hasComponent");
        componentMap.put(duper.getName(), duper);

    }

    /**
     * @param name of the control supervisor
     */
    public JCGComponent parseControlSupervisor(String name){
        JCGComponent duper = new JCGComponent();
        duper.setName(name);
        // parse and add described processes for the control to the supervisor agent
        Set<JCGProcess> processlist = parseProcesses(JCGSetup.COOL_HTTP_BASE+"Control"+
                File.separator+name+
                File.separator+name+"#"+name,"hasProcess");

        if(processlist!=null && !processlist.isEmpty()) {
            duper.setPrcesses(processlist);
        }
          return duper;
    }

    /**
     * Creates Jena model of the rdf/cool file
     * @param fileName of the rdf/cool configuration file direct path
     * @return stat status of the operation
     */
    private boolean createModel(String fileName){
        FileInputStream fis;
        try {
            fis = new FileInputStream(fileName);
            fStreams.add(fis);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File <"+fileName+">not found");
            return false;
        }
// create the jena model
        Model model = ModelFactory.createDefaultModel();
        model.read(fis, JCGSetup.COOL_HTTP_BASE,"RDF/XML") ;

        // add this model to the model list
        if(!includeModels.containsKey(fileName)){
            includeModels.put(fileName,model);
        }

        // list the statements in the Model
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt    = iter.nextStatement();
            RDFNode node    = stmt.getObject();

            if (node instanceof Resource) {
                if((node.toString().endsWith(".rdf"))){
                    String incName;
                    if(node.toString().contains(JCGSetup.COOL_HTTP_BASE)){
                        incName = replace(node.toString(), JCGSetup.COOL_HTTP_BASE, getCoolHome());
                    }
//                    else if(node.toString().contains(JCGSetup.AFECSDB_HTTP_BASE)){
//                        incName = replace(node.toString(), JCGSetup.AFECSDB_HTTP_BASE, _afecsHomeDb);
//                    }
                    else {
                        return false;
                    }
                    if(!incName.equals("undefined")) createModel(incName);
                }
            }
        }
        return true;
    }


    /**
     * Creates one big jena model including all sub-models.
     * @return stat status of the operation
     */
    private boolean generateFinalModel(){
        boolean stat = false;
        if(!includeModels.isEmpty()){
            // create union of the jena models
            GModel = ModelFactory.createDefaultModel();
            for(String s: includeModels.keySet()){
                GModel = GModel.union(includeModels.get(s));
            }
            stat = true;
        }
        return stat;
    }

    /**
     * Replaces the substring of the given string with the new string
     * @param str given string
     * @param pattern substring
     * @param replace the new string
     * @return result string
     */
    private String replace(String str, String pattern, String replace) {
        int s = 0;
        int e;
        StringBuilder result = new StringBuilder();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * public method returns final model
     * @return union of the all included models
     */
    public Model getModel(){
        return GModel;
    }

    /**
     * Resets the arraylist of all included models
     */
    public void resetModelList(){
        includeModels.clear();
    }


    /**
     * Debugging method prints all the rdf/cool statements of the model
     * @param model Jena model object
     */
    public void printStatements(Model model){
        // list the statements in the Model
        StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();
            Resource subject   = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode   node    = stmt.getObject();
            System.out.println("subject   = "+subject.toString());
            System.out.println("predicate = "+predicate.toString());
            if (node instanceof Resource) {
                System.out.println("resource = "+node.toString());
            } else {
                System.out.println("literal = "+node.toString());
            }
            System.out.println();
        }
    }

    /**
     * Method creates a query from the string and executes it within the
     * jena model and returns the result as a string.
     *
     * @param subject            object of the cool concept
     * @param predicate          String: cool predicate ( for example has timestamp, hasdatatype, etc.)
     * @return  result           string
     */
    private String getValue(Object subject, String predicate){
        Object x = null;
        String sq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                "> <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        try {
            Query query = new Query(sq) ;
            query.setSource(GModel);
            QueryExecution qe = new QueryEngine(query) ;

            QueryResults results = qe.exec() ;
            for ( ; results.hasNext() ; )
            {
                ResultBinding res = (ResultBinding)results.next() ;
                x = res.get("x");
            }
            results.close() ;
        } catch (Exception ex)
        { ex.printStackTrace(System.err) ;}
        if (x == null) {
            return null;
        } else return x.toString();
    }


    /**
     * Method creats a query from the string and executes it within the
     * jena model and returns the result as a list of strings.
     *
     * @param subject            object of the cool concept
     * @param predicate          String: cool predicate ( for example has timestamp, hasdatatype, etc.)
     * @return  result           string
     */
    private ArrayList<String> getValueList(Object subject, String predicate){
        Object x;
        ArrayList<String> l = new ArrayList<String>();

        String sq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                "> <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        try {
            Query query = new Query(sq) ;
            query.setSource(GModel);
            QueryExecution qe = new QueryEngine(query) ;

            QueryResults results = qe.exec() ;
            for ( ; results.hasNext() ; )
            {
                ResultBinding res = (ResultBinding)results.next() ;
                x = res.get("x");
                if(x!=null) l.add(x.toString());
            }
            results.close() ;
        } catch (Exception ex)
        { ex.printStackTrace(System.err) ;}
        return l;
    }


    /**
     * Parsing the component of the control.
     *
     * @param subject           object of the cool concept
     * @param predicate         String of the cool predicate name
     * @return cl               list of {@link org.jlab.coda.cedit.system.JCGComponent} objects
     */

    private Map<String,JCGComponent> parseComponent(Object subject, String predicate) {
        JCGComponent cmp;
        String tmps;

        Map<String,JCGComponent> cl= new HashMap<>();
        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                ">, <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;
        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;
            cmp = new JCGComponent();

            tmps = getValue(x,"hasName");
            if(tmps!=null){
                cmp.setName(tmps);
            }
            else {
                System.out.println("COOL-ERROR: Component "+x+" does not have a name");
                return null;
            }

            tmps = getValue(x,"hasType");
            if(tmps!=null){
                cmp.setType(tmps);
            }

            tmps = getValue(x,"hasCode");
            if(tmps!=null){
                cmp.setCode(tmps);
            }

            tmps = getValue(x,"hasPriority");
            if(tmps!=null){
                try {
                    cmp.setPriority(Integer.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"hasID");
            if(tmps!=null){
                try {
                    cmp.setId(Integer.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"representsCoda2Component");
            if(tmps!=null && tmps.equalsIgnoreCase("true")){
                cmp.setCodaVersion2(true);
            }

            tmps = getValue(x,"isCodaComponent");
            if(tmps!=null && tmps.equalsIgnoreCase("true")){
                cmp.setCodaComponent(true);
            }


            tmps = getValue(x,"XCo");
            if(tmps!=null){
                try {
                    cmp.setX(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"YCo");
            if(tmps!=null){
                try {
                    cmp.setY(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"WCo");
            if(tmps!=null){
                try {
                    cmp.setW(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"HCo");
            if(tmps!=null){
                try {
                    cmp.setH(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // parse processes
            Set<JCGProcess> processes = parseProcesses(x,"hasProcess");
            if(processes!=null && !processes.isEmpty()){
                cmp.setPrcesses(processes);
            }


            // pares links
            Set<JCGLink> links = parseLink(x,"usesLink");
            if(links!=null && !links.isEmpty()){
                cmp.setLnks(links);
            }

            tmps = getValue(x,"hasUserConfig");
            if(tmps!=null){
                cmp.setUserConfig(tmps);
            }


            // parse option
            JCGOption option = parseOption(x,"hasOption");
            if(option!=null){

//                if(!option.getConfigFile().equals("")){
//                    cmp.setConfigFile(option.getConfigFile());
//                }

            }
            cl.put(cmp.getName(), cmp);
        }
        results.close() ;
        return cl;
    }

    /**
     * Method parses the cool option concept
     *
     * @param subject           object of the cool concept
     * @param predicate         String of the cool predicate name
     * @return option           JCGOption object
     */
    private JCGOption parseOption(Object subject, String predicate){
        JCGOption option = null;
        String name, configFile, configString, tmps;

        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                ">, <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;
        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;

            option = new JCGOption();

            name = getValue(x,"hasName");
            if(name!=null){
                option.setName(name);
            } else {
                System.out.println("COOL-ERROR: name is not defined for the option "+x.toString());
                return null;
            }

            configFile = getValue(x,"hasConfigFile");
            if(configFile!=null){
                option.setConfigFile(configFile);
            }

            configString = getValue(x,"hasConfigString");
            if(configString!=null){
                option.setConfigString(configString);
            }

            tmps = getValue(x,"hasDownloadString");
            if(tmps!=null){
                option.setDownloadString(tmps);
            }

            tmps = getValue(x,"hasPrestartString");
            if(tmps!=null){
                option.setPrestartString(tmps);
            }

            tmps = getValue(x,"hasGoString");
            if(tmps!=null){
                option.setGoString(tmps);
            }

            tmps = getValue(x,"hasEndString");
            if(tmps!=null){
                option.setEndString(tmps);
            }



        }
        results.close() ;
        return option;
    }


    private Set<JCGLink> parseLink(Object subject, String predicate){
        JCGLink link;
        String tmps;
        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                ">, <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;

        Set<JCGLink> al = Collections.synchronizedSet(new HashSet<JCGLink>());

        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;

            link = new JCGLink();

            tmps = getValue(x,"hasName");
            if(tmps!=null){
                link.setName(tmps);
            } else {
                System.out.println("COOL-ERROR: name is not defined for the linkPoint "+x.toString());
                return null;
            }


            tmps = getValue(x,"sourceComponentName");
            if(tmps!=null){
                link.setSourceComponentName(tmps);
            }


            tmps = getValue(x,"destinationComponentName");
            if(tmps!=null){
                link.setDestinationComponentName(tmps);
            }

            tmps = getValue(x,"startX");
            if(tmps!=null){
                try {
                    link.setStartX(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"startY");
            if(tmps!=null){
                try {
                    link.setStartY(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"endX");
            if(tmps!=null){
                try {
                    link.setEndX(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmps = getValue(x,"endY");
            if(tmps!=null){
                try {
                    link.setEndY(Double.valueOf(tmps));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            al.add(link);

        }
        results.close() ;
        return al;
    }

    private Set<JCGProcess> parseProcesses(Object subject, String predicate) {
        JCGProcess process;
        JCGScript  script;
        JCGPackage sendPackage;
        JCGPackage receivePackage;
        String     tmpS;
        Set<JCGProcess> pl = Collections.synchronizedSet(new HashSet<JCGProcess>());

        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                "> <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;
        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;
            process = new JCGProcess();

            tmpS = getValue(x,"hasName");
            if(tmpS!=null){
                process.setName(tmpS);
            } else {
                System.out.println("COOL-ERROR: name is not defined for the Process "+x.toString());
                return null;
            }

            tmpS = getValue(x,"isSync");
            if(tmpS!=null && tmpS.equals("true")){
                process.setSync(true);
            }

            tmpS = getValue(x,"before");
            if(tmpS!=null){
                process.setBefore(true);

                String tmpTransition = null;
                switch (tmpS) {
                    case "downloaded":
                        tmpTransition = "download";
                        break;
                    case "prestarted":
                        tmpTransition = "prestart";
                        break;
                    case "active":
                        tmpTransition = "go";
                        break;
                    case "ended":
                        tmpTransition = "end";
                        break;
                }
                process.setTransition(tmpTransition);
            }

            tmpS = getValue(x,"after");
            if(tmpS!=null){
                process.setAfter(true);
                String tmpTransition = null;
                switch (tmpS) {
                    case "downloaded":
                        tmpTransition = "download";
                        break;
                    case "prestarted":
                        tmpTransition = "prestart";
                        break;
                    case "active":
                        tmpTransition = "go";
                        break;
                    case "ended":
                        tmpTransition = "end";
                        break;
                }
                process.setTransition(tmpTransition);
            }

            tmpS = getValue(x,"hasPeriodicity");
            if(tmpS!=null){
                try {
                    process.setPeriod(Integer.parseInt(tmpS));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            tmpS = getValue(x,"isInitiator");
            if(tmpS!=null){
                process.setInitiator(true);
            }

            script = parseScript(x,"runs");
            if(script!=null) {
                process.setScriptCommand(script.getCommandString());
                process.setExitCode(script.getExitCode());
            }

            sendPackage = parsePackage(x,"sends");
            if(sendPackage!=null) {
                process.setSendSubject(sendPackage.getSendSubject());
                process.setSendType(sendPackage.getSendType());
                process.setSendText(sendPackage.getSendText());
                process.setSendRc(sendPackage.isRc());
            }

            receivePackage = parsePackage(x,"receives");
            if(receivePackage!=null) {
                process.setReceiveSubject(receivePackage.getReceivedSubject());
                process.setReceiveType(receivePackage.getReceivedType());
                process.setReceiveText(receivePackage.getReceivedText());
                process.setReceiveRc(receivePackage.isRc());
            }

            pl.add(process);
        }
        results.close() ;
        return pl;
    }

    private JCGPackage parsePackage(Object subject, String predicate){
        JCGPackage pk = null;
        String name;
        String tmp;

        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                ">, <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;
        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;
            pk = new JCGPackage();

            name = getValue(x,"hasName");
            if(name!=null){
                pk.setName(name);
            } else {
                System.out.println("COOL-ERROR: name is not defined for the package "+x.toString());
                return null;
            }


            tmp = getValue(x,"hasSendSubject");
            if(tmp!=null){
                pk.setSendSubject(tmp);
            }

            tmp = getValue(x,"hasSendType");
            if(tmp!=null){
                pk.setSendType(tmp);
            }

            tmp = getValue(x,"hasSendText");
            if(tmp!=null){
                pk.setSendText(tmp);
            }

            tmp = getValue(x,"hasReceivedSubject");
            if(tmp!=null){
                pk.setReceivedSubject(tmp);
            }

            tmp = getValue(x,"hasReceivedType");
            if(tmp!=null){
                pk.setReceivedType(tmp);
            }

            tmp = getValue(x,"hasReceivedText");
            if(tmp!=null){
                pk.setReceivedText(tmp);
            }

            tmp = getValue(x,"isForRcClient");
            if(tmp!=null){
                pk.setRc(true);
            }

        }
        results.close() ;
        return pk;
    }

    private JCGScript parseScript(Object subject, String predicate){
        JCGScript sc = null;
        String tmp;

        String tq = "SELECT ?x "+ "WHERE(<"+subject.toString()+
                ">, <"+ JCGSetup.COOL_CORE+""+predicate+">,?x )";
        Query query = new Query(tq) ;
        query.setSource(GModel);
        QueryExecution qe = new QueryEngine(query) ;

        QueryResults results = qe.exec() ;
        for ( ; results.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)results.next() ;
            Object x = res.get("x");
            if(x==null)break;
            sc = new JCGScript();

            tmp = getValue(x,"hasName");
            if(tmp!=null){
                sc.setName(tmp);
            } else {
                System.out.println("COOL-ERROR: name is not defined for the script "+x.toString());
                return null;
            }

            tmp = getValue(x,"hasCommandString");
            if(tmp!=null){
                sc.setCommandString(tmp);
            }

            tmp = getValue(x,"hasExitCode");
            if(tmp!=null){
                try{
                sc.setExitCode(Integer.parseInt(tmp));
                } catch(NumberFormatException e){
                    System.out.println(e.getMessage());
                }
            }

            tmp = getValue(x,"isSynchronous");
            if(tmp!=null){
                sc.setSync(true);
            }

        }
        results.close() ;
        return sc;
    }

    public void close(){
        for(FileInputStream is: fStreams){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, JCGComponent> getComponentMap() {
        return componentMap;
    }
}
