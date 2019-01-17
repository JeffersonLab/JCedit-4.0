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

/*
 * Created by JFormDesigner on Fri Oct 11 10:47:16 EDT 2013
 */

package org.jlab.coda.cedit.forms.simple;

import org.jlab.coda.cedit.cooldesktop.CDesktop;
import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Vardan Gyurjyan
 */
public class FCSForm extends JFrame {
    private DrawingCanvas parentCanvas;
    private JCGComponent component;
    private int processID;
    public FCSForm fForm;

    private SpinnerNumberModel priorityModel;

    private JCGSetup stp = JCGSetup.getInstance();


    public FCSForm(DrawingCanvas canvas, JCGComponent comp, boolean editable) {
        parentCanvas = canvas;
        component = comp;

        initComponents();
        // recreate processes combo box
        for(JCGProcess pr:component.getPrcesses()){
            processID++;
            addProcessCombo(pr.getName());
        }
        addProcessCombo("New...");

        nameTextField.setText(comp.getName());

        typeTextField.setText(comp.getType());

        idTextField.setText(Integer.toString(comp.getId()));
        farmProcessCommand.setText(comp.getCommand());
        nodeListTextArea.setText(comp.getNodeList());
        descriptionTextArea.setText(comp.getDescription());

        if(comp.getType().equals(ACodaType.FCS.name())){
            priorityModel = new SpinnerNumberModel(ACodaType.FCS.priority(),
                    ACodaType.FCS.priority(), ACodaType.FCS.priority()+100, 1);
        } else {
            JOptionPane.showMessageDialog(this,"Type Error","FCSForm",JOptionPane.ERROR_MESSAGE);
        }

        if(priorityModel!=null){
            prioritySpinner.setModel(priorityModel);
            if(comp.getPriority()>0){
                int v =  comp.getPriority();
                prioritySpinner.setValue(v);
            }
        }

        if(component.isPreDefined()){
            descriptionTextArea.setEnabled(false);
        }
        setVisible(true);
        if(!editable){
            nameTextField.setEnabled(false);
            prioritySpinner.setEnabled(false);
            farmProcessCommand.setEnabled(false);
            descriptionTextArea.setEnabled(false);
            processComboBox.setEnabled(false);
            okButton.setEnabled(false);
            clearButton.setEnabled(false);
            processButton.setEnabled(false);
        }

        fForm = this;
        String predefinedDescription = CDesktop.isComponentPredefined(nameTextField.getText().trim(),
                typeTextField.getText().trim(),
                component.getSubType(),
                descriptionTextArea.getText().replace("\\n", "\n"));
        if(predefinedDescription.equals("undefined")) {
            descriptionTextArea.setEnabled(true);
        } else {
            descriptionTextArea.setEnabled(false);
            descriptionTextArea.setText(predefinedDescription);
        }

    }

    public String getComponentName(){
        return component.getName();
    }

    public String getComponentType(){
        return component.getType();
    }

    public String getName(){
        return nameTextField.getText().trim();
    }


    public boolean isComponentDefinedOnCanvas(String name){
        int i=0;
        for(JCGComponent c:parentCanvas.getGCMPs().values()){
            if(c.getName().equals(name)) {
                i = i+1;
            }
        }
        return i > 1;
    }

    public void addProcessCombo(String name){
        for(int i=0;i<processComboBox.getItemCount();i++) {
            if(processComboBox.getItemAt(i).equals(name)) return;
        }
        processComboBox.addItem(name);
    }

    public void removeProcessCombo(String name){
        for(int i=0; i<processComboBox.getItemCount();i++){
            if(processComboBox.getItemAt(i).equals(name)){
                processComboBox.removeItemAt(i);
                return;
            }
        }
    }

    private void updateComponentInfo(){
        if(!nameTextField.getText().equals("")){

            String pName = component.getName();

            if(!nameTextField.getText().trim().equals(pName)){
                parentCanvas.linkDelete2(pName);
            }

            component.setName(nameTextField.getText().trim());
            typeTextField.setText(typeTextField.getText().trim().toUpperCase());
            component.setType(typeTextField.getText().trim());

            if((Integer)prioritySpinner.getValue()<ACodaType.getEnum(component.getType()).priority() ||
                    (Integer)prioritySpinner.getValue()>ACodaType.getEnum(component.getType()).priority()+100){
                component.setPriority(ACodaType.getEnum(component.getType()).priority());
            } else {
                component.setPriority((Integer)prioritySpinner.getValue());
            }

            component.setCommand(farmProcessCommand.getText().trim());
            component.setNodeList(nodeListTextArea.getText().trim());
            component.setDescription(descriptionTextArea.getText().replace("\\n","\n"));


            updateInMemory(pName);

            parentCanvas.repaint();
        }
    }

    public void updateInMemory(String pName){

        //@todo debug printouts
//        parentCanvas.dumpGCMPs();

        for(JCGComponent com:parentCanvas.getGCMPs().values()){
            for(JCGLink l:com.getLnks()){
                if(l.getSourceComponentName().equals(pName)){
                    l.setSourceComponentName(component.getName());
                    l.setSourceComponentType(component.getType());
                    l.setName(l.getSourceComponentName()+"_"+l.getDestinationComponentName());

                } else if(l.getDestinationComponentName().equals(pName)){
                    l.setDestinationComponentName(component.getName());
                    l.setDestinationComponentType(component.getType());
                    l.setName(l.getSourceComponentName()+"_"+l.getDestinationComponentName());
                    if(DrawingCanvas.getComp(l.getDestinationComponentName())!=null){
                        for(JCGTransport tr: DrawingCanvas.getComp(l.getDestinationComponentName()).getTrnsports()){
                            tr.setEtName("/tmp/et_" + stp.getExpid() + "_" +l.getDestinationComponentName());
                        }
                    }
                }
            }
        }

        if(!parentCanvas.getGCMPs().containsKey(component.getName())) {
            parentCanvas.getGCMPs().remove(pName);
        }
        parentCanvas.addgCmp(component);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        typeTextField = new JTextField();
        label5 = new JLabel();
        configFileLabel = new JLabel();
        idTextField = new JTextField();
        label11 = new JLabel();
        label13 = new JLabel();
        panel2 = new JPanel();
        processButton = new JButton();
        processComboBox = new JComboBox<>();
        label2 = new JLabel();
        prioritySpinner = new JSpinner();
        configFileLabel2 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        label3 = new JLabel();
        farmProcessCommand = new JTextField();
        label4 = new JLabel();
        scrollPane2 = new JScrollPane();
        nodeListTextArea = new JTextArea();
        okButton = new JButton();
        clearButton = new JButton();
        cancelButton = new JButton();
        separator1 = new JSeparator();
        action1 = new OkAction();
        action2 = new ClearAction();
        action3 = new CancelAction();
        action4 = new ProcessAction();

        //======== this ========
        setTitle("FarmManager");
        Container contentPane = getContentPane();

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Name");

                //---- typeTextField ----
                typeTextField.setEditable(false);

                //---- label5 ----
                label5.setText("Priority");

                //---- configFileLabel ----
                configFileLabel.setText("Node List");

                //---- idTextField ----
                idTextField.setEditable(false);
                idTextField.setText("auto");

                //---- label11 ----
                label11.setText("Type");

                //---- label13 ----
                label13.setText("ID");

                //======== panel2 ========
                {
                    panel2.setBorder(new TitledBorder("Process"));

                    //---- processButton ----
                    processButton.setAction(action4);
                    processButton.setText("Open");
                    processButton.setToolTipText("add, edit or remove processes");

                    //---- processComboBox ----
                    processComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                        "New..."
                    }));

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(processButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(processComboBox, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(22, Short.MAX_VALUE))
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(processButton)
                                    .addComponent(processComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(17, Short.MAX_VALUE))
                    );
                }

                //---- label2 ----
                label2.setText("( CSV )");
                label2.setEnabled(false);

                //---- prioritySpinner ----
                prioritySpinner.setModel(new SpinnerNumberModel(0, null, null, 1));

                //---- configFileLabel2 ----
                configFileLabel2.setText("Description");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(descriptionTextArea);
                }

                //---- label3 ----
                label3.setText("(optional)");
                label3.setEnabled(false);

                //---- label4 ----
                label4.setText("Command");

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(nodeListTextArea);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(label1)
                                        .addComponent(label5))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(prioritySpinner, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(label13)
                                        .addComponent(label11))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(idTextField, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(typeTextField, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(label3, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                                    .addGap(12, 12, 12)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                                    .addContainerGap())
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(configFileLabel))
                                    .addGap(18, 18, 18)
                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                                    .addContainerGap())
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(configFileLabel2))
                                    .addGap(0, 357, Short.MAX_VALUE))
                                .addComponent(label4, GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                                .addComponent(farmProcessCommand, GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label1)
                                .addComponent(label11)
                                .addComponent(typeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label5)
                                    .addComponent(label13)
                                    .addComponent(idTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addComponent(prioritySpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(label4))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGap(33, 33, 33)
                                    .addComponent(farmProcessCommand, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(configFileLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label2))
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(configFileLabel2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(label3)
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13))
                );
            }

            //---- okButton ----
            okButton.setAction(action1);
            okButton.setText("Ok");

            //---- clearButton ----
            clearButton.setAction(action2);

            //---- cancelButton ----
            cancelButton.setAction(action3);
            cancelButton.setText("Cancel");

            GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
            dialogPane.setLayout(dialogPaneLayout);
            dialogPaneLayout.setHorizontalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addContainerGap(436, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(cancelButton)
                        .addContainerGap())
                    .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
            );
            dialogPaneLayout.setVerticalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dialogPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(clearButton)
                            .addComponent(okButton)
                            .addComponent(cancelButton)))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField nameTextField;
    private JTextField typeTextField;
    private JLabel label5;
    private JLabel configFileLabel;
    private JTextField idTextField;
    private JLabel label11;
    private JLabel label13;
    private JPanel panel2;
    private JButton processButton;
    private JComboBox<String> processComboBox;
    private JLabel label2;
    private JSpinner prioritySpinner;
    private JLabel configFileLabel2;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JLabel label3;
    private JTextField farmProcessCommand;
    private JLabel label4;
    private JScrollPane scrollPane2;
    private JTextArea nodeListTextArea;
    private JButton okButton;
    private JButton clearButton;
    private JButton cancelButton;
    private JSeparator separator1;
    private OkAction action1;
    private ClearAction action2;
    private CancelAction action3;
    private ProcessAction action4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class ProcessAction extends AbstractAction {
        private ProcessAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Open");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            updateComponentInfo();
            if((processComboBox.getSelectedItem()).equals("New...")){

                // define a default name
                processID++;
                String tmpName = component.getName()+"_process_"+processID;

                // create a process
                JCGProcess gp = new JCGProcess();
                gp.setName(tmpName);

                // start a process form
                ProcessForm pf = new ProcessForm(fForm,parentCanvas,gp, true);
                pf.setVisible(true);
            } else {

                // open existing process in the form
                for(JCGProcess gp:component.getPrcesses()){
                    if((processComboBox.getSelectedItem()).equals(gp.getName())){

                        // start a process form
                        ProcessForm pf = new ProcessForm(fForm,parentCanvas,gp, false);
                        pf.setVisible(true);
                        break;
                    }
                }
            }
        }
    }

    private class CancelAction extends AbstractAction {
        private CancelAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Cancel");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class ClearAction extends AbstractAction {
        private ClearAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Clear");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
//            nameTextField.setText("");
            prioritySpinner.setValue(ACodaType.FCS.priority());
            farmProcessCommand.setText("");
            nodeListTextArea.setText("");

        }
    }

    private class OkAction extends AbstractAction {
        private OkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Ok");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(isComponentDefinedOnCanvas(nameTextField.getText())){
                JOptionPane.showMessageDialog(fForm,"Component with the name = "+nameTextField.getText()+
                        " exists","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            updateComponentInfo();
            dispose();
        }
    }


}
