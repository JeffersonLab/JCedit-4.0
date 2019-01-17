
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

import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.system.JCGComponent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Vardan Gyurjyan
 */
public class COutForm extends JFrame {
    private DrawingCanvas parentCanvas;
    private JCGComponent component;

    public COutForm() {
        initComponents();
    }

    public COutForm(DrawingCanvas canvas, JCGComponent comp) {
        parentCanvas = canvas;
        component = comp;

        initComponents();

        nameTextField.setText(comp.getName());

        typeTextField.setText(comp.getSubType());

        descriptionTextArea.setText(comp.getDescription());
        if(!comp.getDescription().equals("")){
            descriptionTextArea.setEditable(false);
            descriptionTextArea.setEnabled(false);
        }

        setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        typeTextField = new JTextField();
        label11 = new JLabel();
        configFileLabel2 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        label3 = new JLabel();
        okButton = new JButton();
        clearButton = new JButton();
        cancelButton = new JButton();
        separator1 = new JSeparator();
        action1 = new OkAction();
        action2 = new ClearAction();
        action3 = new CancelAction();

        //======== this ========
        setTitle("Output");
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

                //---- label11 ----
                label11.setText("Type");

                //---- configFileLabel2 ----
                configFileLabel2.setText("Description");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(descriptionTextArea);
                }

                //---- label3 ----
                label3.setText("(optional)");
                label3.setEnabled(false);

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(label1)
                                    .addGap(15, 15, 15)
                                    .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                                    .addGap(23, 23, 23)
                                    .addComponent(label11)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(typeTextField, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                    .addGap(12, 12, 12))
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(configFileLabel2)
                                        .addComponent(label3, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                                    .addContainerGap())))
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
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(configFileLabel2)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(label3)
                                    .addGap(0, 91, Short.MAX_VALUE))
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                            .addContainerGap())
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
                        .addContainerGap(366, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearButton, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(cancelButton)
                        .addContainerGap())
                    .addComponent(separator1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                    .addComponent(contentPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            dialogPaneLayout.setVerticalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addComponent(contentPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(dialogPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(2, Short.MAX_VALUE))
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
    private JLabel label11;
    private JLabel configFileLabel2;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JLabel label3;
    private JButton okButton;
    private JButton clearButton;
    private JButton cancelButton;
    private JSeparator separator1;
    private OkAction action1;
    private ClearAction action2;
    private CancelAction action3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private class ProcessAction extends AbstractAction {
        private ProcessAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
            // TODO add your code here
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
            typeTextField.setText("");
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
            String pName = component.getName();
            if(!nameTextField.getText().trim().equals(pName)){
                parentCanvas.linkDelete2(pName);
            }

            component.setName(nameTextField.getText().trim());
            typeTextField.setText(typeTextField.getText().trim().toUpperCase());
            component.setSubType(typeTextField.getText().trim());

            component.setDescription(descriptionTextArea.getText());

            // add the modified/created component to the graphics component map
            if(!parentCanvas.getGCMPs().containsKey(component.getName())) {
                parentCanvas.getGCMPs().remove(pName);
                parentCanvas.addgCmp(component);
            }

            parentCanvas.repaint();
            dispose();
        }
    }
}
