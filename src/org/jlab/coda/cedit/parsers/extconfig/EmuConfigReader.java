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

import org.jlab.coda.cedit.system.JCGCompConfig;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGSetup;
import org.jlab.coda.cedit.system.JCGConcept;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class EmuConfigReader {

    private String fileName;

    private JCGComponent component;

    private MyHandler handler = new MyHandler();
    private JCGCompConfig _cConfig;
    private JCGSetup stp = JCGSetup.getInstance();


    public EmuConfigReader(String runType, String compName) {
        fileName = stp.getCoolHome() + File.separator +
                stp.getExpid() + File.separator +
                "config" + File.separator +
                "Control" + File.separator +
                runType + File.separator +
                "Options" + File.separator +
                compName + ".xml";
    }

    public EmuConfigReader(String runType, JCGComponent comp){
        this.component = comp;
        fileName = stp.getCoolHome()+ File.separator+
                stp.getExpid()+File.separator+
                "config"+File.separator+
                "Control"+File.separator +
                runType+File.separator+
                "Options"+File.separator+
                component.getName()+".dat";
    }


    public boolean isConfigExists() {
        return new File(fileName).exists();
    }

    public long getLastModified() {
        return new File(fileName).lastModified();
    }

    public String getFileName(){
        return fileName;
    }

    public JCGCompConfig parseConfig() {
        _cConfig = new JCGCompConfig();

        try {
            // Create a builder factory
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // Create the builder and parse the file
            factory.newSAXParser().parse(new File(fileName), handler);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _cConfig;
    }

    public void parseConfig(JCGComponent com){
    }

    /**
     * This class listens for startElement SAX events
     */
    private class MyHandler extends DefaultHandler {
        // This method is called when an element is encountered
        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) {
            // Get the number of attribute
            int length = atts.getLength();

//System.out.println("Start Element :" + qName);

            // Process each attribute
            for (int i = 0; i < length; i++) {

                // Get names and values for each attribute
                String name = qName+":"+atts.getQName(i);
                String value = atts.getValue(i);
//System.out.println( name+" "+value);
                _cConfig.addConcept(name,value);

            }
        }

        public void endElement(String uri, String localName,
                               String qName) throws SAXException {
//System.out.println("End Element :" + qName);

        }


    }

    public static void main(String[] args){
        EmuConfigReader rd = new EmuConfigReader(args[0], args[1]);
        System.out.println("Configuration for "+args[1]+": file = "+rd.getFileName()+" exists = "+rd.isConfigExists());
        System.out.println("File was last modified = "+rd.getLastModified());
        if(rd.isConfigExists()){
            JCGCompConfig cf = rd.parseConfig();
            Set<JCGConcept> rc = cf.getConfigData();
            for(JCGConcept s:rc){
                System.out.println(s.getName()+" = "+s.getValue());
            }
        }
    }

}
