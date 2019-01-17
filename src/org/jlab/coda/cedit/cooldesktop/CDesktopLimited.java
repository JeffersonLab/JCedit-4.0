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

import org.jlab.coda.cedit.parsers.extconfig.CoolDatabaseBrowser;
import org.jlab.coda.cedit.parsers.extconfig.EmuConfigReader;
import org.jlab.coda.cedit.parsers.extconfig.RocConfigReader;
import org.jlab.coda.cedit.system.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Vardan Gyurjyan
 */
public class CDesktopLimited extends JFrame {
    private DrawingCanvas drawingCanvas;
    private double w = 80, h = 80;
    private CCanvas cnvs;
    private CoolDatabaseBrowser coolDbBrowser;

    private String runType;

    private JCGSetup stp = JCGSetup.getInstance();

    public static void main(String[] args) {
        new CDesktopLimited(args[0]);
    }

    public CDesktopLimited(String runType) {
        initComponents();

        if(stp.createCoolDatabase(stp.getExpid())<0){
            expidLabel.setText("undefined");
        }
        else {
            expidLabel.setText(stp.getExpid());
        }
        coolDbBrowser = new CoolDatabaseBrowser();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });
        expidLabel.setText(stp.getExpid());
        setSize(900, 500);
        setLocation(200, 200);

        this.runType = runType;
        drawingCanvas.setGridVisible(true);
        drawingCanvas.repaint();
        showConfiguration(runType);
        drawingCanvas.repaint();
        setVisible(true);

    }

    public void showConfiguration(String runType){

        Map<String, JCGComponent> comMap = null ;
        drawingCanvas.removeAll();
        cnvs.clearPositionMap();

        configNameLabel.setText(runType);
        try {
            comMap = coolDbBrowser.JLC(runType);
        } catch (ExecutionException | InterruptedException e1) {
            e1.printStackTrace();
        }


        if (comMap != null) {

            for (JCGComponent c : comMap.values()) {
                if (!c.getName().equals(runType)) {
                    String type = c.getType();
                    c.setImage(drawingCanvas.createBufferedImage(File.separator + "resources" + File.separator + type + ".png"));

                    includeUserConfigFileEdits(c);
                    drawingCanvas.addgCmp(c);
                } else {
                    drawingCanvas.setSupervisor(c);
                }
            }
            drawingCanvas.unZoom();
        }
    }


    public void includeUserConfigFileEdits(JCGComponent com){
        String type = com.getType();
        // update component with the possible user edits of the configuration files ( .dat and .xml files)
        // roc configure updates .dat file
        if(type.equals(ACodaType.ROC.name()) ||
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
                    if(cpt.getName().equals("configFile")){
                        com.setUserConfig(cpt.getValue());
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
                                com.setRol1UsrString(tr1.substring(0,tr1.indexOf("}")));
                            }
                        }
                        if (trl2!=null){
                            StringTokenizer r2 = new StringTokenizer(trl2);
                            if(r2.hasMoreTokens()){
                                com.setRol2(r2.nextToken());
                            }
                            if(r2.hasMoreTokens()){
                                String tr2 = r2.nextToken();
                                com.setRol2UsrString(tr2.substring(0,tr2.indexOf("}")));
                            }
                        }
                    }
                }


                boolean breakFlag = false;
                for(JCGLink l:com.getLnks()){
                    if(DrawingCanvas.getComp(l.getDestinationComponentName())!=null){
                        for(JCGTransport tt:DrawingCanvas.getComp(l.getDestinationComponentName()).getTrnsports()) {
                            if(tt.getName().equals(l.getDestinationTransportName())){

                                for(JCGConcept cpt:cf.getConfigData()){

                                    if(cpt.getName().equals("output")){
                                        tt.setTransClass(cpt.getValue());
                                    }

                                    if(tt.getTransClass().equals("Et")){
                                        if(cpt.getName().equals("etName")){
                                            tt.setEtName(cpt.getValue());
                                        }

                                        if(tt.getEtMethodCon().equals("direct")){
                                            if(cpt.getName().equals("etHost")){
                                                tt.setEtHostName(cpt.getValue());
                                            }

                                            if(cpt.getName().equals("etPort")){
                                                try {
                                                    tt.setEtTcpPort(Integer.parseInt(cpt.getValue()));
                                                } catch (NumberFormatException e1) {
                                                    System.out.println("Error: et tcp port is not a number.");
                                                }
                                            }

                                        } else if(tt.getEtMethodCon().equals("mcast")){
                                            if(cpt.getName().equals("etHost")){
                                                tt.setmAddress(cpt.getValue());
                                            }

                                            if(cpt.getName().equals("etPort")){
                                                try {
                                                    tt.setEtUdpPort(Integer.parseInt(cpt.getValue()));
                                                } catch (NumberFormatException e1) {
                                                    System.out.println("Error: et udp port is not a number.");
                                                }
                                            }
                                        }
                                    } else if(tt.getTransClass().equals("File")){
                                        if(cpt.getName().equals("dataFile")){
                                            tt.setFileName(cpt.getValue());
                                        }
                                        if(cpt.getName().equals("fileType")){
                                            tt.setFileType(cpt.getValue());
                                        }

                                    } else if(tt.getTransClass().equals("None") ||
                                            tt.getTransClass().equals("Debug")){
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // emu configure updates .xml file
        } else if(type.equals(ACodaType.DC.name()) ||
                type.equals(ACodaType.SEB.name()) ||
                type.equals(ACodaType.PEB.name())){
            EmuConfigReader rd = new EmuConfigReader(runType, com.getName());
            if(rd.isConfigExists()){
                JCGCompConfig cf = rd.parseConfig();
                Set<JCGConcept> rc = cf.getConfigData();
                //@todo not implemented
            }
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        menu3 = new JMenu();
        menu4 = new JMenu();
        menuItem21 = new JMenuItem();
        menuItem19 = new JMenuItem();
        menuItem20 = new JMenuItem();
        menuItem10 = new JMenuItem();
        menuItem9 = new JMenuItem();
        scrollPane2 = new JScrollPane();
        panel2 = new JPanel();
        expidLabel = new JLabel();
        panel3 = new JPanel();
        configNameLabel = new JLabel();
        linkModeCheckBox = new JCheckBox();
        action1 = new ExitAction();
        action4 = new ZoomOutAction();
        action7 = new ZoomInAction();
        action21 = new GridAlignAction();
        action19 = new GVisibleOnAction();
        action20 = new GVisibleOffAction();
        action11 = new LinkModeAction();

        //======== this ========
        setTitle("JCedit Viewer");
        Container contentPane = getContentPane();

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- menuItem1 ----
                menuItem1.setAction(action1);
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);

            //======== menu3 ========
            {
                menu3.setText("Control");

                //======== menu4 ========
                {
                    menu4.setText("Grid");

                    //---- menuItem21 ----
                    menuItem21.setAction(action21);
                    menuItem21.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
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

                //---- menuItem10 ----
                menuItem10.setAction(action7);
                menuItem10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
                menu3.add(menuItem10);
                menu3.addSeparator();

                //---- menuItem9 ----
                menuItem9.setAction(action4);
                menuItem9.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
                menu3.add(menuItem9);
            }
            menuBar1.add(menu3);
        }
        setJMenuBar(menuBar1);

        //======== scrollPane2 ========
        {
            drawingCanvas = new DrawingCanvas(w,h,false);
            scrollPane2.setViewportView(drawingCanvas);
                // Add a drop target to the drawingCanvas
             cnvs = new CCanvas(drawingCanvas);
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder(null, "Database", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
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
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addComponent(configNameLabel, GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }

        //---- linkModeCheckBox ----
        linkModeCheckBox.setAction(action11);
        linkModeCheckBox.setForeground(new Color(0, 102, 102));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(45, 45, 45)
                            .addComponent(linkModeCheckBox, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 474, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 1141, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(linkModeCheckBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
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
    private JMenuItem menuItem1;
    private JMenu menu3;
    private JMenu menu4;
    private JMenuItem menuItem21;
    private JMenuItem menuItem19;
    private JMenuItem menuItem20;
    private JMenuItem menuItem10;
    private JMenuItem menuItem9;
    private JScrollPane scrollPane2;
    private JPanel panel2;
    private JLabel expidLabel;
    private JPanel panel3;
    private JLabel configNameLabel;
    private JCheckBox linkModeCheckBox;
    private ExitAction action1;
    private ZoomOutAction action4;
    private ZoomInAction action7;
    private GridAlignAction action21;
    private GVisibleOnAction action19;
    private GVisibleOffAction action20;
    private LinkModeAction action11;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class DeleteSessionAction extends AbstractAction {
        private DeleteSessionAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
        }
    }

    private class DeleteAction extends AbstractAction {
        private DeleteAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            // TODO add your code here
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
            } else {
                drawingCanvas.linkModeOff();
                linkModeCheckBox.setForeground(new Color(0, 102, 102));
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
            // TODO add your code here
        }
    }

    private class DeleteLinkAction extends AbstractAction {
        private DeleteLinkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Delete Link");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            // TODO add your code here
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

    private class SynArrOffAction extends AbstractAction {
        private SynArrOffAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Dynamic Off");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK|KeyEvent.SHIFT_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
        }
    }

    private class OpenAction extends AbstractAction {
        private OpenAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Open");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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
            // TODO add your code here
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

    private class ExitAction extends AbstractAction {
        private ExitAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Exit");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }
}
