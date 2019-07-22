
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

import javax.swing.event.*;
import org.jlab.coda.cedit.cooldesktop.CDesktop;
import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGTransport;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author Vardan Gyurjyan
 */
public class SOutputForm extends JFrame {
    private DrawingCanvas canvas;
    private JCGComponent component;
    private JCGLink glob;
    private SOutputForm me;
    private String pName, pSubType;
    private JCGSetup stp = JCGSetup.getInstance();

    public SOutputForm(DrawingCanvas canvas, JCGComponent comp, boolean editable) {
        this.canvas = canvas;
        component = comp;
        pName = component.getName();
        pSubType = component.getSubType();

        initComponents();
        nameTextField.setText(comp.getName());
        descriptionTextArea.setText(comp.getDescription());
        if(!comp.getDescription().equals("undefined")){
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setEnabled(false);
        }

        update();
        me = this;
        if(!editable){
            nameTextField.setEnabled(false);
            transportClassComboBox.setEnabled(false);
            descriptionTextArea.setEnabled(false);
            connectionMethodComboBox.setEnabled(false);
            etHostTextField.setEnabled(false);
            etTcpPortSpinner.setEnabled(false);
            etUdpPortSpinner.setEnabled(false);
            mAddressTextField.setEnabled(false);
            fileNameTextField.setEnabled(false);
            fileTypeComboBox.setEnabled(false);
            fileSplitSpinner.setEnabled(false);
            fileInternalBuffer.setEnabled(false);
            okButton.setEnabled(false);
            clearButton.setEnabled(false);

            compressionCheckBox.setEnabled(false);
            compressionModeComboBox.setEnabled(false);
            compressionThreadsSpinner.setEnabled(false);
        }
    }

    public SOutputForm(DrawingCanvas canvas, JCGComponent comp, JCGLink l, boolean editable) {
        this.glob = l;
        this.canvas = canvas;
        component = comp;
        initComponents();
        nameTextField.setText(comp.getName());
        descriptionTextArea.setText(comp.getDescription());
        if(!comp.getDescription().equals("")){
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setEnabled(false);
        }

        update();
        me = this;
        if(!editable){
            nameTextField.setEnabled(false);
            transportClassComboBox.setEnabled(false);
            descriptionTextArea.setEnabled(false);
            connectionMethodComboBox.setEnabled(false);
            etHostTextField.setEnabled(false);
            etTcpPortSpinner.setEnabled(false);
            etUdpPortSpinner.setEnabled(false);
            mAddressTextField.setEnabled(false);
            fileNameTextField.setEnabled(false);
            fileTypeComboBox.setEnabled(false);
            fileSplitSpinner.setEnabled(false);
            fileInternalBuffer.setEnabled(false);
            okButton.setEnabled(false);
            clearButton.setEnabled(false);

            compressionCheckBox.setEnabled(false);
            compressionModeComboBox.setEnabled(false);
            compressionThreadsSpinner.setEnabled(false);

        }
    }

    private void update() {
        JCGTransport gt=null;
        String tName = component.getName() + "_transport";
        if(component.getTrnsports()!=null){
            if(component.getTrnsports().isEmpty()){

                gt = new JCGTransport();
                gt.setName(tName);
                gt.setTransClass(component.getSubType());
                if(gt.getTransClass().equalsIgnoreCase("")){
                    gt.setTransClass("Et");
                    gt.setEtName("/tmp/et_" + stp.getExpid() + "_" + component.getName());
                }
            }
            else {
                for(JCGTransport tt:component.getTrnsports()){
                    gt = tt;
                    gt.setName(tName);
//                    if(tt.getNameFromTextField().equals(tName)){
//                        gt = tt;
//                    }
                    break;
                }
            }
        } else {
            gt = new JCGTransport();
            gt.setName(tName);
            gt.setTransClass(component.getSubType());
            if(gt.getTransClass().equalsIgnoreCase("")){
                gt.setTransClass("Et");
                gt.setEtName("/tmp/et_" + stp.getExpid() + "_" + component.getName());
            }
        }

        // fill the form

        transportClassComboBox.setSelectedItem(gt.getTransClass());
        etNameTextField.setText(gt.getEtName());
        etHostTextField.setText(gt.getEtHostName());
        etTcpPortSpinner.setValue(gt.getEtTcpPort());
        etUdpPortSpinner.setValue(gt.getEtUdpPort());
        mAddressTextField.setText(gt.getmAddress());
        connectionMethodComboBox.setSelectedItem(gt.getEtMethodCon());
        fileNameTextField.setText(gt.getFileName());
        if(gt.getFileSplit()<0){
            fileSplitSpinner.setValue(200);
        }
        else {
        fileSplitSpinner.setValue((int)(gt.getFileSplit()/10000000));
        }
        fileInternalBuffer.setSelectedItem(String.valueOf(gt.getFileInternalBuffer()));
        fileTypeComboBox.setSelectedItem(gt.getFileType());

        checkTrClass();
        if(component.isPreDefined()){
            descriptionTextArea.setEnabled(false);
        }

        // Compression
        compressionModeComboBox.setSelectedIndex(gt.getCompression());
        compressionThreadsSpinner.setValue(gt.getCompressionThreads());
        if(gt.getCompression() > 0) {
          compressionCheckBox.setSelected(true);
            compressionModeComboBox.setEnabled(true);
            compressionThreadsSpinner.setEnabled(true);
        } else {
            compressionCheckBox.setSelected(false);
            compressionModeComboBox.setEnabled(false);
            compressionThreadsSpinner.setEnabled(false);
        }

    }

    public void checkTrClass(){
        // control access to the form
        if(transportClassComboBox.getSelectedItem().equals("Et")){

            etNameTextField.setEnabled(true);
            etNameTextField.setText("/tmp/et_" + stp.getExpid() + "_" +nameTextField.getText().trim());
            etNameLabel.setForeground(Color.black);
            etConnectionMethodLabel.setForeground(Color.black);
            connectionMethodComboBox.setEnabled(true);

            if(connectionMethodComboBox.getSelectedItem().equals("direct")){
                etTcpPortLabel.setForeground(Color.black);
                etTcpPortSpinner.setEnabled(true);
                etHostLabel.setForeground(Color.black);
                etHostTextField.setEnabled(true);

                etMAddressLabel.setForeground(Color.lightGray);
                mAddressTextField.setEnabled(false);
                etUdpPortLabel.setForeground(Color.lightGray);
                etUdpPortSpinner.setEnabled(false);
            } else {
                etUdpPortLabel.setForeground(Color.black);
                etUdpPortSpinner.setEnabled(true);
                etMAddressLabel.setForeground(Color.black);
                mAddressTextField.setEnabled(true);

                etTcpPortLabel.setForeground(Color.lightGray);
                etTcpPortSpinner.setEnabled(false);
                etHostLabel.setForeground(Color.lightGray);
                etHostTextField.setEnabled(false);
            }

            fileNameTextField.setEnabled(false);
            fileNameLabel.setForeground(Color.lightGray);
            fileSplitSpinner.setEnabled(false);
            fileInternalBuffer.setEnabled(false);
            fileSplitLabel.setForeground(Color.lightGray);
            fileTypeComboBox.setEnabled(false);
            FileTypeLabel.setForeground(Color.lightGray);

            compressionCheckBox.setEnabled(false);
            compressionModeComboBox.setEnabled(false);
            compressionThreadsSpinner.setEnabled(false);

        } else if (transportClassComboBox.getSelectedItem().equals("File")){
            etNameTextField.setEnabled(false);
            etNameTextField.setText("/tmp/et_" + stp.getExpid() + "_" +nameTextField.getText().trim());
            etNameLabel.setForeground(Color.lightGray);
            etHostTextField.setEnabled(false);
            etHostLabel.setForeground(Color.lightGray);
            etTcpPortSpinner.setEnabled(false);
            etTcpPortLabel.setForeground(Color.lightGray);
            etUdpPortSpinner.setEnabled(false);
            etUdpPortLabel.setForeground(Color.lightGray);
            mAddressTextField.setEnabled(false);
            etMAddressLabel.setForeground(Color.lightGray);
            connectionMethodComboBox.setEnabled(false);
            etConnectionMethodLabel.setForeground(Color.lightGray);

            fileNameTextField.setEnabled(true);
            fileNameLabel.setForeground(Color.black);
            fileSplitSpinner.setEnabled(true);
            fileInternalBuffer.setEnabled(true);
            fileSplitLabel.setForeground(Color.black);
            fileTypeComboBox.setEnabled(true);
            FileTypeLabel.setForeground(Color.black);

            compressionCheckBox.setEnabled(true);
            compressionModeComboBox.setEnabled(true);
            compressionThreadsSpinner.setEnabled(true);


        } else if (transportClassComboBox.getSelectedItem().equals("None") ||
                transportClassComboBox.getSelectedItem().equals("Debug")){
            etNameTextField.setEnabled(false);
            etNameTextField.setText("/tmp/et_" + stp.getExpid() + "_" +nameTextField.getText().trim());
            etNameLabel.setForeground(Color.lightGray);
            etHostTextField.setEnabled(false);
            etHostLabel.setForeground(Color.lightGray);
            etTcpPortSpinner.setEnabled(false);
            etTcpPortLabel.setForeground(Color.lightGray);
            etUdpPortSpinner.setEnabled(false);
            etUdpPortLabel.setForeground(Color.lightGray);
            mAddressTextField.setEnabled(false);
            etMAddressLabel.setForeground(Color.lightGray);
            connectionMethodComboBox.setEnabled(false);
            etConnectionMethodLabel.setForeground(Color.lightGray);

            fileNameTextField.setEnabled(false);
            fileNameLabel.setForeground(Color.lightGray);
            fileSplitSpinner.setEnabled(false);
            fileInternalBuffer.setEnabled(false);
            fileSplitLabel.setForeground(Color.lightGray);
            fileTypeComboBox.setEnabled(false);
            FileTypeLabel.setForeground(Color.lightGray);

            compressionCheckBox.setEnabled(true);
            compressionModeComboBox.setEnabled(true);
            compressionThreadsSpinner.setEnabled(true);

        }
    }

    private void compressionCheckBoxStateChanged(ChangeEvent e) {
        if(compressionCheckBox.isSelected()){
            compressionModeComboBox.setEnabled(true);
            compressionThreadsSpinner.setEnabled(true);
        } else {
            compressionModeComboBox.setEnabled(false);
            compressionThreadsSpinner.setEnabled(false);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        label2 = new JLabel();
        transportClassComboBox = new JComboBox<>();
        label1 = new JLabel();
        nameTextField = new JTextField();
        label3 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        panel2 = new JPanel();
        etNameLabel = new JLabel();
        etNameTextField = new JTextField();
        etHostLabel = new JLabel();
        etHostTextField = new JTextField();
        etTcpPortLabel = new JLabel();
        etUdpPortLabel = new JLabel();
        etConnectionMethodLabel = new JLabel();
        connectionMethodComboBox = new JComboBox<>();
        etMAddressLabel = new JLabel();
        mAddressTextField = new JTextField();
        etTcpPortSpinner = new JSpinner();
        etUdpPortSpinner = new JSpinner();
        panel3 = new JPanel();
        fileNameLabel = new JLabel();
        fileNameTextField = new JTextField();
        fileSplitLabel = new JLabel();
        fileSplitSpinner = new JSpinner();
        FileTypeLabel = new JLabel();
        fileTypeComboBox = new JComboBox<>();
        fileInternalBuffer = new JComboBox<>();
        FileTypeLabel2 = new JLabel();
        compressionCheckBox = new JCheckBox();
        compressionModeComboBox = new JComboBox<>();
        label4 = new JLabel();
        compressionThreadsSpinner = new JSpinner();
        label5 = new JLabel();
        separator1 = new JSeparator();
        cancelButton = new JButton();
        clearButton = new JButton();
        okButton = new JButton();
        action1 = new CancelAction();
        action2 = new ClearAction();
        action3 = new OkAction();
        action5 = new TransportClassChanged();
        action8 = new DirectMcastAction();

        //======== this ========
        setTitle("Output");
        setResizable(false);
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(new TitledBorder("General"));

            //---- label2 ----
            label2.setText("Type");

            //---- transportClassComboBox ----
            transportClassComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "Et",
                "File",
                "Debug",
                "None"
            }));
            transportClassComboBox.setAction(action5);

            //---- label1 ----
            label1.setText("Name");

            //---- label3 ----
            label3.setText("Description");

            //======== scrollPane1 ========
            {
                scrollPane1.setViewportView(descriptionTextArea);
            }

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(scrollPane1)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nameTextField)
                                .addGap(18, 18, 18)
                                .addComponent(label2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(transportClassComboBox, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))
                            .addGroup(GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                .addComponent(label3)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(transportClassComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label2)
                            .addComponent(label1)
                            .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label3)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder("ET"));

            //---- etNameLabel ----
            etNameLabel.setText("ET Name");

            //---- etNameTextField ----
            etNameTextField.setEnabled(false);

            //---- etHostLabel ----
            etHostLabel.setText("Host");

            //---- etTcpPortLabel ----
            etTcpPortLabel.setText("TCP Port");

            //---- etUdpPortLabel ----
            etUdpPortLabel.setText("UDP Port");

            //---- etConnectionMethodLabel ----
            etConnectionMethodLabel.setText("Method");

            //---- connectionMethodComboBox ----
            connectionMethodComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "direct",
                "mcast"
            }));
            connectionMethodComboBox.setAction(action8);

            //---- etMAddressLabel ----
            etMAddressLabel.setText("mAddress");

            //---- mAddressTextField ----
            mAddressTextField.setText("239.200.0.0");

            //---- etTcpPortSpinner ----
            etTcpPortSpinner.setModel(new SpinnerNumberModel(23911, 1, 99999, 1));

            //---- etUdpPortSpinner ----
            etUdpPortSpinner.setModel(new SpinnerNumberModel(23912, 1, 99999, 1));

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel2Layout.createParallelGroup()
                            .addComponent(etNameLabel)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(etTcpPortLabel)
                                    .addComponent(etHostLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(etHostTextField, GroupLayout.Alignment.LEADING)
                                    .addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
                                        .addComponent(etTcpPortSpinner, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(etUdpPortLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(etUdpPortSpinner, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(etMAddressLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mAddressTextField))
                                    .addGroup(GroupLayout.Alignment.LEADING, panel2Layout.createSequentialGroup()
                                        .addComponent(etNameTextField, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(etConnectionMethodLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(connectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etNameLabel)
                            .addComponent(etNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etConnectionMethodLabel)
                            .addComponent(connectionMethodComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etHostLabel)
                            .addComponent(etHostTextField))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(etTcpPortLabel)
                            .addComponent(etTcpPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etUdpPortLabel)
                            .addComponent(etUdpPortSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(etMAddressLabel)
                            .addComponent(mAddressTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15))
            );
        }

        //======== panel3 ========
        {
            panel3.setBorder(new TitledBorder("File"));

            //---- fileNameLabel ----
            fileNameLabel.setText("Name");

            //---- fileSplitLabel ----
            fileSplitLabel.setText("Split x 10MB");

            //---- fileSplitSpinner ----
            fileSplitSpinner.setModel(new SpinnerNumberModel(200, 0, 1000000, 1));

            //---- FileTypeLabel ----
            FileTypeLabel.setText("Type");

            //---- fileTypeComboBox ----
            fileTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "coda",
                "binary",
                "ascii"
            }));

            //---- fileInternalBuffer ----
            fileInternalBuffer.setModel(new DefaultComboBoxModel<>(new String[] {
                "100",
                "600",
                "1800",
                "3000",
                "6000"
            }));

            //---- FileTypeLabel2 ----
            FileTypeLabel2.setText("Internal Buffer [MB]");

            //---- compressionCheckBox ----
            compressionCheckBox.setText("Compression");
            compressionCheckBox.setSelected(true);
            compressionCheckBox.addChangeListener(e -> compressionCheckBoxStateChanged(e));

            //---- compressionModeComboBox ----
            compressionModeComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
                "NoC",
                "LZ4",
                "LZ4+",
                "GZIP"
            }));
            compressionModeComboBox.setSelectedIndex(1);

            //---- label4 ----
            label4.setText("Mode");

            //---- label5 ----
            label5.setText("Threads");

            GroupLayout panel3Layout = new GroupLayout(panel3);
            panel3.setLayout(panel3Layout);
            panel3Layout.setHorizontalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fileNameLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup()
                            .addComponent(fileNameTextField)
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGroup(panel3Layout.createParallelGroup()
                                    .addComponent(FileTypeLabel)
                                    .addComponent(fileTypeComboBox, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(compressionCheckBox))
                                .addGap(76, 76, 76)
                                .addGroup(panel3Layout.createParallelGroup()
                                    .addComponent(FileTypeLabel2)
                                    .addComponent(fileInternalBuffer, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addComponent(compressionModeComboBox, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label4)))
                                .addGroup(panel3Layout.createParallelGroup()
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addGap(100, 100, 100)
                                        .addGroup(panel3Layout.createParallelGroup()
                                            .addComponent(fileSplitSpinner, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(fileSplitLabel)))
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addGap(74, 74, 74)
                                        .addComponent(compressionThreadsSpinner, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label5)))
                                .addGap(37, 37, 37)))
                        .addContainerGap())
            );
            panel3Layout.setVerticalGroup(
                panel3Layout.createParallelGroup()
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(fileNameLabel)
                            .addComponent(fileNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(FileTypeLabel)
                            .addComponent(FileTypeLabel2)
                            .addComponent(fileSplitLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(fileTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(fileInternalBuffer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(fileSplitSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel3Layout.createParallelGroup()
                            .addComponent(compressionCheckBox)
                            .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(compressionModeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(compressionThreadsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label4)
                                .addComponent(label5)))
                        .addContainerGap(10, Short.MAX_VALUE))
            );
        }

        //---- cancelButton ----
        cancelButton.setAction(action1);

        //---- clearButton ----
        clearButton.setAction(action2);

        //---- okButton ----
        okButton.setAction(action3);
        okButton.setText("Ok");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(okButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(clearButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton)
                            .addGap(4, 4, 4))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addComponent(separator1)
                            .addContainerGap())
                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(okButton)
                        .addComponent(clearButton)
                        .addComponent(cancelButton))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JLabel label2;
    private JComboBox<String> transportClassComboBox;
    private JLabel label1;
    private JTextField nameTextField;
    private JLabel label3;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JPanel panel2;
    private JLabel etNameLabel;
    private JTextField etNameTextField;
    private JLabel etHostLabel;
    private JTextField etHostTextField;
    private JLabel etTcpPortLabel;
    private JLabel etUdpPortLabel;
    private JLabel etConnectionMethodLabel;
    private JComboBox<String> connectionMethodComboBox;
    private JLabel etMAddressLabel;
    private JTextField mAddressTextField;
    private JSpinner etTcpPortSpinner;
    private JSpinner etUdpPortSpinner;
    private JPanel panel3;
    private JLabel fileNameLabel;
    private JTextField fileNameTextField;
    private JLabel fileSplitLabel;
    private JSpinner fileSplitSpinner;
    private JLabel FileTypeLabel;
    private JComboBox<String> fileTypeComboBox;
    private JComboBox<String> fileInternalBuffer;
    private JLabel FileTypeLabel2;
    private JCheckBox compressionCheckBox;
    private JComboBox<String> compressionModeComboBox;
    private JLabel label4;
    private JSpinner compressionThreadsSpinner;
    private JLabel label5;
    private JSeparator separator1;
    private JButton cancelButton;
    private JButton clearButton;
    private JButton okButton;
    private CancelAction action1;
    private ClearAction action2;
    private OkAction action3;
    private TransportClassChanged action5;
    private DirectMcastAction action8;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class DirectMcastAction extends AbstractAction {
        private DirectMcastAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "dm");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            if(connectionMethodComboBox.getSelectedItem().equals("direct")){
                etMAddressLabel.setForeground(Color.lightGray);
                mAddressTextField.setEnabled(false);
                etUdpPortLabel.setForeground(Color.lightGray);
                etUdpPortSpinner.setEnabled(false);
                etTcpPortLabel.setForeground(Color.black);
                etTcpPortSpinner.setEnabled(true);
                etHostLabel.setForeground(Color.black);
                etHostTextField.setEnabled(true);
            } else {
                etTcpPortLabel.setForeground(Color.lightGray);
                etTcpPortSpinner.setEnabled(false);
                etUdpPortLabel.setForeground(Color.black);
                etUdpPortSpinner.setEnabled(true);
                etMAddressLabel.setForeground(Color.black);
                mAddressTextField.setEnabled(true);
                etHostLabel.setForeground(Color.lightGray);
                etHostTextField.setEnabled(false);
            }
        }
    }

    private class TransportClassChanged extends AbstractAction {
        private TransportClassChanged() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "transport");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            checkTrClass();
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
            int i = 0;
            if(!nameTextField.getText().equals("")){

                String predefinedDescription = CDesktop.isComponentPredefined(nameTextField.getText().trim(),
                        ACodaType.FILE.name(),
                        component.getSubType(),
                        descriptionTextArea.getText().replace("\\n","\n"));
                if(predefinedDescription.equals("undefined")) {
                    descriptionTextArea.setEnabled(true);
                } else {
                    descriptionTextArea.setEnabled(false);
                    descriptionTextArea.setText(predefinedDescription);
                }

                // remove component
                canvas.removeCmp(component);
                component.removeTrnsports();
                component.removeLnks();

                // update a component
                component.setName(nameTextField.getText().trim());
                component.setSubType(transportClassComboBox.getSelectedItem().toString());
                component.setDescription(descriptionTextArea.getText().replace("\\n","\n"));


                JCGTransport gt = new JCGTransport();
                // fill and add/update the transport
                gt.setTransClass(transportClassComboBox.getSelectedItem().toString());
                gt.setEtName("/tmp/et_" + stp.getExpid() + "_" + etNameTextField.getText().trim());
                gt.setEtHostName(etHostTextField.getText().trim());
                gt.setEtTcpPort((Integer) etTcpPortSpinner.getValue());
                gt.setEtUdpPort((Integer) etUdpPortSpinner.getValue());
                gt.setmAddress(mAddressTextField.getText().trim());
                gt.setEtMethodCon(connectionMethodComboBox.getSelectedItem().toString());
                gt.setFileName(fileNameTextField.getText().trim());

                int d =  (Integer)fileSplitSpinner.getValue();
                gt.setFileSplit(d*10000000L);

                gt.setFileInternalBuffer(Integer.parseInt(fileInternalBuffer.getSelectedItem().toString()));
                gt.setFileType((String) fileTypeComboBox.getSelectedItem());
                gt.setNoLink(true);

                if(compressionCheckBox.isSelected()){
                    gt.setCompression(compressionModeComboBox.getSelectedIndex());
                    gt.setCompressionThreads((Integer)compressionThreadsSpinner.getValue());
                } else {
                    gt.setCompression(0);
                }

                gt.setName(nameTextField.getText().trim()+"_transport");


                // add transport
                // remove the old transport if exists
//                component.removeTrnsport(gt);
                component.addTrnsport(gt);

                // add component to tht map
                canvas.addgCmp(component);

                if(glob != null){

                    // remove and add links
                    if(canvas.getGCMPs().containsKey(glob.getSourceComponentName())){
                    canvas.getGCMPs().get(glob.getSourceComponentName()).removeLnk(glob);
                    } else {
                        System.out.println("Error: malformed configuration. SourceComponent of the link is not defined.");
                    }
                    //canvas.getGCMPs().get(glob.getDestinationComponent().getNameFromTextField()).removeLink(glob.getNameFromTextField());

                    glob.setName(glob.getSourceComponentName() + "_" + component.getName());
                    glob.setDestinationComponentName(component.getName());
                    glob.setDestinationComponentType(component.getType());
                    glob.setDestinationModuleName(component.getModule().getName());

                    // remove and add transports
//                    canvas.getGCMPs().get(glob.getSourceComponentName()).removeTrnsport(gt);
//                    canvas.getGCMPs().get(glob.getDestinationComponentName()).removeTrnsport(gt);

                    canvas.getGCMPs().get(glob.getDestinationComponentName()).addTrnsport(gt);
                    canvas.getGCMPs().get(glob.getSourceComponentName()).addTrnsport(gt);

                    canvas.getGCMPs().get(glob.getSourceComponentName()).addLnk(glob);
                    canvas.getGCMPs().get(glob.getDestinationComponentName()).addLnk(glob);
                }

                canvas.repaint();
            } else {
                JOptionPane.showMessageDialog(me,"Component name is undefined","",JOptionPane.ERROR_MESSAGE);
            }
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
            if(component.getSubType().equals(ACodaType.FILE.name())){
                transportClassComboBox.setSelectedItem("File");
            } else {
                transportClassComboBox.setSelectedItem("Et");
            }
            etHostTextField.setText("");
            etTcpPortSpinner.setValue(23911);
            etUdpPortSpinner.setValue(23912);
            mAddressTextField.setText("239.200.0.0");
            connectionMethodComboBox.setSelectedItem("mcast");

            fileNameTextField.setText("");
            fileSplitSpinner.setValue(200);
            fileInternalBuffer.setSelectedItem("100");
            fileTypeComboBox.setSelectedItem("coda");

            compressionCheckBox.setEnabled(true);
            compressionModeComboBox.setEnabled(true);
            compressionThreadsSpinner.setEnabled(true);
            compressionCheckBox.setSelected(true);
            compressionModeComboBox.setSelectedIndex(1);
            compressionThreadsSpinner.setValue(2);

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
}
