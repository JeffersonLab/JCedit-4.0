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
 * Created by JFormDesigner on Fri May 16 15:22:20 EDT 2014
 */

package org.jlab.coda.cedit.forms.simple;

import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGProcess;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Vardan Gyurjyan
 */
public class SupervisorForm extends JFrame {
    private int processID;
    private JCGComponent superv;
    private DrawingCanvas parentCanvas;
    private SupervisorForm sForm;
    private SupervisorForm me;

    public SupervisorForm(DrawingCanvas canvas,JCGComponent superv) {
        this.superv = superv;
        parentCanvas = canvas;
        initComponents();
        // recreate processes combo box
        for(JCGProcess pr:superv.getPrcesses()){
            processID++;
            addProcessCombo(pr.getName());
        }
        addProcessCombo("New...");

        nameTextField.setText(superv.getName());
        sForm = this;
        setSize(500,235);
        me = this;
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

    public String getSupervisorName(){
        return superv.getName();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        panel2 = new JPanel();
        processButton = new JButton();
        processComboBox = new JComboBox<>();
        separator1 = new JSeparator();
        okButton = new JButton();
        cancelButton = new JButton();
        button1 = new JButton();
        action1 = new OkAction();
        action3 = new CancelAction();
        action4 = new ProcessAction();
        action5 = new ApplyAlsoAction();

        //======== this ========
        setTitle("Component");
        Container contentPane = getContentPane();

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Name");

                //---- nameTextField ----
                nameTextField.setEditable(false);

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
                                .addComponent(processComboBox, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(processButton)
                                    .addComponent(processComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }

                //---- okButton ----
                okButton.setAction(action1);
                okButton.setText("Apply");

                //---- cancelButton ----
                cancelButton.setAction(action3);
                cancelButton.setText("Cancel");

                //---- button1 ----
                button1.setAction(action5);

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(GroupLayout.Alignment.LEADING, contentPanelLayout.createSequentialGroup()
                                            .addComponent(label1)
                                            .addGap(15, 15, 15)
                                            .addComponent(nameTextField))
                                        .addComponent(panel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(29, 29, 29))
                                .addComponent(separator1, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(okButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                    .addComponent(cancelButton)
                                    .addGap(56, 56, 56))))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(okButton)
                                .addComponent(cancelButton)
                                .addComponent(button1))
                            .addContainerGap())
                );
            }

            GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
            dialogPane.setLayout(dialogPaneLayout);
            dialogPaneLayout.setHorizontalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(dialogPaneLayout.createSequentialGroup()
                        .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
            );
            dialogPaneLayout.setVerticalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(7, 7, 7))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(1, 1, 1))
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
    private JPanel panel2;
    private JButton processButton;
    private JComboBox<String> processComboBox;
    private JSeparator separator1;
    private JButton okButton;
    private JButton cancelButton;
    private JButton button1;
    private OkAction action1;
    private CancelAction action3;
    private ProcessAction action4;
    private ApplyAlsoAction action5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class ProcessAction extends AbstractAction {
        private ProcessAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Open");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if((processComboBox.getSelectedItem()).equals("New...")){

                // define a default name
                processID++;
                String tmpName = superv.getName()+"_process_"+processID;

                // create a process
                JCGProcess gp = new JCGProcess();
                gp.setName(tmpName);

                // start a process form
                ProcessForm pf = new ProcessForm(sForm, parentCanvas,gp, superv);
                pf.setVisible(true);
            } else {

                // open existing process in the form
                for(JCGProcess gp:superv.getPrcesses()){
                    if((processComboBox.getSelectedItem()).equals(gp.getName())){

                        // start a process form
                        ProcessForm pf = new ProcessForm(sForm, parentCanvas,gp, superv);
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

    private class OkAction extends AbstractAction {
        private OkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "Apply");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
           dispose();
        }
    }

    private class ApplyAlsoAction extends AbstractAction {
        private ApplyAlsoAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "ApplyAlso");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
           new RunTypeList(me).setVisible(true);
        }
    }
}
