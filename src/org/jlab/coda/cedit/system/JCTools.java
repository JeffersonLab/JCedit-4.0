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

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Describe...
 *
 * @author gurjyan
 *         Date: 1/13/14 Time: 8:45 AM
 * @version 2
 */
public class JCTools {

    public static JCGSetup stp = JCGSetup.getInstance();

    public static void deleteDir(File file) {

        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length<=0){

                file.delete();

            }else{

                //list all the directory contents
                String[] files = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteDir(fileDelete);
                }

                //check the directory again, if empty then delete it
                if(file.list().length<=0){
                    file.delete();
                }
            }

        }else{
            file.delete();
        }

    }

    /**
     * Pops up a dialog box with three buttons: reset, abort, cancel
     * @return int indicating button selection
     */
    public static int popConfirmationDialog(String msg1, String msg2, boolean auto){
        String[] choices = {"Ok", "Cancel"};
        if(auto){
            return JOptionPane.showOptionDialog(
                    null
                    , "Are you sure you want to " + msg1 + "? \n" + msg2
                    , "COOL Confirmation"
                    , JOptionPane.YES_NO_OPTION
                    , JOptionPane.PLAIN_MESSAGE
                    , null
                    , choices
                    , "Cancel"
            );
        } else {
            return JOptionPane.showOptionDialog(
                    null
                    , msg1+" \n"+msg2
                    , "COOL Confirmation"
                    , JOptionPane.YES_NO_OPTION
                    , JOptionPane.PLAIN_MESSAGE
                    , null
                    , choices
                    , "Cancel"
            );
        }
    }


    public static void walkDirAndDelete(File dir, Pattern pattern) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    walkDirAndDelete(file, pattern);
                } else if (pattern.matcher(file.getName()).matches()) {
                    file.delete();
//                    System.out.println("file to delete: " + file.getAbsolutePath());
                }
            }
        }
    }

    public static int isNumber(String s){
        int res = -1;
        try{
            res = Integer.parseInt(s);
        } catch (NumberFormatException e){
            return -1;
        }
        return res;
    }

    public static void showInfo(String txt){
        JOptionPane.showMessageDialog(null,txt,"Info",JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showWarning(String txt){
        JOptionPane.showMessageDialog(null,txt,"Warning",JOptionPane.WARNING_MESSAGE);
    }
    public static void showError(String txt){
        JOptionPane.showMessageDialog(null,txt,"Error",JOptionPane.ERROR_MESSAGE);
    }
    public static List<CDefinedComponent> parseUserCompDef(String fileName) throws Exception {
        List<CDefinedComponent> pcomps;
        //Get the DOM Builder Factory
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        //Get the DOM Builder
        DocumentBuilder builder = factory.newDocumentBuilder();
        //Load and Parse the XML document
        //document contains the complete XML as a Tree.
        FileInputStream is = new FileInputStream(new File(fileName));
        Document document = builder.parse(is);
        pcomps = new ArrayList<CDefinedComponent>();
        //Iterating through the nodes and extracting the data.
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            //We have encountered an <component> tag.
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                CDefinedComponent comp = new CDefinedComponent();
                String id = node.getAttributes().getNamedItem("id").getNodeValue();
                if(JCTools.isNumber(id)>=0){
                    comp.setId(JCTools.isNumber(id));
                } else {
                    showError("Malformed coda component description xml file. " +
                            "\nComponent id is not an integer.");
                    return null;
                }
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);
                    //Identifying the child tag of component encountered.
                    if (cNode instanceof Element) {
                        String content = cNode.getLastChild().getTextContent().trim();
                        if(cNode.getNodeName().equals("name")) {
                            comp.setName(content);
                        } else if(cNode.getNodeName().equals("name_prefix")) {
                            if(comp.getName().equals("undefined")){
                                // construct name as name_prefix+id
                                comp.setName(content+comp.getId());
                            }
                        } else if(cNode.getNodeName().equals("type")) {
                            comp.setType(content);
                        } else if(cNode.getNodeName().equals("description")) {
                            comp.setDescription(content);
                        }
                    }
                }
                pcomps.add(comp);
            }
        }
        return pcomps;
    }

    public static List<CDefinedComponent> getDescCompDatabase(){
        JCGSetup stp = JCGSetup.getInstance();
        String s;

        StringTokenizer st1,st2;
        String name = "undefined";
        String type = "undefined";
        String sType = "undefined";
        String desc = "undefined";
        int id = -1;

        List<CDefinedComponent> res = new ArrayList<CDefinedComponent>();
        for(ACodaType c:ACodaType.values()){
            if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                    "jcedit"+File.separator+c.name()+".txt").exists()){
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+c.name()+".txt"));

                    StringBuilder sb = new StringBuilder();

                    // read entire file
                    while((s = br.readLine())!=null){
                        sb.append(s);
                        if(!s.endsWith("@@")){
                            sb.append("\n");
                        }
                    }

                    // get single component record
                    st1 = new StringTokenizer(sb.toString(),"@@");
                    while(st1.hasMoreTokens()){
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(),"$");
                        if(st2.hasMoreTokens()) name  = st2.nextToken();
                        if(st2.hasMoreTokens()) type  = st2.nextToken();
                        if(st2.hasMoreTokens()) sType = st2.nextToken();
                        try{
                            if(st2.hasMoreTokens()) id  = Integer.parseInt(st2.nextToken());
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                        }
                        if(st2.hasMoreTokens()) desc  = st2.nextToken();
                        res.add(new CDefinedComponent(name, type, sType, id, desc));
                    }
                    br.close();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return res;
    }

    public static boolean isNameUnique(List<CDefinedComponent> pcomps){
        List<String> names = new ArrayList<String>();

        for(CDefinedComponent p:pcomps){
            names.add(p.getName());
        }

        Set<String> unique = new HashSet<String>(names);

        for(String tmp:unique){
            if(Collections.frequency(names, tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nname conflict (name = " + tmp + ").");
                return false;
            }
        }
        return true;
    }

    // check to see if ID's are unique within the type
    public static boolean isIdUnique(List<CDefinedComponent> pcomps){
        List<Integer> rtgIds = new ArrayList<Integer>();
        List<Integer> dcIds = new ArrayList<Integer>();
        List<Integer> pebIds = new ArrayList<Integer>();
        List<Integer> sebIds = new ArrayList<Integer>();
        List<Integer> eberIds = new ArrayList<Integer>();
        List<Integer> erIds = new ArrayList<Integer>();
        List<Integer> slcIds = new ArrayList<Integer>();
        List<Integer> usrIds = new ArrayList<Integer>();
        List<Integer> outIds = new ArrayList<Integer>();

        for(CDefinedComponent p:pcomps){
            String t = p.getType();
            if(t.equals(ACodaType.ROC.name()) ||
                    t.equals(ACodaType.GT.name()) ||
                    t.equals(ACodaType.FPGA.name()) ||
                    t.equals(ACodaType.TS.name())){
                rtgIds.add(p.getId());
            } else if(t.equals(ACodaType.DC.name())){
                dcIds.add(p.getId());
            } else if(t.equals(ACodaType.PEB.name())){
                pebIds.add(p.getId());
            } else if(t.equals(ACodaType.SEB.name())){
                sebIds.add(p.getId());
            } else if(t.equals(ACodaType.EBER.name())){
                eberIds.add(p.getId());
            } else if(t.equals(ACodaType.ER.name())){
                erIds.add(p.getId());
            } else if(t.equals(ACodaType.SLC.name())){
                slcIds.add(p.getId());
            } else if(t.equals(ACodaType.USR.name())){
                usrIds.add(p.getId());
            } else if(t.equals(ACodaType.FILE.name())){
                outIds.add(p.getId());
            }
        }

        Set<Integer> unique = new HashSet<Integer>(rtgIds);

        for(int tmp:unique){
            if(Collections.frequency(rtgIds, tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = ROC/GT/TS components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(dcIds);
        for(int tmp:unique){
            if(Collections.frequency(dcIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = DC components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(pebIds);
        for(int tmp:unique){
            if(Collections.frequency(pebIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = "+tmp+" for type = PEB components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(sebIds);
        for(int tmp:unique){
            if(Collections.frequency(sebIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = SEB components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(eberIds);
        for(int tmp:unique){
            if(Collections.frequency(eberIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = FCS components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(erIds);
        for(int tmp:unique){
            if(Collections.frequency(erIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = ER components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(slcIds);
        for(int tmp:unique){
            if(Collections.frequency(slcIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = SLC components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(usrIds);
        for(int tmp:unique){
            if(Collections.frequency(usrIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = USR components.");
                return false;
            }
        }

        unique = new HashSet<Integer>(outIds);
        for(int tmp:unique){
            if(Collections.frequency(outIds,tmp)>1) {
                JCTools.showError("Malformed coda component description xml. " +
                        "\nDuplicate id = " + tmp + " for type = FILE components.");
                return false;
            }
        }
        return true;
    }

    public static HashMap<String, CDefinedComponent> getPredefinedComponents(String type){
        JCGSetup stp = JCGSetup.getInstance();
        String s;
        StringTokenizer st1,st2;
        String _name = "undefined";
        String _type = "undefined";
        String _sType = "undefined";
        String _desc = "undefined";
        int id = -1;


        HashMap<String,CDefinedComponent> res = new HashMap<String, CDefinedComponent>();

        if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                "jcedit"+File.separator+type+".txt").exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                        File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+type+".txt"));
                StringBuilder sb = new StringBuilder();
                // read entire file
                while((s = br.readLine())!=null){
                    sb.append(s);
                    if(!s.endsWith("@@")){
                        sb.append("\n");
                    }
                }
                // get single component record
                st1 = new StringTokenizer(sb.toString(),"@@");
                while(st1.hasMoreTokens()){
                    // get data
                    st2 = new StringTokenizer(st1.nextToken(),"$");
                    if(st2.hasMoreTokens()) _name  = st2.nextToken();
                    if(st2.hasMoreTokens()) _type  = st2.nextToken();
                    if(st2.hasMoreTokens()) _sType = st2.nextToken();
                    try{
                        if(st2.hasMoreTokens()) id  = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException e){
                        System.out.println(e.getMessage());
                    }
                    if(st2.hasMoreTokens()) _desc  = st2.nextToken();
                    res.put(_name, new CDefinedComponent(_name, _type, _sType, id, _desc));
                }
                br.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return res;
    }
    public static HashMap<String,Integer> getPredefinedIds(String t){
        String s;
        StringTokenizer st1, st2;
        StringBuilder sb = new StringBuilder();

        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id;
        JCGSetup stp = JCGSetup.getInstance();
        HashMap<String,Integer> res = new HashMap<>();
        if(t.equals(ACodaType.ROC.name()) ||
                t.equals(ACodaType.GT.name()) ||
                t.equals(ACodaType.FPGA.name()) ||
                t.equals(ACodaType.TS.name())
                ){
            if(new File(stp.getCoolHome()+
                    File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.ROC.name()+".txt").exists()){
                try {

                    // roc
                    BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome() +
                            File.separator + stp.getExpid() + File.separator + "jcedit" + File.separator + ACodaType.ROC.name() + ".txt"));

                    // read entire file
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                        if (!s.endsWith("@@")) {
                            sb.append("\n");
                        }
                    }
                    // get single component record
                    st1 = new StringTokenizer(sb.toString(), "@@");
                    while (st1.hasMoreTokens()) {
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(), "$");
                        if (st2.hasMoreTokens()) name = st2.nextToken();
                        if (st2.hasMoreTokens()) type = st2.nextToken();
                        if (st2.hasMoreTokens()) sType = st2.nextToken();

                        try {
                            if (st2.hasMoreTokens()) {
                                id = Integer.parseInt(st2.nextToken());
                                res.put(name, id);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(e.getMessage());
                        }

                        if (st2.hasMoreTokens()) desc = st2.nextToken();

                    }
                    br.close();
                } catch (FileNotFoundException e) {

                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            if(new File(stp.getCoolHome()+
                    File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.GT.name()+".txt").exists()){
                try {

                    //gt
                    BufferedReader  br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.GT.name()+".txt"));

                    // read entire file
                    while((s = br.readLine())!=null){
                        sb.append(s);
                        if(!s.endsWith("@@")){
                            sb.append("\n");
                        }
                    }
                    // get single component record
                    st1 = new StringTokenizer(sb.toString(),"@@");
                    while(st1.hasMoreTokens()){
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(),"$");
                        if(st2.hasMoreTokens()) name  = st2.nextToken();
                        if(st2.hasMoreTokens()) type  = st2.nextToken();
                        if(st2.hasMoreTokens()) sType = st2.nextToken();

                        try{
                            if(st2.hasMoreTokens()) {
                                id    = Integer.parseInt(st2.nextToken());
                                res.put(name,id);
                            }
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                        }

                        if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    }
                    br.close();
                } catch (FileNotFoundException e) {

                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            if(new File(stp.getCoolHome()+
                    File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.FPGA.name()+".txt").exists()){
                try {

                    //fpga
                    BufferedReader  br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.FPGA.name()+".txt"));

                    // read entire file
                    while((s = br.readLine())!=null){
                        sb.append(s);
                        if(!s.endsWith("@@")){
                            sb.append("\n");
                        }
                    }
                    // get single component record
                    st1 = new StringTokenizer(sb.toString(),"@@");
                    while(st1.hasMoreTokens()){
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(),"$");
                        if(st2.hasMoreTokens()) name  = st2.nextToken();
                        if(st2.hasMoreTokens()) type  = st2.nextToken();
                        if(st2.hasMoreTokens()) sType = st2.nextToken();

                        try{
                            if(st2.hasMoreTokens()) {
                                id    = Integer.parseInt(st2.nextToken());
                                res.put(name,id);
                            }
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                        }

                        if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    }
                    br.close();
                } catch (FileNotFoundException e) {

                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            if(new File(stp.getCoolHome()+
                    File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.TS.name()+".txt").exists()){
                try {

                    //ts
                    BufferedReader  br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+ACodaType.TS.name()+".txt"));

                    // read entire file
                    while((s = br.readLine())!=null){
                        sb.append(s);
                        if(!s.endsWith("@@")){
                            sb.append("\n");
                        }
                    }
                    // get single component record
                    st1 = new StringTokenizer(sb.toString(),"@@");
                    while(st1.hasMoreTokens()){
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(),"$");
                        if(st2.hasMoreTokens()) name  = st2.nextToken();
                        if(st2.hasMoreTokens()) type  = st2.nextToken();
                        if(st2.hasMoreTokens()) sType = st2.nextToken();

                        try{
                            if(st2.hasMoreTokens()) {
                                id    = Integer.parseInt(st2.nextToken());
                                res.put(name,id);
                            }
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                        }

                        if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    }
                    br.close();
                    br.close();
                } catch (FileNotFoundException e) {

                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }


        } else {
            if(new File(stp.getCoolHome()+
                    File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+t+".txt").exists()) {
                // non roc components
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome() +
                            File.separator + stp.getExpid() + File.separator + "jcedit" + File.separator + t + ".txt"));

                    // read entire file
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                        if (!s.endsWith("@@")) {
                            sb.append("\n");
                        }
                    }
                    // get single component record
                    st1 = new StringTokenizer(sb.toString(), "@@");
                    while (st1.hasMoreTokens()) {
                        // get data
                        st2 = new StringTokenizer(st1.nextToken(), "$");
                        if (st2.hasMoreTokens()) name = st2.nextToken();
                        if (st2.hasMoreTokens()) type = st2.nextToken();
                        if (st2.hasMoreTokens()) sType = st2.nextToken();

                        try {
                            if (st2.hasMoreTokens()) {
                                id = Integer.parseInt(st2.nextToken());
                                res.put(name, id);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(e.getMessage());
                        }

                        if (st2.hasMoreTokens()) desc = st2.nextToken();

                    }
                    br.close();
                } catch (FileNotFoundException e) {

                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return res;
    }


    public static boolean isComponentNamePredefined(String n, String t){
        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id = 0;
        String s;
        StringTokenizer st1, st2;
        StringBuilder sb = new StringBuilder();
        JCGSetup stp = JCGSetup.getInstance();
        if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                "jcedit"+File.separator+t+".txt").exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                        File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+t+".txt"));

                // read entire file
                while((s = br.readLine())!=null){
                    sb.append(s);
                    if(!s.endsWith("@@")){
                        sb.append("\n");
                    }
                }
                // get single component record
                st1 = new StringTokenizer(sb.toString(),"@@");
                while(st1.hasMoreTokens()){
                    // get data
                    st2 = new StringTokenizer(st1.nextToken(),"$");
                    if(st2.hasMoreTokens()) name  = st2.nextToken();
                    if(st2.hasMoreTokens()) type  = st2.nextToken();
                    if(st2.hasMoreTokens()) sType = st2.nextToken();

                    try{
                        if(st2.hasMoreTokens()) id    = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException e){
                        System.out.println(e.getMessage());
                    }

                    if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    if(name.equals(n)){
                        br.close();
                        return true;
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Checks existence of undefined rtv in the string
     * (i.e. checks for existence of "%(" in the string)
     * @param input original input string
     * @return true or false
     */
    public static boolean containsRTV(String input){
        return input.contains("%(");
    }

    public static void getRTVsInAString(String input, List<String> l){
        if(containsRTV(input)){
            StringTokenizer st = new StringTokenizer(input,"%(");

            while(st.hasMoreTokens()){
                String tmp = st.nextToken();
                if(tmp!=null && tmp.contains(")")){
                    String tmp2 = tmp.substring(0, tmp.indexOf(")"));
                    String rtv = "%("+tmp2+")";
                    if(!l.contains(rtv)) l.add(rtv);
                }
            }
        }
    }

    public static JCGComponent deepCpComp (JCGComponent c){
        return new JCGComponent(c.getX(), c.getY(),c.getGridX(), c.getGridY(),
                c.getW(), c.getH(), c.getPx(), c.getPy(), c.getId(), c.getName(),
                c.getType(), c.getSubType(), c.getRol1(), c.getRol1UsrString(),
                c.getRol2(), c.getRol2UsrString(), c.getDescription(),
                c.getUserConfig(), c.getPriority(), c.isCodaComponent(),
                c.isCodaVersion2(), c.isPreDefined(), c.getNodeList(),
                c.getCommand(), c.isMaster(), c.getModule(), c.getLnks(),
                c.getTrnsports(), c.getPrcesses(), c.getImage());
    }

    public static JCGLink deepCpLink (JCGLink c){
        return new JCGLink(c.getSourceEndian(), c.getSourceCapacity(), c.getSourceGroup(), c.getSourceStationName(),
                c.getSourcePosition(), c.isSourceIdFilter(), c.getSourceSendBuffer(), c.getSourceRecvBuffer(),
                c.isSourceNoDelay(), c.getSourceOThreads(), c.getSourceWThreads(), c.getSourceTransportName(),
                c.getSourceModuleName(), c.getSourceComponentName(), c.getSourceComponentType(), c.getDestinationEndian(),
                c.getDestinationCapacity(), c.getDestinationGroup(), c.getDestinationStationName(), c.getDestinationPosition(),
                c.isDestinationIdFilter(), c.getDestinationSendBuffer(), c.getDestinationRecvBuffer(), c.isDestinationNoDelay(),
                c.getDestinationIThreads(), c.getDestinationTransportName(), c.getDestinationModuleName(),
                c.getDestinationComponentName(), c.getDestinationComponentType(), c.getName(),
                c.getStartX(), c.getEndX(), c.getStartY(), c.getEndY());

    }

    public static JCGTransport deepCpTransport (JCGTransport c){
        return new JCGTransport(c.getName(), c.getTransClass(), c.getEtName(), c.isEtCreate(), c.getEtTcpPort(),
                c.getEtUdpPort(), c.getEtWait(), c.getmAddress(), c.getEtEventNum(), c.getEtEventSize(), c.getEtChunkSize(),
                c.getInputEtChunkSize(),c.getSingle(), c.getEtGroups(), c.getEtRecvBuf(), c.getEtSendBuf(), c.getEtMethodCon(),
                c.getEtHostName(),
                c.getEtSubNet(), c.getDestinationEtCreate(), c.getEmuDirectPort(), c.getEmuMaxBuffer(), c.getEmuWait(),
                c.getEmuSubNet(), c.isEmuFatPipe(),
                c.getcMsgHost(), c.getcMsgPort(), c.getcMsgNameSpace(), c.getcMsgSubject(), c.getcMsgType(),
                c.getFileName(), c.getFileType(), c.getFileSplit(), c.getFileInternalBuffer(), c.isNoLink(),
                c.getCompression(), c.getCompressionThreads());
    }

    public static JCGProcess deepCpProcess (JCGProcess c) {
        return new JCGProcess(c.getName(), c.isSync(), c.isPeriodic(), c.getPeriod(), c.getTransition(),
                c.isBefore(), c.isAt(), c.isAfter(), c.getScriptCommand(), c.getExitCode(), c.getSendSubject(),
                c.getSendType(), c.getSendText(), c.isSendRc(), c.getReceiveSubject(), c.getReceiveType(),
                c.getReceiveText(), c.isReceiveRc(), c.isInitiator());
    }


    public static boolean fCheckCreate(File f){
        boolean suc = true;
        if(!f.exists()){
            suc = f.mkdir();
            if(!suc){
                System.out.println("Error: Cannot create f = "+f);
            }
        }
        return suc;
    }

    public static String defineExpIDDire(){
        JCGSetup stp = JCGSetup.getInstance();
        return stp.getCoolHome()+File.separator+stp.getExpid()+File.separator+"config"+File.separator+"Control";
    }


}
