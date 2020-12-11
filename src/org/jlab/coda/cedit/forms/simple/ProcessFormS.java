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

package org.jlab.coda.cedit.forms.simple;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGProcess;


public class ProcessFormS extends JFrame {

    private JCGComponent spv;
    private SupervisorForm sForm = null;
    private SComponentForm sCompForm = null;
    private JCGProcess gp;
    private DrawingCanvas canvas;
    private Pattern ptr = Pattern.compile("\\w+");


    public ProcessFormS(SupervisorForm sForm, DrawingCanvas canvas,JCGProcess gp, JCGComponent s) {
        this.gp = gp;
        this.canvas = canvas;
        this.sForm = sForm;
        spv = s;
        initComponents();
        prePostGroup   = new ButtonGroup();
        prePostGroup.add(beforeRadioButton);
        prePostGroup.add(afterRadioButton);
        update();
    }

    public ProcessFormS(SComponentForm compForm, DrawingCanvas canvas, JCGProcess gp, boolean isNew) {
        this.sCompForm = compForm;
        this.gp        = gp;
        this.canvas    = canvas;
        initComponents();
        prePostGroup   = new ButtonGroup();
        prePostGroup.add(beforeRadioButton);
        prePostGroup.add(afterRadioButton);
        update();
    }

    private void setCommandPS(String sp){
        if(sp.lastIndexOf(File.separator)>0){
            scriptPathTextField.setText(sp.substring(0,sp.lastIndexOf(File.separator)));
            scriptCommandTextField.setText((sp.substring(sp.lastIndexOf(File.separator)+1)));
        } else {
            scriptCommandTextField.setText(sp);
        }
    }

    private String getCommandPS(){
        if(scriptPathTextField.getText().trim().equals("")) {
            return scriptCommandTextField.getText().trim();
        }
        else {
            return scriptPathTextField.getText().trim()+File.separator+scriptCommandTextField.getText().trim();
        }
    }

    private void update(){
        nameTextField.setText(gp.getName());
        if(gp.isPeriodic()){
            periodicCheckBox.setSelected(true);
            peridicLabel.setForeground(Color.black);
            periodSpinner.setEnabled(true);
            periodSpinner.setValue(gp.getPeriod());
        }
        stateComboBox.setSelectedItem(gp.getTransition());

        if(gp.isBefore()){
            beforeRadioButton.setSelected(true);
            afterRadioButton.setSelected(false);
        }
        else if(gp.isAfter()){
            afterRadioButton.setSelected(true);
            beforeRadioButton.setSelected(false);
        }

        if(gp.isSync()){
            syncCheckBox.setSelected(true);
        } else {
            syncCheckBox.setSelected(false);
        }

        setCommandPS(gp.getScriptCommand());
        exitCodeSpinner.setValue(gp.getExitCode());

        sendSubjectTextField.setText(gp.getSendSubject());
        sendTypeTextField.setText(gp.getSendType());
        sendTextTextField.setText(gp.getSendText());
        if(gp.isSendRc())sendIsRcCheckBox.setSelected(true);

//        receiveSubjectTextField.setText(gp.getReceiveSubject());
//        receiveTypeTextField.setText(gp.getReceiveType());
//        receiveTextTextField.setText(gp.getReceiveText());
//        if(gp.isReceiveRc())receiveIsRcCheckBox.setSelected(true);
//
//        if(gp.isInitiator())initiatorCheckBox.setSelected(true);
    }

    private void scriptCommandTextFieldActionPerformed(ActionEvent e) {
        Matcher m = ptr.matcher(scriptCommandTextField.getText().trim());
        if(m.matches()){
            sendSubjectLabel.setForeground(Color.lightGray);
            sendTextLabel.setForeground(Color.lightGray);
            sendTypeLabel.setForeground(Color.lightGray);

            sendSubjectTextField.setEditable(false);
            sendTypeTextField.setEditable(false);
            sendTextTextField.setEditable(false);
            sendIsRcCheckBox.setEnabled(false);

//            recvSubjectLabel.setForeground(Color.lightGray);
//            recvTxtLabel.setForeground(Color.lightGray);
//            recvTypeLabel.setForeground(Color.lightGray);
//
//            receiveSubjectTextField.setEditable(false);
//            receiveTypeTextField.setEditable(false);
//            receiveTextTextField.setEditable(false);
//            receiveIsRcCheckBox.setEnabled(false);
//            initiatorCheckBox.setEnabled(false);

        } else {
            sendSubjectLabel.setForeground(Color.black);
            sendTextLabel.setForeground(Color.black);
            sendTypeLabel.setForeground(Color.black);

            sendSubjectTextField.setEditable(true);
            sendTypeTextField.setEditable(true);
            sendTextTextField.setEditable(true);
            sendIsRcCheckBox.setEnabled(true);

//            recvSubjectLabel.setForeground(Color.black);
//            recvTxtLabel.setForeground(Color.black);
//            recvTypeLabel.setForeground(Color.black);
//
//            receiveSubjectTextField.setEditable(true);
//            receiveTypeTextField.setEditable(true);
//            receiveTextTextField.setEditable(true);
//            receiveIsRcCheckBox.setEnabled(true);
//
//            initiatorCheckBox.setEnabled(true);
        }
    }

    private void scriptCommandTextFieldKeyTyped(KeyEvent e) {
        Matcher m = ptr.matcher(scriptCommandTextField.getText().trim());
        if(m.matches()){
            sendSubjectLabel.setForeground(Color.lightGray);
            sendTextLabel.setForeground(Color.lightGray);
            sendTypeLabel.setForeground(Color.lightGray);

            sendSubjectTextField.setEditable(false);
            sendTypeTextField.setEditable(false);
            sendTextTextField.setEditable(false);
            sendIsRcCheckBox.setEnabled(false);

//            recvSubjectLabel.setForeground(Color.lightGray);
//            recvTxtLabel.setForeground(Color.lightGray);
//            recvTypeLabel.setForeground(Color.lightGray);
//
//            receiveSubjectTextField.setEditable(false);
//            receiveTypeTextField.setEditable(false);
//            receiveTextTextField.setEditable(false);
//            receiveIsRcCheckBox.setEnabled(false);
//
//            initiatorCheckBox.setEnabled(false);
        } else {
            sendSubjectLabel.setForeground(Color.black);
            sendTextLabel.setForeground(Color.black);
            sendTypeLabel.setForeground(Color.black);

            sendSubjectTextField.setEditable(true);
            sendTypeTextField.setEditable(true);
            sendTextTextField.setEditable(true);
            sendIsRcCheckBox.setEnabled(true);

//            recvSubjectLabel.setForeground(Color.black);
//            recvTxtLabel.setForeground(Color.black);
//            recvTypeLabel.setForeground(Color.black);
//
//            receiveSubjectTextField.setEditable(true);
//            receiveTypeTextField.setEditable(true);
//            receiveTextTextField.setEditable(true);
//            receiveIsRcCheckBox.setEnabled(true);
//
//            initiatorCheckBox.setEnabled(true);

        }
    }

    private void sendSubjectTextFieldActionPerformed(ActionEvent e) {
        Matcher m = ptr.matcher(sendSubjectTextField.getText().trim());
        if(m.matches()){
            scriptCommandLabel.setForeground(Color.lightGray);
            scriptErrorLabel.setForeground(Color.lightGray);
            exitCodeSpinner.setEnabled(false);
            scriptCommandTextField.setEditable(false);
            scriptPathTextField.setEnabled(false);
        } else {
            scriptCommandLabel.setForeground(Color.black);
            scriptErrorLabel.setForeground(Color.black);
            exitCodeSpinner.setEnabled(true);
            scriptCommandTextField.setEditable(true);
            scriptPathTextField.setEnabled(true);
        }
    }

    private void sendSubjectTextFieldKeyTyped(KeyEvent e) {
        Matcher m = ptr.matcher(sendSubjectTextField.getText().trim());
        if(m.matches()){
            scriptCommandLabel.setForeground(Color.lightGray);
            scriptErrorLabel.setForeground(Color.lightGray);
            exitCodeSpinner.setEnabled(false);
            scriptCommandTextField.setEditable(false);
            scriptPathTextField.setEnabled(false);
        } else {
            scriptCommandLabel.setForeground(Color.black);
            scriptErrorLabel.setForeground(Color.black);
            exitCodeSpinner.setEnabled(true);
            scriptCommandTextField.setEditable(true);
            scriptPathTextField.setEnabled(true);
        }
    }

    private void receiveSubjectTextFieldActionPerformed(ActionEvent e) {
        Matcher m = ptr.matcher(sendSubjectTextField.getText().trim());
        if(m.matches()){
            scriptCommandLabel.setForeground(Color.lightGray);
            scriptErrorLabel.setForeground(Color.lightGray);
            exitCodeSpinner.setEnabled(false);
            scriptCommandTextField.setEditable(false);
        } else {
            scriptCommandLabel.setForeground(Color.black);
            scriptErrorLabel.setForeground(Color.black);
            exitCodeSpinner.setEnabled(true);
            scriptCommandTextField.setEditable(true);
        }
    }

    private void receiveSubjectTextFieldKeyTyped(KeyEvent e) {
        Matcher m = ptr.matcher(sendSubjectTextField.getText().trim());
        if(m.matches()){
            scriptCommandLabel.setForeground(Color.lightGray);
            scriptErrorLabel.setForeground(Color.lightGray);
            exitCodeSpinner.setEnabled(false);
            scriptCommandTextField.setEditable(false);
        } else {
            scriptCommandLabel.setForeground(Color.black);
            scriptErrorLabel.setForeground(Color.black);
            exitCodeSpinner.setEnabled(true);
            scriptCommandTextField.setEditable(true);
        }
    }

    private void scriptPathTextFieldKeyTyped(KeyEvent e) {
        Matcher m = ptr.matcher(scriptPathTextField.getText().trim());
        if(m.matches()){
            sendSubjectLabel.setForeground(Color.lightGray);
            sendTextLabel.setForeground(Color.lightGray);
            sendTypeLabel.setForeground(Color.lightGray);

            sendSubjectTextField.setEditable(false);
            sendTypeTextField.setEditable(false);
            sendTextTextField.setEditable(false);
            sendIsRcCheckBox.setEnabled(false);

        } else {
            sendSubjectLabel.setForeground(Color.black);
            sendTextLabel.setForeground(Color.black);
            sendTypeLabel.setForeground(Color.black);

            sendSubjectTextField.setEditable(true);
            sendTypeTextField.setEditable(true);
            sendTextTextField.setEditable(true);
            sendIsRcCheckBox.setEnabled(true);

        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        syncCheckBox = new JCheckBox();
        periodicCheckBox = new JCheckBox();
        peridicLabel = new JLabel();
        periodSpinner = new JSpinner();
        beforeRadioButton = new JRadioButton();
        afterRadioButton = new JRadioButton();
        label3 = new JLabel();
        stateComboBox = new JComboBox<>();
        panel1 = new JPanel();
        scriptCommandLabel = new JLabel();
        scriptCommandTextField = new JTextField();
        scriptErrorLabel = new JLabel();
        exitCodeSpinner = new JSpinner();
        label2 = new JLabel();
        scriptPathTextField = new JTextField();
        panel2 = new JPanel();
        panel3 = new JPanel();
        sendSubjectLabel = new JLabel();
        sendSubjectTextField = new JTextField();
        sendTypeLabel = new JLabel();
        sendTypeTextField = new JTextField();
        sendTextLabel = new JLabel();
        sendTextTextField = new JTextField();
        sendIsRcCheckBox = new JCheckBox();
        forceEndCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        saveButton = new JButton();
        removeButton = new JButton();
        cancelButton = new JButton();
        button1 = new JButton();
        action1 = new SaveAction();
        action2 = new CancelAction();
        action3 = new PeriodicCheckAction();
        action4 = new RemoveAction();
        action5 = new ClearAction();
        action6 = new SyncCheckBoxAction();
        action7 = new ForceEndScriptAction();

        //======== this ========
        setTitle("Process");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Name");

                //---- syncCheckBox ----
                syncCheckBox.setAction(action6);

                //---- periodicCheckBox ----
                periodicCheckBox.setAction(action3);

                //---- peridicLabel ----
                peridicLabel.setText("period (in sec.)");

                //---- periodSpinner ----
                periodSpinner.setEnabled(false);
                periodSpinner.setModel(new SpinnerNumberModel(-1, -1, null, 1));

                //---- beforeRadioButton ----
                beforeRadioButton.setText("before");
                beforeRadioButton.setSelected(true);

                //---- afterRadioButton ----
                afterRadioButton.setText("after");

                //---- label3 ----
                label3.setText("Transition");

                //---- stateComboBox ----
                stateComboBox.setMaximumRowCount(5);
                stateComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    "download",
                    "prestart",
                    "go",
                    "end",
                    "reset",
                    "pause",
                    "resume"
                }));

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder("Script"));

                    //---- scriptCommandLabel ----
                    scriptCommandLabel.setText("Command");

                    //---- scriptCommandTextField ----
                    scriptCommandTextField.addActionListener(e -> scriptCommandTextFieldActionPerformed(e));
                    scriptCommandTextField.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            scriptCommandTextFieldKeyTyped(e);
                        }
                    });

                    //---- scriptErrorLabel ----
                    scriptErrorLabel.setText("ExitCode");

                    //---- exitCodeSpinner ----
                    exitCodeSpinner.setModel(new SpinnerNumberModel(777, -200, 777, 1));

                    //---- label2 ----
                    label2.setText("Path");

                    //---- scriptPathTextField ----
                    scriptPathTextField.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            scriptPathTextFieldKeyTyped(e);
                        }
                    });

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(scriptErrorLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(exitCodeSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(scriptCommandLabel)
                                            .addComponent(label2))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(scriptCommandTextField)
                                            .addComponent(scriptPathTextField))))
                                .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label2)
                                    .addComponent(scriptPathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(scriptCommandLabel, GroupLayout.Alignment.TRAILING)
                                    .addComponent(scriptCommandTextField, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(scriptErrorLabel, GroupLayout.Alignment.TRAILING)
                                    .addComponent(exitCodeSpinner, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                    );
                }

                //======== panel2 ========
                {
                    panel2.setBorder(new TitledBorder("Messaging"));

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("Send"));

                        //---- sendSubjectLabel ----
                        sendSubjectLabel.setText("Subject");

                        //---- sendSubjectTextField ----
                        sendSubjectTextField.addActionListener(e -> sendSubjectTextFieldActionPerformed(e));
                        sendSubjectTextField.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                                sendSubjectTextFieldKeyTyped(e);
                            }
                        });

                        //---- sendTypeLabel ----
                        sendTypeLabel.setText("Type");

                        //---- sendTextLabel ----
                        sendTextLabel.setText("Text");

                        //---- sendIsRcCheckBox ----
                        sendIsRcCheckBox.setText("RC domain");
                        sendIsRcCheckBox.setToolTipText("cMsg RC domain");

                        GroupLayout panel3Layout = new GroupLayout(panel3);
                        panel3.setLayout(panel3Layout);
                        panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel3Layout.createParallelGroup()
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addGroup(panel3Layout.createParallelGroup()
                                                .addComponent(sendSubjectLabel)
                                                .addComponent(sendTypeLabel)
                                                .addComponent(sendTextLabel))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panel3Layout.createParallelGroup()
                                                .addComponent(sendTypeTextField)
                                                .addComponent(sendSubjectTextField)
                                                .addComponent(sendTextTextField)))
                                        .addGroup(panel3Layout.createSequentialGroup()
                                            .addComponent(sendIsRcCheckBox)
                                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sendSubjectLabel)
                                        .addComponent(sendSubjectTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sendTypeLabel)
                                        .addComponent(sendTypeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(sendTextLabel)
                                        .addComponent(sendTextTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(sendIsRcCheckBox))
                        );
                    }

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(18, Short.MAX_VALUE))
                    );
                }

                //---- forceEndCheckBox ----
                forceEndCheckBox.setAction(action7);

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(label1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nameTextField)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(syncCheckBox)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(periodicCheckBox, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(peridicLabel)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(periodSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(beforeRadioButton)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(afterRadioButton)
                                    .addGap(18, 18, 18)
                                    .addComponent(label3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(stateComboBox, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(forceEndCheckBox)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addGap(25, 25, 25))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(periodSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(peridicLabel)
                                .addComponent(periodicCheckBox)
                                .addComponent(syncCheckBox))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(beforeRadioButton)
                                    .addComponent(afterRadioButton)
                                    .addComponent(label3))
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(stateComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(forceEndCheckBox)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                //---- saveButton ----
                saveButton.setAction(action1);
                saveButton.setText("Save");

                //---- removeButton ----
                removeButton.setAction(action4);
                removeButton.setText("Remove");

                //---- cancelButton ----
                cancelButton.setAction(action2);
                cancelButton.setText("Cancel");

                //---- button1 ----
                button1.setAction(action5);

                GroupLayout buttonBarLayout = new GroupLayout(buttonBar);
                buttonBar.setLayout(buttonBarLayout);
                buttonBarLayout.setHorizontalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createSequentialGroup()
                            .addGap(171, 171, 171)
                            .addComponent(saveButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(removeButton, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
                buttonBarLayout.setVerticalGroup(
                    buttonBarLayout.createParallelGroup()
                        .addGroup(buttonBarLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(saveButton)
                            .addComponent(removeButton)
                            .addComponent(button1)
                            .addComponent(cancelButton))
                );
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of compForm initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField nameTextField;
    private JCheckBox syncCheckBox;
    private JCheckBox periodicCheckBox;
    private JLabel peridicLabel;
    private JSpinner periodSpinner;
    private JRadioButton beforeRadioButton;
    private JRadioButton afterRadioButton;
    private JLabel label3;
    private JComboBox<String> stateComboBox;
    private JPanel panel1;
    private JLabel scriptCommandLabel;
    private JTextField scriptCommandTextField;
    private JLabel scriptErrorLabel;
    private JSpinner exitCodeSpinner;
    private JLabel label2;
    private JTextField scriptPathTextField;
    private JPanel panel2;
    private JPanel panel3;
    private JLabel sendSubjectLabel;
    private JTextField sendSubjectTextField;
    private JLabel sendTypeLabel;
    private JTextField sendTypeTextField;
    private JLabel sendTextLabel;
    private JTextField sendTextTextField;
    private JCheckBox sendIsRcCheckBox;
    private JCheckBox forceEndCheckBox;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton removeButton;
    private JButton cancelButton;
    private JButton button1;
    private SaveAction action1;
    private CancelAction action2;
    private PeriodicCheckAction action3;
    private RemoveAction action4;
    private ClearAction action5;
    private SyncCheckBoxAction action6;
    private ForceEndScriptAction action7;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    ButtonGroup prePostGroup;

    private class SaveAction extends AbstractAction {
        private SaveAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Save");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(!nameTextField.getText().equals("")){
                gp.setName(nameTextField.getText().trim());

                if(syncCheckBox.isSelected()){
                    gp.setSync(true);
                } else {
                    gp.setSync(false);
                }

                if(periodicCheckBox.isSelected()){
                    gp.setPeriodic(true);
                    gp.setPeriod((Integer)periodSpinner.getValue());
                } else {
                    gp.setPeriodic(false);
                    gp.setPeriod(-1);
                }


                gp.setTransition( (String)stateComboBox.getSelectedItem());

                if(beforeRadioButton.isSelected()){
                    gp.setBefore(true);
                    gp.setAfter(false);
                }
                else if(afterRadioButton.isSelected()){
                    gp.setAfter(true);
                    gp.setBefore(false);
                }

                if(exitCodeSpinner.isEnabled()){
                    gp.setScriptCommand(getCommandPS());
                    gp.setExitCode((Integer)exitCodeSpinner.getValue());
                }

                if(sendSubjectTextField.isEditable()){
                    gp.setSendSubject(sendSubjectTextField.getText().trim());
                    gp.setSendType(sendTypeTextField.getText().trim());
                    gp.setSendText(sendTextTextField.getText().trim());
                    if(sendIsRcCheckBox.isSelected())gp.setSendRc(true);
//                    if(initiatorCheckBox.isSelected())gp.setInitiator(true);
                }

//                if(receiveSubjectTextField.isEnabled()){
//                    gp.setReceiveSubject(receiveSubjectTextField.getText().trim());
//                    gp.setReceiveType(receiveTypeTextField.getText().trim());
//                    gp.setReceiveText(receiveTextTextField.getText().trim());
//                    if(receiveIsRcCheckBox.isSelected())gp.setSendRc(true);
//                    if(initiatorCheckBox.isSelected())gp.setInitiator(true);
//
//                }

                // add process to the component
                if(sCompForm!=null){
                    canvas.getGCMPs().get(sCompForm.getNameFromTextField()).addPrcess(gp);

                    // add process name to the combo box of the component form
                    sCompForm.addProcessCombo(gp.getName());
                }  else if(sForm!=null){
                    canvas.getSupervisor().addPrcess(gp);
                    sForm.addProcessCombo(gp.getName());
                }
                dispose();

            }
        }
    }

    private class CancelAction extends AbstractAction {
        private CancelAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Cancel");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    private class PeriodicCheckAction extends AbstractAction {
        private PeriodicCheckAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "periodic");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(periodicCheckBox.isSelected()){
                peridicLabel.setForeground(Color.black);
                periodSpinner.setEnabled(true);
                syncCheckBox.setEnabled(false);
            } else {
                peridicLabel.setForeground(Color.lightGray);
                periodSpinner.setEnabled(false);
                syncCheckBox.setEnabled(true);
            }
        }
    }

    private class RemoveAction extends AbstractAction {
        private RemoveAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Remove");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            periodicCheckBox.setSelected(false);

            peridicLabel.setForeground(Color.lightGray);
            periodSpinner.setEnabled(false);

            stateComboBox.setSelectedItem("downloaded");

            beforeRadioButton.setSelected(false);
            afterRadioButton.setSelected(false);

            scriptCommandTextField.setText("");
            exitCodeSpinner.setValue(777);

            sendSubjectTextField.setText("");
            sendTypeTextField.setText("");
            sendTextTextField.setText("");
            sendIsRcCheckBox.setSelected(false);

//            receiveSubjectTextField.setText("");
//            receiveTypeTextField.setText("");
//            receiveTextTextField.setText("");
//            receiveIsRcCheckBox.setSelected(false);
//
//            initiatorCheckBox.setSelected(true);

            // remove process from the component
            if(sCompForm!=null){
                canvas.getGCMPs().get(sCompForm.getNameFromTextField()).removePrcess((gp));

                // remove process name from the combo box of the component form
                sCompForm.removeProcessCombo(gp.getName());

            // Supervisor
            } else if(sForm!=null && spv!=null){
                spv.removePrcess(gp);

                // remove process name from the combo box of the component form
                sForm.removeProcessCombo(gp.getName());
            }
            dispose();
        }
    }

    private class ClearAction extends AbstractAction {
        private ClearAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Clear");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            periodicCheckBox.setSelected(false);

            peridicLabel.setForeground(Color.lightGray);
            periodSpinner.setEnabled(false);

            stateComboBox.setSelectedItem("downloaded");

            beforeRadioButton.setSelected(false);
            afterRadioButton.setSelected(false);

            scriptCommandTextField.setText("");
            exitCodeSpinner.setValue(777);

            sendSubjectTextField.setText("");
            sendTypeTextField.setText("");
            sendTextTextField.setText("");
            sendIsRcCheckBox.setSelected(false);

//            receiveSubjectTextField.setText("");
//            receiveTypeTextField.setText("");
//            receiveTextTextField.setText("");
//            receiveIsRcCheckBox.setSelected(false);
//
//            initiatorCheckBox.setSelected(true);
        }
    }

    private class SyncCheckBoxAction extends AbstractAction {
        private SyncCheckBoxAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "sync");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(syncCheckBox.isSelected()){
                periodicCheckBox.setEnabled(false);
                periodSpinner.setEnabled(false);
            } else {
                periodicCheckBox.setEnabled(true);
                periodSpinner.setEnabled(true);
            }
        }
    }

    private class ForceEndScriptAction extends AbstractAction {
        private ForceEndScriptAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            putValue(NAME, "Force End Script");
            setEnabled(false);
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
//            nameTextField.setText();
            // TODO add your code here
        }
    }
}
