
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

package org.jlab.coda.cedit.cooldesktop;


import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;

import org.jlab.coda.cedit.forms.simple.RefactorForm;
import org.jlab.coda.cedit.forms.simple.SupervisorForm;
import org.jlab.coda.cedit.forms.util.JCListDialog;
import org.jlab.coda.cedit.parsers.coolparser.JCParser;
import org.jlab.coda.cedit.parsers.extconfig.*;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.util.*;

/**
 * @author gurjyan
 */
public class CDesktop extends JFrame {
    private static DrawingCanvas drawingCanvas;
    private double w = 80, h = 80;

    private CDesktop me;
    private static String runType = "undefined";

    private CCanvas cnvs;

    private static CoolDatabaseBrowser coolDbBrowser = new CoolDatabaseBrowser();

    private DefaultListModel listModel;

    private ImageIcon[] images;
    private String[] componentStrings = {
            ACodaType.TS.name(),
            ACodaType.GT.name(),
            ACodaType.ROC.name(),
            ACodaType.DC.name(),
            ACodaType.PEB.name(),
            ACodaType.SEB.name(),
            ACodaType.FCS.name(),
            ACodaType.ER.name(),
            ACodaType.SLC.name(),
            ACodaType.USR.name(),
            ACodaType.FILE.name(),
    };

    private String selectedType;

    public static JCGSetup stp = JCGSetup.getInstance();

    private static HashMap<String,Long> configRecordedTimes = new HashMap<>();

    // Indicates if we need to update other runType supervisor processes.
    // This is in the case when defined processes for the current supervisor
    // is requested to be applicable for the list of other supervisors
    public static boolean updateManySupervisors = false;

    // The list of supervisor names that process updates must be applied to.
    public static List<String> supervisorList = new ArrayList<>();

    private JCParser rdfParser;

    private Map<String, JCGComponent> serCompMap = new HashMap<>();

    public CDesktop() {
        initComponents();
        expidLabel.setText(stp.getExpid());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
                System.exit(1);
            }
        });
        me = this;
        setTitle("COOL Database Editor v4.0   db = "+stp.getCoolHome()+"                                                                                                      ");
        setSize(1100, 850);
        setLocationByPlatform(true);
        rdfParser = new JCParser();
    }

    private void disableMenue(){
        menu1.setEnabled(false);
        menu8.setEnabled(false);
    }

    private void enableMenue(){
        menu1.setEnabled(true);
        menu8.setEnabled(true);
    }


    public static DrawingCanvas getDrawingCvanvas(){
        return drawingCanvas;
    }

    /**
     * Define mastership
     * @param type of the component
     * @param gc graphics component object
     * @return
     */
    static boolean defineRocMastership(String name, String type, JCGComponent gc){
        if(drawingCanvas.getGCMPs().isEmpty()){
            if(type.equals(ACodaType.TS.name()) ||
                    type.equals(ACodaType.GT.name())||
                    type.equals(ACodaType.ROC.name())){
                gc.setMaster(true);
            }
        } else {

            //check to see if component is TS and there is already TS defined in the configuration
            if(type.equals(ACodaType.TS.name())){
                if(CDesktop.containsTs(drawingCanvas)!=null) {
                    return false;
                } else {
                    // reset all already defined components isMaster
                    CDesktop.resetMaster(drawingCanvas);

                    // set the TS as a master
                    gc.setMaster(true);
                }

                // check to see if the component is GT and there is already GT or TS defined
            } else if(type.equals(ACodaType.GT.name())){
                if(CDesktop.containsGt(drawingCanvas)!=null){
                    return false;
                } else {
                    if(CDesktop.containsTs(drawingCanvas)!=null){
                        // ts should be master
                        gc.setMaster(false);
                    } else {
                        // reset all already defined components isMaster
                        CDesktop.resetMaster(drawingCanvas);

                        // if we do not have a ts gt should be master
                        gc.setMaster(true);
                    }
                }
            } else if(type.equals(ACodaType.ROC.name())){
                if(CDesktop.containsTs(drawingCanvas)!=null || CDesktop.containsGt(drawingCanvas)!=null){
                    gc.setMaster(false);
                } else if(CDesktop.getNumberOfRocs(drawingCanvas)==0){
                    gc.setMaster(true);
                } else {
                    gc.setMaster(false);
                }
            }
        }
        return true;
    }

    private static boolean checkReassignMastership(){
        JCGComponent com = CDesktop.containsTs(drawingCanvas);
        if(com !=null ) {
            CDesktop.resetMaster(drawingCanvas);
            com.setMaster(true);
            return true;
        } else {
            com = CDesktop.containsGt(drawingCanvas);
            if(com !=null) {
                CDesktop.resetMaster(drawingCanvas);
                com.setMaster(true);
                return true;
            }
        }
        return false;
    }

    private static void checkReassignEmuSocketTransportPort(){
        Set<Integer> _ports = new HashSet<>();
        Map<String,JCGTransport> _transports = new HashMap<>();
        for(JCGComponent c: drawingCanvas.getGCMPs().values()) {
            for(JCGTransport t: c.getTrnsports()) {
                _transports.put(c.getName(), t);
            }
        }
        for(JCGTransport tt:_transports.values()) {
            if (tt.getTransClass().equals("EmuSocket") ||
                    tt.getTransClass().equals("EmuSocket+Et")) {
                int xp = tt.getEmuDirectPort();
                while (!_ports.add(xp)) {
                    xp = xp + 1;
                }
                tt.setEmuDirectPort(xp);
            }
        }
     }

    /**
     * Assigns a unique id to a component
     * @param type of the component
     * @return unique id for the component
     */
    public static int assignUniqueId(String type){

         List<Integer> ids = new ArrayList<>();
        for(JCGComponent c: drawingCanvas.getGCMPs().values()){
            if(type.equals(c.getType())){
                ids.add(c.getId());
            }
        }
        if (JCTools.getPredefinedIds(type).isEmpty()){

            return 1;
        } else {
            // open predefined component file and add predefined ids for a specific type.
            ids.addAll(JCTools.getPredefinedIds(type).values());
            return Collections.max(ids) + 1;
        }
    }


    private static String defineExpIDDire(){
        return stp.getCoolHome()+File.separator+stp.getExpid()+File.separator+"config"+File.separator+"Control";
    }

    public DrawingCanvas getDrawingCanvas(){
        return drawingCanvas;
    }

    public JLabel getConfigNameLabel() {
        return configNameLabel;
    }

    public CCanvas getCCanvas(){
        return cnvs;
    }

    public static String getCurrentRunType(){
        return runType;
    }

    public void clearCommonComponentList(){
        listModel.clear();
    }


    public void parseControlRdfDefineLastModified(String runType){
        if(!runType.equals("undefined")){
            for(JCGComponent c:serCompMap.values()){
                if(!c.getName().equals(runType)){
                    Long clm= coolDbBrowser.compLastModified(runType,c.getName());
                    if(clm>0){
                        configRecordedTimes.put(c.getName(), clm);
                    }
                }
            }
        }
    }

    public void showConfiguration(String runType){

        drawingCanvas.removeAll();
        cnvs.clearPositionMap();

        configNameLabel.setText(runType);
        for(JCGComponent c:serCompMap.values()){
            if(!c.getName().equals(runType)){
                String type = c.getType();
                if(type!=null && !type.equals(""))
                    c.setImage(drawingCanvas.createBufferedImage(File.separator+"resources"+File.separator+type+".png"));

                includeUserConfigFileEdits(c);

                drawingCanvas.addgCmp(JCTools.deepCpComp(c));
            } else {
                drawingCanvas.setSupervisor(JCTools.deepCpComp(c));
            }
        }
        drawingCanvas.unZoom();
    }

    private void customCreateRegisteredComponentList(){
        // registered component name list
        listModel = new DefaultListModel();
        componentNameList = new JList(listModel);
        componentNameList.setCellRenderer(new CListCellRenderer());

        // add mouse listener
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                Object o;
                String s,n;
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 1) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        o = theList.getModel().getElementAt(index);
                        s = (String)o;
                        n = s.substring(0,s.lastIndexOf(":")).trim();
                        drawingCanvas.setSelectedGCmpName(n);
                    }
                } else if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        o = theList.getModel().getElementAt(index);
                        s = (String)o;
                        n = s.substring(0,s.lastIndexOf(":")).trim();

                        new RefactorForm(me,n,selectedType).setVisible(true);
                    }
                } else if(mouseEvent.isMetaDown()){
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        o = theList.getModel().getElementAt(index);
                        s = (String)o;
                        n = s.substring(0,s.lastIndexOf(":")).trim();
                        int i = JCTools.popConfirmationDialog("delete component = "+n,
                                "", true);
                        if(i==0){
                            listModel.remove(index);
                            removeDescribedComponent(n);
                        }
                    }
                }
            }
        };
        componentNameList.addMouseListener(mouseListener);
        // make registered component list draggable
        new CListDragSource(componentNameList, DnDConstants.ACTION_COPY_OR_MOVE);

    }

    private void customCreateComponentLabelList(){
        // component label list
        //Load the pet images and create an array of indexes.
        images = new ImageIcon[componentStrings.length];
        Integer[] intArray = new Integer[componentStrings.length];
        for (int i = 0; i < componentStrings.length; i++) {
            intArray[i] = i;
            images[i] = new ImageIcon(getClass().getResource(File.separator+"resources"+File.separator+componentStrings[i]+".png"));
            if (images[i] != null) {
                images[i].setDescription(componentStrings[i]);
            }
        }
        //Create the list.
        componentLabelList = new JList(intArray);
        CListRenderer renderer= new CListRenderer(images, componentStrings);
        renderer.setPreferredSize(new Dimension(100, 60));
        componentLabelList.setCellRenderer(renderer);
        // add mouse listener
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 1) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object o = theList.getModel().getElementAt(index);
                        if(o instanceof Integer){
                            Integer selectedTypeIndex = (Integer)o;
                            switch(selectedTypeIndex) {
                                case 0:
                                    cbt(ACodaType.TS.name());
                                    selectedType = ACodaType.TS.name();
                                    break;
                                case 1:
                                    cbt(ACodaType.GT.name());
                                    selectedType = ACodaType.GT.name();
                                    break;
                                case 2:
                                    cbt(ACodaType.ROC.name());
                                    selectedType = ACodaType.ROC.name();
                                    break;
                                case 3:
                                    cbt(ACodaType.DC.name());
                                    selectedType = ACodaType.DC.name();
                                    break;
                                case 4:
                                    cbt(ACodaType.PEB.name());
                                    selectedType = ACodaType.PEB.name();
                                    break;
                                case 5:
                                    cbt(ACodaType.SEB.name());
                                    selectedType = ACodaType.SEB.name();
                                    break;
                                case 6:
                                    cbt(ACodaType.FCS.name());
                                    selectedType = ACodaType.FCS.name();
                                    break;
                                case 7:
                                    cbt(ACodaType.ER.name());
                                    selectedType = ACodaType.ER.name();
                                    break;
                                case 8:
                                    cbt(ACodaType.SLC.name());
                                    selectedType = ACodaType.SLC.name();
                                    break;
                                case 9:
                                    cbt(ACodaType.USR.name());
                                    selectedType = ACodaType.USR.name();
                                    break;
                                case 10:
                                    cbt(ACodaType.FILE.name());
                                    selectedType = ACodaType.FILE.name();
                                    break;
                            }
                        }
                    }
                }
            }
        };
        componentLabelList.addMouseListener(mouseListener);

        // make  component label list draggable
        new CListDragSource(componentLabelList, DnDConstants.ACTION_COPY_OR_MOVE);
    }
    private void createUIComponents() {
        customCreateRegisteredComponentList();
        customCreateComponentLabelList();
    }

    private void configNameLabelMouseClicked(MouseEvent e) {
        if(!configNameLabel.getText().equals("") && !configNameLabel.getText().equals("undefined")){
            if(drawingCanvas.getSupervisor()==null){
                drawingCanvas.setSupervisor(new JCGComponent(configNameLabel.getText()));
            }
            new SupervisorForm(drawingCanvas, drawingCanvas.getSupervisor()).setVisible(true);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        createUIComponents();

        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem15 = new JMenuItem();
        menuItem2 = new JMenuItem();
        menuItem4 = new JMenuItem();
        menuItem3 = new JMenuItem();
        deleteMenuItem = new JMenuItem();
        menuItem1 = new JMenuItem();
        menu2 = new JMenu();
        menuItem11 = new JMenuItem();
        menuItem12 = new JMenuItem();
        menuItem8 = new JMenuItem();
        menuItem13 = new JMenuItem();
        menuItem14 = new JMenuItem();
        menu3 = new JMenu();
        menu4 = new JMenu();
        menuItem6 = new JMenuItem();
        menuItem7 = new JMenuItem();
        menuItem21 = new JMenuItem();
        menuItem19 = new JMenuItem();
        menuItem20 = new JMenuItem();
        menu9 = new JMenu();
        menuItem10 = new JMenuItem();
        menuItem9 = new JMenuItem();
        menu10 = new JMenu();
        menuItem32 = new JMenuItem();
        menuItem31 = new JMenuItem();
        menu11 = new JMenu();
        menuItem16 = new JMenuItem();
        menuItem17 = new JMenuItem();
        menu8 = new JMenu();
        menu6 = new JMenu();
        menuItem23 = new JMenuItem();
        menuItem24 = new JMenuItem();
        menuItem25 = new JMenuItem();
        menu5 = new JMenu();
        menuItem5 = new JMenuItem();
        menuItem18 = new JMenuItem();
        menuItem22 = new JMenuItem();
        menu7 = new JMenu();
        menuItem26 = new JMenuItem();
        menuItem27 = new JMenuItem();
        menuItem28 = new JMenuItem();
        menuItem29 = new JMenuItem();
        scrollPane2 = new JScrollPane();
        panel2 = new JPanel();
        expidLabel = new JLabel();
        panel3 = new JPanel();
        configNameLabel = new JLabel();
        componentlabelPanel = new JScrollPane();
        scrollPane1 = new JScrollPane();
        toolBar1 = new JToolBar();
        linkModeCheckBox = new JCheckBox();
        afecsHomeLabel = new JLabel();
        groupModeCheckBox = new JCheckBox();
        action1 = new ExitAction();
        action2 = new GridOnAction();
        action3 = new GridOffAction();
        action4 = new ZoomOutAction();
        action7 = new ZoomInAction();
        action8 = new DrawBoxesAction();
        action9 = new RemoveBoxesAction();
        action12 = new SaveAsAction();
        action13 = new OpenAction();
        action14 = new ClearAllAction();
        action15 = new NewAction();
        action16 = new DynArrOnAction();
        action17 = new SynArrOffAction();
        action21 = new GridAlignAction();
        action19 = new GVisibleOnAction();
        action20 = new GVisibleOffAction();
        action18 = new DeleteComponentAction();
        action22 = new DeleteLinkAction();
        action11 = new LinkModeAction();
        action23 = new SaveAction();
        action24 = new DeleteAction();
        action25 = new NewExperimentAction();
        action26 = new OpenExperimentAction();
        action27 = new DeleteExperimentAction();
        action28 = new NewSessionAction();
        action29 = new ListSessionsAction();
        action30 = new DeleteSessionAction();
        action31 = new CoolHomeAction();
        action32 = new UpdateJCeditDB();
        action33 = new Db2Xml();
        action37 = new RemoveFromGroupAction();
        action38 = new GroupResetAction();
        action39 = new GroupModeCBAction();
        action40 = new SupervisorProcess();

        //======== this ========
        Container contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- menuItem15 ----
                menuItem15.setAction(action15);
                menu1.add(menuItem15);
                menu1.addSeparator();

                //---- menuItem2 ----
                menuItem2.setAction(action13);
                menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
                menuItem2.setText("Open...");
                menu1.add(menuItem2);
                menu1.addSeparator();

                //---- menuItem4 ----
                menuItem4.setAction(action23);
                menu1.add(menuItem4);
                menu1.addSeparator();

                //---- menuItem3 ----
                menuItem3.setAction(action12);
                menuItem3.setText("Save As...");
                menu1.add(menuItem3);
                menu1.addSeparator();

                //---- deleteMenuItem ----
                deleteMenuItem.setAction(action24);
                deleteMenuItem.setText("Delete...");
                menu1.add(deleteMenuItem);
                menu1.addSeparator();

                //---- menuItem1 ----
                menuItem1.setAction(action1);
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("Edit");

                //---- menuItem11 ----
                menuItem11.setAction(action8);
                menuItem11.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
                menu2.add(menuItem11);
                menu2.addSeparator();

                //---- menuItem12 ----
                menuItem12.setAction(action9);
                menuItem12.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                menu2.add(menuItem12);
                menu2.addSeparator();

                //---- menuItem8 ----
                menuItem8.setAction(action18);
                menuItem8.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                menu2.add(menuItem8);
                menu2.addSeparator();

                //---- menuItem13 ----
                menuItem13.setAction(action22);
                menuItem13.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                menuItem13.setText("Delete Output Link");
                menu2.add(menuItem13);
                menu2.addSeparator();

                //---- menuItem14 ----
                menuItem14.setAction(action14);
                menuItem14.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                menu2.add(menuItem14);
            }
            menuBar1.add(menu2);

            //======== menu3 ========
            {
                menu3.setText("Control");

                //======== menu4 ========
                {
                    menu4.setText("Grid");

                    //---- menuItem6 ----
                    menuItem6.setAction(action2);
                    menuItem6.setActionCommand("On");
                    menu4.add(menuItem6);
                    menu4.addSeparator();

                    //---- menuItem7 ----
                    menuItem7.setAction(action3);
                    menuItem7.setActionCommand("Off");
                    menuItem7.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                    menu4.add(menuItem7);
                    menu4.addSeparator();

                    //---- menuItem21 ----
                    menuItem21.setAction(action21);
                    menuItem21.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
                    menuItem21.setEnabled(false);
                    menu4.add(menuItem21);
                    menu4.addSeparator();

                    //---- menuItem19 ----
                    menuItem19.setAction(action19);
                    menu4.add(menuItem19);
                    menu4.addSeparator();

                    //---- menuItem20 ----
                    menuItem20.setAction(action20);
                    menu4.add(menuItem20);
                }
                menu3.add(menu4);
                menu3.addSeparator();

                //======== menu9 ========
                {
                    menu9.setText("Zoom");

                    //---- menuItem10 ----
                    menuItem10.setAction(action7);
                    menuItem10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
                    menu9.add(menuItem10);
                    menu9.addSeparator();

                    //---- menuItem9 ----
                    menuItem9.setAction(action4);
                    menuItem9.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                    menu9.add(menuItem9);
                }
                menu3.add(menu9);
                menu3.addSeparator();

                //======== menu10 ========
                {
                    menu10.setText("Group");

                    //---- menuItem32 ----
                    menuItem32.setAction(action38);
                    menu10.add(menuItem32);
                    menu10.addSeparator();

                    //---- menuItem31 ----
                    menuItem31.setAction(action37);
                    menuItem31.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK|KeyEvent.SHIFT_MASK));
                    menu10.add(menuItem31);
                }
                menu3.add(menu10);
                menu3.addSeparator();

                //======== menu11 ========
                {
                    menu11.setText("Dynamic");

                    //---- menuItem16 ----
                    menuItem16.setAction(action16);
                    menuItem16.setActionCommand("Dynamic On");
                    menu11.add(menuItem16);
                    menu11.addSeparator();

                    //---- menuItem17 ----
                    menuItem17.setAction(action17);
                    menuItem17.setActionCommand("Dynamic Off");
                    menu11.add(menuItem17);
                }
                menu3.add(menu11);
            }
            menuBar1.add(menu3);

            //======== menu8 ========
            {
                menu8.setText("Expert");

                //======== menu6 ========
                {
                    menu6.setText("Experiment");

                    //---- menuItem23 ----
                    menuItem23.setAction(action25);
                    menu6.add(menuItem23);
                    menu6.addSeparator();

                    //---- menuItem24 ----
                    menuItem24.setAction(action26);
                    menu6.add(menuItem24);
                    menu6.addSeparator();

                    //---- menuItem25 ----
                    menuItem25.setAction(action27);
                    menu6.add(menuItem25);
                }
                menu8.add(menu6);
                menu8.addSeparator();

                //======== menu5 ========
                {
                    menu5.setText("Session");

                    //---- menuItem5 ----
                    menuItem5.setAction(action28);
                    menu5.add(menuItem5);
                    menu5.addSeparator();

                    //---- menuItem18 ----
                    menuItem18.setAction(action29);
                    menu5.add(menuItem18);
                    menu5.addSeparator();

                    //---- menuItem22 ----
                    menuItem22.setAction(action30);
                    menu5.add(menuItem22);
                }
                menu8.add(menu5);
                menu8.addSeparator();

                //======== menu7 ========
                {
                    menu7.setText("Cool DB");

                    //---- menuItem26 ----
                    menuItem26.setAction(action31);
                    menuItem26.setText("Path");
                    menu7.add(menuItem26);
                    menu7.addSeparator();

                    //---- menuItem27 ----
                    menuItem27.setAction(action32);
                    menuItem27.setText("Import...");
                    menu7.add(menuItem27);
                    menu7.addSeparator();

                    //---- menuItem28 ----
                    menuItem28.setAction(action33);
                    menuItem28.setText("Export");
                    menu7.add(menuItem28);
                }
                menu8.add(menu7);
                menu8.addSeparator();

                //---- menuItem29 ----
                menuItem29.setAction(action40);
                menu8.add(menuItem29);
            }
            menuBar1.add(menu8);
        }
        setJMenuBar(menuBar1);

        //======== scrollPane2 ========
        {
            drawingCanvas = new DrawingCanvas(w,h);
            scrollPane2.setViewportView(drawingCanvas);
                // Add a drop target to the drawingCanvas
             cnvs = new CCanvas(drawingCanvas);
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(null, "Experiment", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("Bitstream Charter", Font.PLAIN, 11), new Color(0, 102, 102)));

            //---- expidLabel ----
            expidLabel.setText("undefined");
            expidLabel.setForeground(new Color(102, 0, 0));

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(expidLabel, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addComponent(expidLabel, GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== panel3 ========
        {
            panel3.setBorder(new TitledBorder(null, "Configuration", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("Bitstream Charter", Font.PLAIN, 11), new Color(0, 102, 102)));

            //---- configNameLabel ----
            configNameLabel.setText("undefined");
            configNameLabel.setForeground(new Color(102, 0, 0));
            configNameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    configNameLabelMouseClicked(e);
                }
            });

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(configNameLabel, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addContainerGap())
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                        .addComponent(configNameLabel, GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //======== componentlabelPanel ========
        {
            componentlabelPanel.setBorder(new TitledBorder(null, "Components", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("Bitstream Charter", Font.PLAIN, 11), new Color(0, 102, 102)));
            componentlabelPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //---- componentLabelList ----
            componentLabelList.setFont(new Font("Bitstream Charter", Font.PLAIN, 10));
            componentLabelList.setForeground(new Color(0, 102, 102));
            componentlabelPanel.setViewportView(componentLabelList);
        }

        //======== scrollPane1 ========
        {
            scrollPane1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("Bitstream Charter", Font.PLAIN, 11), new Color(0, 102, 102)));
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

            //---- componentNameList ----
            componentNameList.setForeground(new Color(102, 51, 0));
            scrollPane1.setViewportView(componentNameList);
        }

        //======== toolBar1 ========
        {

            //---- linkModeCheckBox ----
            linkModeCheckBox.setAction(action11);
            linkModeCheckBox.setForeground(new Color(0, 102, 102));
            toolBar1.add(linkModeCheckBox);
            toolBar1.add(afecsHomeLabel);

            //---- groupModeCheckBox ----
            groupModeCheckBox.setForeground(new Color(0, 102, 102));
            groupModeCheckBox.setAction(action39);
            toolBar1.add(groupModeCheckBox);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(componentlabelPanel, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(toolBar1, GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE)
                        .addComponent(scrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addGap(6, 6, 6)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                                .addComponent(componentlabelPanel)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(toolBar1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
                            .addGap(6, 6, 6)))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem15;
    private JMenuItem menuItem2;
    private JMenuItem menuItem4;
    private JMenuItem menuItem3;
    private JMenuItem deleteMenuItem;
    private JMenuItem menuItem1;
    private JMenu menu2;
    private JMenuItem menuItem11;
    private JMenuItem menuItem12;
    private JMenuItem menuItem8;
    private JMenuItem menuItem13;
    private JMenuItem menuItem14;
    private JMenu menu3;
    private JMenu menu4;
    private JMenuItem menuItem6;
    private JMenuItem menuItem7;
    private JMenuItem menuItem21;
    private JMenuItem menuItem19;
    private JMenuItem menuItem20;
    private JMenu menu9;
    private JMenuItem menuItem10;
    private JMenuItem menuItem9;
    private JMenu menu10;
    private JMenuItem menuItem32;
    private JMenuItem menuItem31;
    private JMenu menu11;
    private JMenuItem menuItem16;
    private JMenuItem menuItem17;
    private JMenu menu8;
    private JMenu menu6;
    private JMenuItem menuItem23;
    private JMenuItem menuItem24;
    private JMenuItem menuItem25;
    private JMenu menu5;
    private JMenuItem menuItem5;
    private JMenuItem menuItem18;
    private JMenuItem menuItem22;
    private JMenu menu7;
    private JMenuItem menuItem26;
    private JMenuItem menuItem27;
    private JMenuItem menuItem28;
    private JMenuItem menuItem29;
    private JScrollPane scrollPane2;
    private JPanel panel2;
    private JLabel expidLabel;
    private JPanel panel3;
    private JLabel configNameLabel;
    private JScrollPane componentlabelPanel;
    private JList componentLabelList;
    private JScrollPane scrollPane1;
    private JList componentNameList;
    private JToolBar toolBar1;
    private JCheckBox linkModeCheckBox;
    private JLabel afecsHomeLabel;
    private JCheckBox groupModeCheckBox;
    private ExitAction action1;
    private GridOnAction action2;
    private GridOffAction action3;
    private ZoomOutAction action4;
    private ZoomInAction action7;
    private DrawBoxesAction action8;
    private RemoveBoxesAction action9;
    private SaveAsAction action12;
    private OpenAction action13;
    private ClearAllAction action14;
    private NewAction action15;
    private DynArrOnAction action16;
    private SynArrOffAction action17;
    private GridAlignAction action21;
    private GVisibleOnAction action19;
    private GVisibleOffAction action20;
    private DeleteComponentAction action18;
    private DeleteLinkAction action22;
    private LinkModeAction action11;
    private SaveAction action23;
    private DeleteAction action24;
    private NewExperimentAction action25;
    private OpenExperimentAction action26;
    private DeleteExperimentAction action27;
    private NewSessionAction action28;
    private ListSessionsAction action29;
    private DeleteSessionAction action30;
    private CoolHomeAction action31;
    private UpdateJCeditDB action32;
    private Db2Xml action33;
    private RemoveFromGroupAction action37;
    private GroupResetAction action38;
    private GroupModeCBAction action39;
    private SupervisorProcess action40;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args){
        CDesktop cd = new CDesktop();
        cd.setVisible(true);
    }

    private void cbt(String componentType){
        String s;
        StringTokenizer st1,st2;
        String name = "undefined",type = "undefined",sType = "undefined",desc = "undefined";
        int id = 0;

        StringBuilder sb = new StringBuilder();
        Set<String> hs = new HashSet<String>();

        if(componentType.equalsIgnoreCase(("out"))) componentType = ACodaType.FILE.name();
        scrollPane1.setBorder(new TitledBorder(null, componentType, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
                new Font("Bitstream Charter", Font.PLAIN, 11), new Color(0, 102, 102)));
        listModel.clear();
        if(new File(stp.getCoolHome()+File.separator+
                stp.getExpid()+File.separator+
                "jcedit"+File.separator+componentType+".txt").exists()){
            try {
                BufferedReader r = new BufferedReader(new FileReader(stp.getCoolHome()+
                        File.separator+stp.getExpid()+
                        File.separator+"jcedit"+
                        File.separator+componentType+".txt"));

                // read entire file
                while((s = r.readLine())!=null){
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
                    } catch(NumberFormatException e){
                        System.out.println(e.getMessage());
                    }

                    if(st2.hasMoreTokens()) desc  = st2.nextToken();
                    hs.add(name +
                            "                                     :^" + type +
                            "#^" + sType +
                            "@^" + id +
                            "%^" + desc);

                }

                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> ts = new TreeSet(hs);

            // sorting alphabetically using the list
            List<String> l = new ArrayList<>(ts);
            Collections.sort(l);
            for(String ss:l){
                listModel.addElement(ss);
            }
        }
    }

    /**
     *
     * @param name runType name
     * @return true if runType dir exists
     */
    public boolean isConfigExist(String name) {
        File dir =  new File(defineExpIDDire());
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if(file.getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isConfigChangedOnCanvas(String name){
        if(isConfigExist(name)) {

            // +1 because we assume also supervisor
            if (drawingCanvas.getGCMPs().size()+1 != serCompMap.size()) {
                return true;
            } else {
                for (JCGComponent c : drawingCanvas.getGCMPs().values()) {
                    if (!c.equals(serCompMap.get(c.getName()))) {
                        return true;
                    }
                }
                return !drawingCanvas.getSupervisor().equals(serCompMap.get(name));
            }
        }
        return false;
    }

    public boolean isConfigChangedByOtherUser(String name){

        for(String s:serCompMap.keySet()){
            if(!s.equals(name)){
                if(!configRecordedTimes.containsKey(s)){
                    return true;
                } else {
                    long x = coolDbBrowser.compLastModified(name,s);
                    long y = configRecordedTimes.get(s);
                    if(x != y){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static boolean fCheckCreate(File f){
        boolean suc = true;
        if(!f.exists()){
            suc = f.mkdir();
            if(!suc){
                System.out.println("Error: Cannot create f = "+f);
            }
        }
        return suc;
    }

    /**
     * Updates the foreign runType supervisor relevant rdf files with
     * process information of the current (editing) supervisor specified processes list.
     * This method is used in case user requested that defined processes of the supervisor
     * be applied to the defined list of foreign supervisors.
     *
     * @param runTypes  the list of foreign supervisor (runType) names.
     */
    public void updateForeignSupervisorsProcesses(List<String> runTypes){

        // get current editing supervisor component from the canvas
        JCGComponent current_supervisor = drawingCanvas.getSupervisor();

        if(current_supervisor.getPrcesses()==null || current_supervisor.getPrcesses().isEmpty()) return;

        for(String f_runType: runTypes){

            if(f_runType!=null){

                // de-serialize stored components (.p_name.xml files in Options dir)
                Map<String, JCGComponent> fc = null;
                try {
                    fc = coolDbBrowser.JLC(f_runType);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                // if there is no serialized supervisor description get it from rdf parser
                if(!fc.containsKey(f_runType)){
                    if (rdfParser.openFile(f_runType + File.separator + f_runType + ".rdf", false)) {
                        JCGComponent c = rdfParser.parseControlSupervisor(f_runType);
                        fc.put(c.getName(), c);
                        rdfParser.close();
                    } else {
                        return;
                    }
                }

                // update processes
                for(JCGComponent c:fc.values()) {

                    // remove all attached processes
                    c.removeAllProcesses();

                    if (c.getName().equals(f_runType)) {
                        // update the requested supervisor processes
                        c.setPrcesses(current_supervisor.getPrcesses());
                    }
                }

                 // dump JAXB sand cool files
                try {
                    coolDbBrowser.JLX(f_runType, new ArrayList(fc.values()),false);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();  //Exception handling
                }

//                JCGComponent tsp = null;
//                for(JCGComponent c:fc.values()){
//
//                    // remove all attached processes
//                    c.removeAllProcesses();
//
//                    if(c.getName().equals(f_runType)){
//                        // update the requested supervisor processes
//                        c.setPrcesses(current_supervisor.getPrcesses());
//                        tsp = c;
//
//                    } else {
//                        // create component cool-rdf description
//                        createCompCoolRDF(f_runType, c);
//                        // create component serialization (.comp_name_p.rdf file in Options dir)
//                        coolDbBrowser.xmlDumpComponent(f_runType, c);
//                    }
//
//                }
//
//                // create supervisor cool-rdf description
//                createSupCoolRDF(tsp, fc.keySet(), tsp.getName(), false);
//                // create supervisor serialization (.comp_name_p.rdf file in Options dir)
//                coolDbBrowser.xmlDumpComponent(f_runType, tsp);
            }
        }
    }

    public void removeDescribedComponent(String n){
        Map<String, CDefinedComponent> tcm = new HashMap<>();
        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id = 0;
        String s;
        StringTokenizer st1,st2;
        CDefinedComponent rc = null;
        StringBuffer sb = new StringBuffer();
        // open stored description files and add it to the map
        for(ACodaType c:ACodaType.values()){
            if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                    "jcedit"+File.separator+c.name()+".txt").exists()){
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+c.name()+".txt"));
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
                            if(st2.hasMoreTokens()) id   = Integer.parseInt(st2.nextToken());
                        } catch (NumberFormatException e){
                            System.out.println(e.getMessage());
                        }
                        if(st2.hasMoreTokens()) desc  = st2.nextToken();
                        tcm.put(name,new CDefinedComponent(name,type,sType,id,desc));
                    }
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // remove described component
        if(tcm.containsKey(n)){
            rc = tcm.get(n);
        }
        if(rc!=null){
            tcm.remove(rc.getName());
        }

        // record all into the database
        for(ACodaType c:ACodaType.values()){
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(stp.getCoolHome()+
                        File.separator+
                        stp.getExpid()+File.separator+
                        "jcedit"+File.separator+
                        c.name()+".txt"));
                for(CDefinedComponent cm:tcm.values()){
                    if(cm.getType().equals(c.name())){
                        bw.write(cm.getName()+"$"+
                                cm.getType()+"$"+
                                cm.getSubType()+"$"+
                                cm.getId()+"$"+
                                cm.getDescription()+"@@\n"
                        );
                    }
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void recordDescribedComponents(String refName, String newName, String description){

        Map<String, CDefinedComponent> tcm = new HashMap<>();
        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id = 0;

        String s;
        StringTokenizer st1, st2;
        StringBuilder sb = new StringBuilder();


        // open stored description files and add it to the map
        for(ACodaType c:ACodaType.values()){
            if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                    "jcedit"+File.separator+c.name()+".txt").exists()){
                try {
                    BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                            File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+c.name()+".txt"));

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

                        if(refName!=null && newName!=null && description!=null){
                            if(name.equals(refName)){
                                name = newName;
                                desc = description;
                            }
                        }
                        tcm.put(name,new CDefinedComponent(name,type,sType,id,desc));
                    }
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // add new created component descriptions
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){
            if(!tcm.containsKey(gc.getName())){
                tcm.put(gc.getName(),new CDefinedComponent(
                        gc.getName(),
                        gc.getType(),
                        gc.getSubType(),
                        gc.getId(),
                        gc.getDescription()
                ));
            }
        }

        // record all into the database
        for(ACodaType c:ACodaType.values()){
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(stp.getCoolHome()+
                        File.separator+
                        stp.getExpid()+File.separator+
                        "jcedit"+File.separator+
                        c.name()+".txt"));
                for(CDefinedComponent cm:tcm.values()){
                    if(cm.getType().equals(c.name())){
                        bw.write(cm.getName()+"$"+
                                cm.getType()+"$"+
                                cm.getSubType()+"$"+
                                cm.getId()+"$"+
                                cm.getDescription()+"@@\n"
                        );
                    }
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void recordComponent(JCGComponent com){
        Map<String, CDefinedComponent> tcm = new HashMap<>();
        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id = 0;
        String s;
        StringTokenizer st1, st2;
        StringBuilder sb = new StringBuilder();
        if(new File(stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                "jcedit"+File.separator+com.getType()+".txt").exists()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(stp.getCoolHome()+
                        File.separator+ stp.getExpid()+File.separator+"jcedit"+File.separator+com.getType()+".txt"));

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
                        if(st2.hasMoreTokens()) id = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException e){
                        System.out.println(e.getMessage());
                    }

                    if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    tcm.put(name,new CDefinedComponent(name,type,sType,id,desc));
                }
                br.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }


        // add new created component descriptions
        tcm.put(com.getName(),new CDefinedComponent(com.getName(),com.getType(),com.getSubType(),com.getId(),com.getDescription()));

        // record into the database
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(stp.getCoolHome()+
                    File.separator+
                    stp.getExpid()+File.separator+
                    "jcedit"+File.separator+
                    com.getType()+".txt"));
            for(CDefinedComponent cm:tcm.values()){
                bw.write(cm.getName()+"$"+
                        cm.getType()+"$"+
                        cm.getSubType()+"$"+
                        cm.getId()+"$"+
                        cm.getDescription()+"@@\n"
                );
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads already predefined component database (jcedit/type.txt)
     * Reformat updates the ID and the description of already recorded component
     * (priority xml file) definition passed through List parameter.
     * At the end recreates entire database including updated old and new described components.
     * Yet, returns only map of updated components
     * @param l list containing information passed by the user description (.xml file)
     */
    public Map<String, CDefinedComponent> updatePDB(List<CDefinedComponent> l, boolean complete){

        JCGSetup stp = JCGSetup.getInstance();
        Map<String, CDefinedComponent> tcm = new HashMap<>();
        // map contains only updated components
        Map<String, CDefinedComponent> res = new HashMap<>();
        String name = "undefined";
        String type = "undefined";
        String desc = "undefined";
        String sType = "undefined";
        int id = 0;

        String s;
        StringTokenizer st1, st2;

        // list of renamed described components (cool db)
        List<String> lRenamed;


        // open stored description files and add it to the map
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
                    // list of renamed described components (cool db)
                    lRenamed = new ArrayList<>();

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


                        // go over the user defined component list ( from .xml file)
                        // and update the id and the description of already described component
                        for(CDefinedComponent comp:l){
                            if(comp.getType().equals(type) && comp.getName().equals(name) && comp.getId()!=id){
                                int selectedOption = JOptionPane.showConfirmDialog(me,
                                        "Found ID conflict ("+name +" current id = "+id+", required id = "+comp.getId()+" for name = "+ comp.getName()+") " +
                                                "\nUpdate component ?",
                                        "Choose",
                                        JOptionPane.OK_CANCEL_OPTION);
                                if (selectedOption == JOptionPane.OK_OPTION) {
                                    tcm.put(name,new CDefinedComponent(name,type,sType,comp.getId(),comp.getDescription()));
                                    res.put(name, new CDefinedComponent(name, type, sType, comp.getId(), comp.getDescription()));
                                }

                            } else if(comp.getType().equals(type) && comp.getId()==id && !comp.getName().equals(name)){
                                int selectedOption = JOptionPane.showConfirmDialog(me,
                                        "Found Name conflict (for recorded name = "+name +" required name = "+comp.getName()+", id = "+id+" "+comp.getId()+") " +
                                                "\nRename and update component ?",
                                        "Choose",
                                        JOptionPane.OK_CANCEL_OPTION);
                                if (selectedOption == JOptionPane.OK_OPTION) {
                                    // remove by renaming
                                    lRenamed.add(name);
                                    tcm.put(comp.getName(), new CDefinedComponent(comp.getName(), type, sType, comp.getId(), comp.getDescription()));
                                    res.put(comp.getName(), new CDefinedComponent(comp.getName(), type, sType, comp.getId(), comp.getDescription()));

                                }
                            }
                        }
                        // preserve already recorded but not required to update components
                        if(!tcm.containsKey(name) && lRenamed!=null && !lRenamed.contains(name)){
                            tcm.put(name, new CDefinedComponent(name, type, sType, id, desc));
                        }

                    }
                    br.close();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // now we add new described components from the xml file to the map
        for(CDefinedComponent comp:l){
            if(!tcm.containsKey(comp.getName())){
                tcm.put(comp.getName(),comp);
            }
        }

        // record created component map
        for(ACodaType c:ACodaType.values()){
            try {
                String fName = stp.getCoolHome()+
                        File.separator+
                        stp.getExpid()+File.separator+
                        "jcedit"+File.separator+
                        c.name()+".txt";
                BufferedWriter bw = new BufferedWriter(new FileWriter(fName));
                for(CDefinedComponent cm:tcm.values()){
                    if(cm.getType().equals(c.name())){
                        bw.write(cm.getName()+"$"+
                                cm.getType()+"$"+
                                cm.getSubType()+"$"+
                                cm.getId()+"$"+
                                cm.getDescription()+"@@\n"
                        );
                    }
                }
                bw.close();
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
        if(complete) return tcm;
        else return res;
    }

    public static String isComponentPredefined(String n, String t, String st, String d){
        String out   = "undefined";
        String name  = "undefined";
        String type  = "undefined";
        String desc  = "undefined";
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
                        if(st2.hasMoreTokens()) id = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException e ){
                        System.out.println(e.getMessage());
                    }
                    if(st2.hasMoreTokens()) desc  = st2.nextToken();

                    if(name.equals(n)){
                        br.close();
                        out = desc;
                        return out;
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return out;
    }

    public static boolean isIDPredefined(String t, int _id){
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
                br.close();
                // get single component record
                st1 = new StringTokenizer(sb.toString(),"@@");
                while(st1.hasMoreTokens()){
                    // get data
                    st2 = new StringTokenizer(st1.nextToken(),"$");
                    if(st2.hasMoreTokens()) st2.nextToken();
                    if(st2.hasMoreTokens())  st2.nextToken();
                    if(st2.hasMoreTokens())  st2.nextToken();
                    try{
                        if(st2.hasMoreTokens()) id = Integer.parseInt(st2.nextToken());
                    } catch (NumberFormatException e ){
                        System.out.println(e.getMessage());
                    }
                    if(st2.hasMoreTokens())  st2.nextToken();


                    if(id==_id){
                        br.close();
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    public static boolean isComponentPredefined(String t,String n){
        String name  = "undefined";
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

                    if(name.equals(n)){
                        br.close();
                        return true;
                    }
                }
                br.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }



    public void refactor(String _type, String name, int id, String description) {

        // clear canvas
        getDrawingCanvas().removeAll();
        getCCanvas().clearPositionMap();
        clearCommonComponentList();


        String _pName;


        // update name and description through entire cool database
        // for all registered runTypes get the component object and change the name and description of it
        File configs = new File(stp.getCoolHome() + File.separator + stp.getExpid() + File.separator + "config" + File.separator + "Control");
        String[] list = configs.list();

        for (String runType : list) {
            if(runType!=null){

                // de-serialize stored components (.p_name.xml files in Options dir)
                Map<String, JCGComponent> comps = null;
                try {
                    comps = coolDbBrowser.JLC(runType);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                for (JCGComponent com : comps.values()) {

                    if (com.getType().equals(_type) && com.getName().equals(name) && com.getId() != id) {
                        com.setDescription(description);
                        com.setId(id);
                    } else if (com.getType().equals(_type) && com.getId() == id && !com.getName().equals(name)) {
                        // we need to preserve the old name for updating links and transports
                        _pName = com.getName();
                        com.setName(name);
                        com.setDescription(description);
                        for (JCGLink l : com.getLnks()) {
                            if (l.getSourceComponentName().equals(_pName)) {
                                l.setSourceComponentName(name);
                                l.setName(l.getSourceComponentName() + "_" + l.getDestinationComponentName());
                            } else if (l.getDestinationComponentName().equals(_pName)) {
                                l.setDestinationComponentName(name);
                                l.setName(l.getSourceComponentName() + "_" + l.getDestinationComponentName());
                            }
                            for (JCGTransport t : com.getTrnsports()) {
                                t.setName(t.getName().replace(_pName, name));
                                t.setEtName(t.getEtName().replace(_pName, name));
                                l.setDestinationTransportName(t.getName());
                                l.setSourceTransportName(t.getName());
                            }
                        }
                    }
                    includeUserConfigFileEdits(com);
                }
                try {
                    coolDbBrowser.JLX(runType, new ArrayList(comps.values()),false);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

//                // create component cool-rdf description
//                    createCompCoolRDF(runType, com);
//                    // create component serialization (.comp_name_p.rdf file in Options dir)
//                    coolDbBrowser.xmlDumpComponent(runType, com);

            }
        }

        // clear canvas
        getConfigNameLabel().setText("");

        // show configuration
//        showConfiguration(getCurrentRunType());
    }

    public static void includeUserConfigFileEdits(JCGComponent com){
        String type = com.getType();
        // update component with the possible user edits of the configuration files ( .dat and .xml files)
        // roc configure updates .dat file
        if(type.equals(ACodaType.ROC.name()) ||
                type.equals(ACodaType.USR.name()) ||
                type.equals(ACodaType.TS.name()) ||
                type.equals(ACodaType.GT.name())){
            RocConfigReader rd = new RocConfigReader(runType, com.getName());
            if(rd.isConfigExists()){
                JCGCompConfig cf = rd.parseConfig();

                for(JCGConcept cpt:cf.getConfigData()){
                    if(cpt.getName().equals("priority")){
                        try {
                            com.setPriority(Integer.parseInt(cpt.getValue()));
                        } catch (NumberFormatException e1) {
                            System.out.println("Error: Priority is not a number");
                        }
                    }

                    if(cpt.getName().equals("code")){
                        String code = cpt.getValue();
                        // parse code string and get rol1 (rol1String) and rol2(rol2String)
                        StringTokenizer st1 = new StringTokenizer(code,"{");
                        String trl1 = null;
                        String trl2 = null;
                        if(st1.hasMoreTokens()){
                            trl1 = st1.nextToken();
                        }
                        if(st1.hasMoreTokens()){
                            trl2 = st1.nextToken();
                        }
                        if (trl1!=null){
                            StringTokenizer r1 = new StringTokenizer(trl1);
                            if(r1.hasMoreTokens()){
                                com.setRol1(r1.nextToken());
                            }
                            if(r1.hasMoreTokens()){
                                String tr1 = r1.nextToken();
                                if(!tr1.contains("}")) {
                                    com.setRol1UsrString(tr1);
                                } else {
                                    com.setRol1UsrString(tr1.substring(0, tr1.indexOf("}")));
                                }
                            }
                        }
                        if (trl2!=null){
                            StringTokenizer r2 = new StringTokenizer(trl2);
                            if(r2.hasMoreTokens()){
                                com.setRol2(r2.nextToken());
                            }
                            if(r2.hasMoreTokens()){
                                String tr2 = r2.nextToken();
                                if(!tr2.contains("}")){
                                    com.setRol2UsrString(tr2);
                                } else {
                                    com.setRol2UsrString(tr2.substring(0, tr2.indexOf("}")));
                                }
                            }
                        }
                    }
                }
            }


            // emu configure updates .xml file
        }
//        else if(type.equals(ACodaType.DC.name()) ||
//                type.equals(ACodaType.SEB.name()) ||
//                type.equals(ACodaType.PEB.name())){
//            EmuConfigReader rd = new EmuConfigReader(runType, com.getName());
//            if(rd.isConfigExists()){
//                JCGCompConfig cf = rd.parseConfig();
//                Set<JCGConcept> rc = cf.getConfigData();
//                //@todo not implemented
//            }
//        }
    }

    private boolean isRocExists(){
        for(JCGComponent tCmp: drawingCanvas.getGCMPs().values()){
            if(tCmp.getType().equals(ACodaType.ROC.name()) ||
                    tCmp.getType().equals(ACodaType.TS.name()) ||
                    tCmp.getType().equals(ACodaType.GT.name())){
                return true;
            }
        }
        return false;
    }

    private boolean isMasterRocExists(){
        for(JCGComponent tCmp: drawingCanvas.getGCMPs().values()){
            if(tCmp.isMaster())return true;
        }
        return false;
    }

    private void createAndSave(String name, boolean removeFirst){

        drawingCanvas.unZoom();

        // check and reassign mastership
        if(!checkReassignMastership()){

            if(isRocExists() && !isMasterRocExists()){
                JOptionPane.showMessageDialog(me,
                        "Master Roc definition error ",
                        "Error",JOptionPane.ERROR_MESSAGE);
                return;

            }
        }


        // make sure emuSocketPorts are unique.
        checkReassignEmuSocketTransportPort();

        // check if user requested to apply current supervisor scripts
        // on all other supervisors, the same time removing all other
        // scripts assigned to non-supervisor components.
        if(updateManySupervisors){
            supervisorList.remove(runType);

            // remove current runType components attached processes
           for(JCGComponent c: drawingCanvas.getGCMPs().values()){
               if(!c.getName().equals(runType)) {
                   c.removeAllProcesses();
               }
           }
            updateForeignSupervisorsProcesses(supervisorList);
            supervisorList.clear();
            updateManySupervisors = false;
        }

        // update serialized object map
        serCompMap.clear();

        // set for the supervisor component names
        Set<String> cmps = new HashSet<>();
        // deep copy into serialized component map. This is done to
        // avoid deserialization from the disk after we save the config
        for(JCGComponent c: drawingCanvas.getGCMPs().values()){
            JCGComponent tmp = JCTools.deepCpComp(c);
            serCompMap.put(tmp.getName(), tmp);
            cmps.add(tmp.getName());
        }
        // supervisor component is added to the serialization map
        JCGComponent sup = JCTools.deepCpComp(drawingCanvas.getSupervisor());
        serCompMap.put(sup.getName(), sup);

        // now dump jaxb as well as cool files
        try {
            coolDbBrowser.JLX(name, new ArrayList(serCompMap.values()), removeFirst);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


//        // create supervisor cool-rdf description
//        createSupCoolRDF(drawingCanvas.getSupervisor(), drawingCanvas.getGCMPs().keySet(), name, removeFirst);
//        // create component serialization (.comp_name_p.rdf file in Options dir)
//        coolDbBrowser.xmlDumpComponent(name, drawingCanvas.getSupervisor());
//
//        // update supervisor serialized object in memory
//        JCGComponent dsp = JCTools.deepCpComp(drawingCanvas.getSupervisor());
//        serCompMap.put(dsp.getName(), dsp);
//
//
//        for(JCGComponent c: drawingCanvas.getGCMPs().values()){
//            createCompCoolRDF(name,c);
//             create component serialization (.comp_name_p.rdf file in Options dir)
//            coolDbBrowser.xmlDumpComponent(name, c);
//
//            // update component serialized object in memory
//            JCGComponent dc = JCTools.deepCpComp(c);
//            serCompMap.put(dc.getName(), dc);
//
//        }

        // add to the JCEdit defined component file ( common to all configurations .txt)
        recordDescribedComponents(null, null, null);

        // create runType/configuration specific RTV file,
        // i.e. file that contains defined RTV with values = "unset"
        createRTVFile(drawingCanvas.getGCMPs(),name);

        configNameLabel.setText(name);
        runType = name;

        // create/replace tar file for the EXPID
        try {
            String xpDir = stp.getCoolHome()+File.separator+stp.getExpid();
            String cmd =  "tar cvf "+stp.getCoolHome()+File.separator+"tar"+File.separator+stp.getExpid()+".tar "+ xpDir;
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createRTVFile(ConcurrentHashMap<String, JCGComponent> compMap, String runType){

        List<String> rtvs = new ArrayList<>();

        // find defined rtvs and store them in the list
        for(JCGComponent c:compMap.values()){
            JCTools.getRTVsInAString(c.getCommand(),rtvs);
            JCTools.getRTVsInAString(c.getRol1(),rtvs);
            JCTools.getRTVsInAString(c.getRol1UsrString(),rtvs);
            JCTools.getRTVsInAString(c.getRol2(),rtvs);
            JCTools.getRTVsInAString(c.getRol2UsrString(),rtvs);
            JCTools.getRTVsInAString(c.getUserConfig(),rtvs);

            for(JCGProcess p:c.getPrcesses()){
                JCTools.getRTVsInAString(p.getScriptCommand(), rtvs);
            }
            for(JCGTransport t:c.getTrnsports()){
                JCTools.getRTVsInAString(t.getFileName(),rtvs);
            }
        }

        // check for RTVs in processes for supervisor agent
        JCGComponent gc = drawingCanvas.getSupervisor();
        for(JCGProcess p:gc.getPrcesses()){
            JCTools.getRTVsInAString(p.getScriptCommand(), rtvs);
        }

        // check and create rtv directory if it does not exist
        File x= new File(stp.getCoolHome()+File.separator+
                stp.getExpid()+File.separator+
                "user"+File.separator+
                "rtv"+File.separator+
                runType);

        // delete first the directory.
        if (x.exists()) JCTools.deleteDir(x);

        // create a new directory for the runType
        x.mkdir();

        try {
            Runtime.getRuntime().exec("chmod a+rwx "+stp.getCoolHome()+File.separator+
                    stp.getExpid()+File.separator+
                    "user"+File.separator+
                    "rtv"+File.separator+
                    runType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = stp.getCoolHome()+File.separator+
                stp.getExpid()+File.separator+
                "user"+File.separator+
                "rtv"+File.separator+
                runType+File.separator+
                "undefined_rtv.xml";


        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<rtvs>\n");
            for(String r:rtvs){
                bw.write("  <rtv>\n");
                bw.write("    <name>"+r+"</name>\n");
                bw.write("    <value>unset</value>\n");
                bw.write("  </rtv>\n");
            }
            bw.write("</rtvs>\n");
            bw.close();
        } catch (IOException e1) {
            e1.getMessage();
        }
    }

    /**
     * Counts the number of roc and ts components in the configuration that have
     * are defined to be the master ROC.
     * @param drawingCanvas  object of DrawingCanvas
     * @return number of master rocs
     */
    public static int getNumberOfRocs(DrawingCanvas drawingCanvas){
        int count = 1;
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){
            if(gc.getType().equals(ACodaType.ROC.name())){
                count++;
            }
        }
        return count;
    }

    /**
     * see if there is a roc or ts or gt component in the configuration
     * @param drawingCanvas  object of DrawingCanvas
     * @return true or false
     */
    public static boolean containsRocGtTs(DrawingCanvas drawingCanvas){
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){
            if(gc.getType().equals(ACodaType.ROC.name()) ||
                    gc.getType().equals(ACodaType.TS.name()) ||
                    gc.getType().equals(ACodaType.GT.name())
                    ){
                return true;
            }
        }
        return false;
    }

    /**
     * see if there is a ts or ts component in the configuration
     * @param drawingCanvas  object of DrawingCanvas
     * @return JCGComponent
     */
    public static JCGComponent containsTs(DrawingCanvas drawingCanvas){
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){
            if(gc.getType().equals(ACodaType.TS.name())){
                return gc;
            }
        }
        return null;
    }

    /**
     * see if there is a gt or ts component in the configuration
     * @param drawingCanvas  object of DrawingCanvas
     * @return JCGComponent
     */
    public static JCGComponent containsGt(DrawingCanvas drawingCanvas){
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){
            if(gc.getType().equals(ACodaType.GT.name())){
                return gc;
            }
        }
        return null;
    }

    /**
     * see if there is a gt or ts component in the configuration
     * @param drawingCanvas  object of DrawingCanvas
     */
    public static void resetMaster(DrawingCanvas drawingCanvas){
        for(JCGComponent gc:drawingCanvas.getGCMPs().values()){

            if(gc.getType().equals(ACodaType.ROC.name())){
                gc.setMaster(false);
                // set default ROC priority in case it was previously bumped to TS priority. Keep the rest of ROCs
                // with user specified priorities within the ROC priority range.
                if(gc.getPriority() == ACodaType.TS.priority()){
                    gc.setPriority(ACodaType.ROC.priority());
                }
            } else if (gc.getType().equals(ACodaType.TS.name()) || gc.getType().equals(ACodaType.GT.name())) {
                gc.setMaster(false);
                gc.setPriority(ACodaType.getEnum(gc.getType()).priority());
            }
        }
    }

    private class ExitAction extends AbstractAction {
        private ExitAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Exit");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            int i=0;
            boolean canvasEmpty = drawingCanvas.getGCMPs().isEmpty();
            boolean kaConfig   = isConfigExist(configNameLabel.getText());
            boolean kaChange   = isConfigChangedOnCanvas(configNameLabel.getText());

            boolean popup = false;
            if(!canvasEmpty){
                if(kaConfig){
                    if(kaChange){
                        popup = true;
                    }
                } else {
                    popup = true;
                }
            }

            if(popup){
                i = JCTools.popConfirmationDialog("There are unsaved changes.",
                        "Do you want to continue?", false);
            }

            if(i==0){
                dispose();
                System.exit(1);
            }
        }
    }

    private class GridOnAction extends AbstractAction {
        private GridOnAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "On");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.gridOn();
        }
    }

    private class GridOffAction extends AbstractAction {
        private GridOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.gridOff();
        }
    }


    private class ZoomInAction extends AbstractAction {
        private ZoomInAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Zoom In");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.zoomIn();
        }
    }

    private class LinkModeOnAction extends AbstractAction {
        private LinkModeOnAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Link On");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.linkModeOn();
            setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                  Link Mode                           ");
        }
    }

    private class LinkModeOffAction extends AbstractAction {
        private LinkModeOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Link Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.linkModeOff();
            if(drawingCanvas.isAllowed2Move()){
                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                  Dynamic Mode                        ");
            } else {
                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                                                      ");
            }
        }
    }

    private class DrawBoxesAction extends AbstractAction {
        private DrawBoxesAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Draw Boxes");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.boxesOn();
        }
    }

    private class RemoveBoxesAction extends AbstractAction {
        private RemoveBoxesAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Remove Boxes");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.boxesOff();
        }
    }

    private class DeleteAction extends AbstractAction {
        private DeleteAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete...");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {

            if(expidLabel.getText().equals("undefined") || expidLabel.getText().equals("")){
                JOptionPane.showMessageDialog(me,"No experiment is selected.");
            } else {

                int i;
                String fileNameFilter = (String)JOptionPane.showInputDialog(me,
                        "Enter an optional Config name filter. \n(use * as wildcard characters.)",
                        "COOL RunTypes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        null,
                        "");

                CDirList fc = new CDirList(stp.getCoolHome() + File.separator+ stp.getExpid()+
                        File.separator+"config"+File.separator+"Control", fileNameFilter);
                String rt = (String)JOptionPane.showInputDialog(me,
                        "Select a configuration to delete",
                        "COOL RunTypes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        fc.getNames(),"");
                if(rt!=null){
                    i = JCTools.popConfirmationDialog("delete configuration = "+rt,
                            "This will permanently delete "+rt+" configuration description from the COOL database.", true);
                    if(i==0){
                        File f = new File(stp.getCoolHome()+File.separator+stp.getExpid()+File.separator+"config"+File.separator+"Control"+File.separator+rt);
                        JCTools.deleteDir(f);
                        if(rt.equals(runType)){
                            getDrawingCanvas().removeAll();
                            getConfigNameLabel().setText("");
                        }
                    }
                }
            }
        }
    }

    private class DeleteLinkAction extends AbstractAction {
        private DeleteLinkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete Output Link");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(drawingCanvas.getLinkCount()>0){
                int i = JCTools.popConfirmationDialog("delete link","",true);
                if(i==0){
                    if(drawingCanvas.getLinkCount()==1){
                        drawingCanvas.linkDeleteAll();
                    } else {
                        JCListDialog d = new JCListDialog();
                        d.showDialog(me,drawingCanvas.getLinkNames(),"Links",drawingCanvas.getSelectedGCmpName(),null);
                        if(d.status==1){
                            drawingCanvas.linkDelete(d.getSelectedListElement());
                        }
                    }
                }
            }
        }
    }

    private class SaveAction extends AbstractAction {
        private SaveAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Save");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            String name;
            StringBuilder noRolCmps = new StringBuilder();
            if(drawingCanvas.getGCMPs().isEmpty()){
                JOptionPane.showMessageDialog(me,"Nothing to save");
            } else {
                for(JCGComponent c:drawingCanvas.getGCMPs().values()){

                    if((c.getType().equals(ACodaType.ROC.name()) ||
                            c.getType().equals(ACodaType.GT.name()) ||
                            c.getType().equals(ACodaType.TS.name()))
                            && c.getRol1().equals("undefined")){
                        noRolCmps.append(c.getName()).append(", ");
                    }
                }
                if(noRolCmps.length()>0) {
                    int j = JOptionPane.showConfirmDialog(me,
                            "Readout list is not defined for \n"+
                                    noRolCmps.toString().substring(0,noRolCmps.toString().lastIndexOf(","))+
                                    ".\nContinue?",
                            "warning",JOptionPane.YES_NO_OPTION);
                    if(j!=0) return;
                }
                name = runType;

                if(name.equals("undefined")) name = configNameLabel.getText();

                if(name!=null && name.equals("undefined")){
                    name = JOptionPane.showInputDialog("Name the configuration:");
                }

                if(name!=null && !(name.equals("undefined"))){
//                    drawingCanvas.setSupervisor(new JCGComponent(name));
                    int i ;
                    if(isConfigExist(name)){
                        if(isConfigChangedByOtherUser(name)){
                            String message = "Some other user has modified the configuration in the database. " +
                                    "\nThis will overwrite current configuration in the database.";
                            i = JCTools.popConfirmationDialog("save", message, true);
                            if (i == 0) {
                                drawingCanvas.getSupervisor().setName(name);
                                createAndSave(name, true);
                            }
                        } else if(isConfigChangedOnCanvas(name)) {
                            String message = "This will overwrite existing configuration in the database.";
                            i = JCTools.popConfirmationDialog("save", message, true);

                            if (i == 0) {
                                drawingCanvas.getSupervisor().setName(name);
                                createAndSave(name, true);
                            }
                        }
                    } else {
                        drawingCanvas.getSupervisor().setName(name);
                        createAndSave(name, true);
                    }

                    // save drawing canvas JPanel content into a png file
                    BufferedImage image = new BufferedImage(drawingCanvas.getWidth(), drawingCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = image.createGraphics();

                    drawingCanvas.paint(graphics2D);
                    try{
                        javax.imageio.ImageIO.write(image,"jpeg", new File(stp.getCoolHome()+File.separator+stp.getExpid()+
                                File.separator+"config"+File.separator+"Control"+File.separator+name+File.separator+name+".jpeg"));
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }

                }
                // update stored disk configuration image
                parseControlRdfDefineLastModified(runType);
            }

        }
    }

    private class SaveAsAction extends AbstractAction {
        private SaveAsAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Save As...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            String name;
            StringBuilder noRolCmps = new StringBuilder();
            if(drawingCanvas.getGCMPs().isEmpty()){
                JOptionPane.showMessageDialog(me,"Nothing to save");
            } else {
                for(JCGComponent c:drawingCanvas.getGCMPs().values()){
                    if((c.getType().equals(ACodaType.ROC.name()) ||
                            c.getType().equals(ACodaType.GT.name()) ||
                            c.getType().equals(ACodaType.TS.name())
                    )
                            && c.getRol1().equals("undefined")){
                        noRolCmps.append(c.getName()).append(",");
                    }
                }
                if(noRolCmps.length()>0) {
                    int j = JOptionPane.showConfirmDialog(me,
                            "Readout list is not defined for \n"+
                                    noRolCmps.toString().substring(0,noRolCmps.toString().lastIndexOf(","))+
                                    ".\nContinue?",
                            "warning",JOptionPane.YES_NO_OPTION);
                    if(j!=0) return;
                }
                if(runType == null || runType.equals("undefined")){
                    name = JOptionPane.showInputDialog("Name the configuration:");
                } else {
                    name = JOptionPane.showInputDialog("Name the configuration:", runType);
                }

                if(name!=null && !(name.equals("undefined")) && !name.equals("")){

                    name = name.trim().replaceAll(" ","_");
                    name = name.trim().replaceAll(Pattern.quote("+"),"_");

                    int i=0;
                    if(isConfigExist(name)){
                        if(isConfigChangedByOtherUser(name)){
                            String message = "Some other user has modified the configuration in the database. " +
                                    "\nThis will overwrite current configuration in the database.";
                            i = JCTools.popConfirmationDialog("save", message, true);
                        } else if(isConfigChangedOnCanvas(name)){
                            String message = "This will overwrite existing configuration in the database.";
                            i = JCTools.popConfirmationDialog("save", message, true);
                        }
                        if(i==0){
                            drawingCanvas.getSupervisor().setName(name);
                            runType = name;
                            createAndSave(name, true);
                        }
                    } else {
                        drawingCanvas.getSupervisor().setName(name);
                        runType = name;
                        createAndSave(name, true);
                    }

                    // save drawing canvas JPanel content into a png file
                    BufferedImage image = new BufferedImage(drawingCanvas.getWidth(), drawingCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics2D = image.createGraphics();

                    drawingCanvas.paint(graphics2D);
                    try{
                        javax.imageio.ImageIO.write(image,"jpeg", new File(stp.getCoolHome()+File.separator+stp.getExpid()+
                                File.separator+"config"+File.separator+"Control"+File.separator+name+File.separator+name+".jpeg"));
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }

                }
                // update stored disk configuration image
                parseControlRdfDefineLastModified(runType);
            }
            setTitle("COOL Database Editor.   db = " + stp.getCoolHome() + "                                                                                                      ");
        }
    }

    private class OpenAction extends AbstractAction {
        private OpenAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Open...");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            int i = 0;
            if (expidLabel.getText().equals("undefined") || expidLabel.getText().equals("")) {
                JOptionPane.showMessageDialog(me, "No experiment is selected.");
            } else {
                drawingCanvas.resetGridFont();
                if ( !drawingCanvas.getGCMPs().isEmpty() &&
                        isConfigChangedOnCanvas(configNameLabel.getText()
                        )) {
                    i = JCTools.popConfirmationDialog("There are unsaved changes.",
                            "Do you want to continue?", false);
                }

                if (i == 0) {

                    String fileNameFilter = (String)JOptionPane.showInputDialog(me,
                            "Enter an optional Config name filter. \n(use * as wildcard characters.)",
                            "COOL RunTypes",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            null,
                            "");

                    CDirList fc = new CDirList(stp.getCoolHome() + File.separator + stp.getExpid() +
                            File.separator + "config" + File.separator + "Control", fileNameFilter);
                    runType = (String) JOptionPane.showInputDialog(me,
                            "Select a configuration",
                            "COOL RunTypes",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            fc.getNames(), "");
                    if (runType == null) runType = "undefined";
                    if (!runType.equals("undefined")) {

                        // New configuration request. Clear rdf and serialization maps
                        serCompMap.clear();

                        // de-serialize stored components (.p_name.xml files in Options dir)
//                        long st = System.currentTimeMillis();

//                        serCompMap = coolDbBrowser.listComponents(runType);
                        try {
                            serCompMap = coolDbBrowser.JLC(runType);
                        } catch (ExecutionException | InterruptedException e1) {
                            e1.printStackTrace();
                        }


//                        long et = System.currentTimeMillis();
//                        System.out.println("DDD - dt = "+(et-st) +" ms");

                        // if there is no serialized supervisor description get it from rdf parser
                        if(!serCompMap.containsKey(runType)){
                            if (rdfParser.openFile(runType + File.separator + runType + ".rdf", false)) {
                                JCGComponent c = rdfParser.parseControlSupervisor(runType);
                                serCompMap.put(c.getName(), c);
                                rdfParser.close();
                            } else {
                                return;
                            }
                        }

                        // show the configuration on the canvas
                        showConfiguration(runType);
                        parseControlRdfDefineLastModified(runType);

                    }
                }
            }
        }
    }

    private class ClearAllAction extends AbstractAction {
        private ClearAllAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Clear All");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            int i = JCTools.popConfirmationDialog("delete all","", true);
            if(i==0){
                drawingCanvas.removeAll();
                cnvs.clearPositionMap();
            }
        }
    }

    private class NewAction extends AbstractAction {
        private NewAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "New...");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(expidLabel.getText().equals("undefined") || expidLabel.getText().equals("")){
                JOptionPane.showMessageDialog(me,"No experiment is selected.");
            } else {

                int i=0;
                if( (
                        !drawingCanvas.getGCMPs().isEmpty() &&
                                !isConfigExist(configNameLabel.getText())
                )  || (
                        isConfigExist(configNameLabel.getText()) &&
                                isConfigChangedOnCanvas(configNameLabel.getText())
                ) ){
                    i = JCTools.popConfirmationDialog("There are unsaved changes.",
                            "Do you want to continue?",false);
                }
                if(i==0){
                    drawingCanvas.removeAll();
                    cnvs.clearPositionMap();
                    runType = "undefined";
                    runType = JOptionPane.showInputDialog("Name the configuration:");
                    if(runType==null)runType = "undefined";
                    runType = runType.trim().replaceAll(" ","_");
                    runType = runType.trim().replaceAll(Pattern.quote("+"),"_");
                    configNameLabel.setText(runType);
                }
            }
        }
    }

    class RdfFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(".rdf");
        }
        public String getDescription() {
            return "*.rdf";
        }
    }

    private class DynArrOnAction extends AbstractAction {
        private DynArrOnAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Dynamic On");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(!drawingCanvas.isLinkMode()){
                drawingCanvas.setAllowedToMove(true);
                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                                                      ");
//                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                  Dynamic Mode                        ");
            }
        }
    }

    private class SynArrOffAction extends AbstractAction {
        private SynArrOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Dynamic Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.setAllowedToMove(false);
            setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                  Static Mode                        ");
//            if(drawingCanvas.isLinkMode()){
//                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                  Link Mode                           ");
//            } else {
//                setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                                                      ");
//            }
        }
    }



    private class GridAlignAction extends AbstractAction {
        private GridAlignAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Align");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.align();
        }
    }


    private class GVisibleOnAction extends AbstractAction {
        private GVisibleOnAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Visible On");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.setGridVisible(true);
        }
    }

    private class GVisibleOffAction extends AbstractAction {
        private GVisibleOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Visible Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            putValue(ACTION_COMMAND_KEY, "Visible Off");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.setGridVisible(false);
        }
    }

    private class ZoomOutAction extends AbstractAction {
        private ZoomOutAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Zoom Out");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.zoomOut();
        }
    }

    private class DeleteComponentAction extends AbstractAction {
        private DeleteComponentAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete Component");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            int i = JCTools.popConfirmationDialog("delete component","",true);
            if(i==0){
                drawingCanvas.componentDelete();
            }
        }
    }

    private class LinkOffAction extends AbstractAction {
        private LinkOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Link Off");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.linkModeOff();
            setTitle("COOL Database Editor.   db = "+stp.getCoolHome()+"                                                                                            ");

        }
    }

    private class LinkModeAction extends AbstractAction {
        private LinkModeAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Link Mode");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(linkModeCheckBox.isSelected()){
                drawingCanvas.linkModeOn();
                linkModeCheckBox.setForeground(Color.RED);
                menuItem8.setEnabled(false);
//                menuItem13.setEnabled(false);
            } else {
                drawingCanvas.linkModeOff();
                linkModeCheckBox.setForeground(new Color(0, 102, 102));
                menuItem8.setEnabled(true);
                menuItem13.setEnabled(true);
            }
        }
    }




    private class NewExperimentAction extends AbstractAction {
        private NewExperimentAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "New...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {

            String nexpid = JOptionPane.showInputDialog(me,"Name the Experiment:", "COOL ", JOptionPane.QUESTION_MESSAGE);
            if(nexpid!=null){
                if(stp.isCoolDatabaseExist(nexpid)){
                    JOptionPane.showMessageDialog(me,
                            "Database for the experiment ID = "+nexpid+" already exists.",
                            "warning",JOptionPane.WARNING_MESSAGE);
                } else {
                    if(stp.createCoolDatabase(nexpid)<0){
                        expidLabel.setText("undefined");
                    } else {
                        expidLabel.setText(nexpid);
                    }
                }
            }
        }
    }

    private class OpenExperimentAction extends AbstractAction {
        private OpenExperimentAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Open");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            CDirList fc = new CDirList(stp.getCoolHome(), null);
            String ne = (String)JOptionPane.showInputDialog(me,
                    "Select an experiment",
                    "COOL Experiments",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    fc.getNames(),"");
            if(ne!=null){
                stp.setExpid(ne);
                expidLabel.setText(ne);
            }
        }
    }

    private class DeleteExperimentAction extends AbstractAction {
        private DeleteExperimentAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            CDirList fc = new CDirList(stp.getCoolHome(),null);
            String ne = (String)JOptionPane.showInputDialog(me,
                    "Select an experiment.",
                    "COOL Experiments",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    fc.getNames(),"");
            if(ne!=null){
                int i = JCTools.popConfirmationDialog("delete the experiment = "+ne,
                        "This will permanently delete "+ne+" COOL database.",true);
                if(i==0){
                    JCTools.deleteDir(new File(stp.getCoolHome() + File.separator + ne));
                    if (expidLabel.getText().equals(ne)) expidLabel.setText("undefined");
                    JCTools.deleteDir(new File(stp.getCoolHome() + File.separator + ne));
                }
            }
        }
    }

    private class NewSessionAction extends AbstractAction {
        private NewSessionAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "New...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            String session = JOptionPane.showInputDialog(me,"Name the Session:", "COOL ", JOptionPane.QUESTION_MESSAGE);
            if(session!=null){
                if(stp.isSessionExist(session)){
                    JOptionPane.showMessageDialog(me,
                            "Session = "+session+" already exists.",
                            "warning",JOptionPane.WARNING_MESSAGE);
                } else {
                    stp.addSession(session);
                }
            }
        }
    }

    private class ListSessionsAction extends AbstractAction {
        private ListSessionsAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "List");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            new CListDialog(me,stp.getSessionNames());
        }
    }

    private class DeleteSessionAction extends AbstractAction {
        private DeleteSessionAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            String ne = (String)JOptionPane.showInputDialog(me,
                    "Select a session of the experiment = "+stp.getExpid(),
                    "COOL Session",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    stp.getSessionNames(),"");
            if(ne!=null){
                int i = JCTools.popConfirmationDialog("delete the session = "+ne,
                        "This will permanently delete session = "+ne+" from the "+stp.getExpid()+" database.",true);
                if(i==0){
                    stp.removeSession(ne);
                }
            }
        }
    }


    private class CoolHomeAction extends AbstractAction {
        private CoolHomeAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Cool Database");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(me,System.getenv("COOL_HOME"), "COOL Database",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class UpdateJCeditDB extends AbstractAction {
        private UpdateJCeditDB() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Import...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            String runConfDir = stp.getCoolHome()+File.separator;
            chooser.setCurrentDirectory(new File(runConfDir));
            chooser.setMultiSelectionEnabled(false);
            int option = chooser.showOpenDialog(me);
            if (option == JFileChooser.APPROVE_OPTION) {
                File sf = chooser.getSelectedFile();
                String  fxml = sf.getAbsolutePath();


                List<CDefinedComponent> l = null;
                try {
                    l = JCTools.parseUserCompDef(fxml);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if(l!=null){
                    if(!JCTools.isNameUnique(l)){
                        return;
                    }
                    if(JCTools.isIdUnique(l)){
                        // get map of components that are changed (id, name or desc)
                        Map<String,CDefinedComponent> mp = updatePDB(l,false);
                        if(mp == null) return;

                        disableMenue();

                        ProgressUI pui = new ProgressUI("Do not exit the program. Updating. Please wait...");
                        final JProgressBar pb = pui.getBar();


                        Refactor task =  new Refactor(mp.values(),pui);
                        task.addPropertyChangeListener(
                                new PropertyChangeListener() {
                                    public void propertyChange(PropertyChangeEvent evt) {
                                        if ("progress".equals(evt.getPropertyName())) {
                                            pb.setValue((Integer) evt.getNewValue());
                                        }
                                    }
                                });
                        task.execute();

                    }
                } else {
                    JOptionPane.showMessageDialog(me,
                            "Error parsing component description user file",
                            "error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class Db2Xml extends AbstractAction {
        private Db2Xml() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Export");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            List<CDefinedComponent>  l= JCTools.getDescCompDatabase();
            // open a file in $COOL_HOME/expid/jcedit dir
            String fileName = stp.getCoolHome()+ File.separator+ stp.getExpid()+File.separator+
                    "jcedit"+File.separator+"component_db.xml";

            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
                bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                bw.write("<components>\n");
                for(CDefinedComponent com:l){
                    bw.write("  <component id =\""+com.getId()+"\">\n");
                    bw.write("    <name>"+com.getName()+"</name>\n");
                    bw.write("    <type>"+com.getType()+"</type>\n");
                    bw.write("    <description>"+com.getDescription()+"</description>\n");
                    bw.write("  </component>\n");
                }
                bw.write("</components>\n");
                bw.close();
            } catch (IOException e1) {
                e1.getMessage();
            }
        }
    }


    private class Refactor extends SwingWorker<Integer, Integer>{

        private Collection<CDefinedComponent> lcd;
        private ProgressUI pui;

        public Refactor(Collection<CDefinedComponent> lcd, ProgressUI pui){
            this.lcd = lcd;
            this.pui = pui;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            int i = 0;
            int s = lcd.size();
            int p;
            for(CDefinedComponent cm: lcd){

                refactor(cm.getType(), cm.getName(), cm.getId(), cm.getDescription());
                p = i++*100/s;
                setProgress(p);
            }
            return 0;
        }

        @Override
        protected void done() {
            super.done();
            enableMenue();
            pui.exit();
        }
    }


    private class ForceUpdate extends AbstractAction {
        private ForceUpdate() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Force Update ...");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
        }
    }

    private class GroupOnAction extends AbstractAction {
        private GroupOnAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Group On");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.ALT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.groupOn();
        }
    }

    private class GroupOffAction extends AbstractAction {
        private GroupOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Group Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.ALT_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.groupOff();
        }
    }

    private class RemoveFromGroupAction extends AbstractAction {
        private RemoveFromGroupAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Remove From The Group");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.removeFromGroup();
        }
    }

    private class GroupResetAction extends AbstractAction {
        private GroupResetAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Group Reset");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            drawingCanvas.groupReset();
        }
    }

    private class GroupModeCBAction extends AbstractAction {
        private GroupModeCBAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Group Mode");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(groupModeCheckBox.isSelected()){
                drawingCanvas.groupOn();
                groupModeCheckBox.setForeground(Color.RED);
                menuItem31.setEnabled(true);
                menuItem32.setEnabled(true);
            } else {
                drawingCanvas.groupOff();
                groupModeCheckBox.setForeground(new Color(0, 102, 102));
                menuItem31.setEnabled(false);
                menuItem32.setEnabled(false);
            }
        }
    }

    private class SupervisorProcess extends AbstractAction {
        private SupervisorProcess() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Supervisor Process");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(!configNameLabel.getText().equals("") && !configNameLabel.getText().equals("undefined")){
                if(drawingCanvas.getSupervisor()==null){
                    drawingCanvas.setSupervisor(new JCGComponent(configNameLabel.getText()));
                }
                new SupervisorForm(drawingCanvas, drawingCanvas.getSupervisor()).setVisible(true);
            }
        }
    }
}
