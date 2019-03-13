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
 * Created by JFormDesigner on Tue Dec 04 14:14:05 EST 2012
 */

package org.jlab.coda.cedit.forms.simple;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import org.jlab.coda.cedit.cooldesktop.CDesktop;
import org.jlab.coda.cedit.cooldesktop.DrawingCanvas;
import org.jlab.coda.cedit.parsers.coolparser.JCParser;
import org.jlab.coda.cedit.parsers.extconfig.CoolDatabaseBrowser;
import org.jlab.coda.cedit.system.*;
import org.jlab.coda.cedit.system.JCGComponent;
import org.jlab.coda.cedit.system.JCGLink;
import org.jlab.coda.cedit.system.JCGTransport;
import org.jlab.coda.cedit.util.JCUtil;

/**
 * @author Vardan Gyurjyan
 */
public class RefactorForm extends JFrame {
    private String name;
//    private HashMap<String, CDefinedComponent> cmt ;
    private CoolDatabaseBrowser coolDbBrowser;
    public CDesktop GOwner;
    private JCGSetup stp = JCGSetup.getInstance();
    private String type ;

    public RefactorForm(CDesktop owner, String Name, String Type) {
        String s = "undefined";
        String n = "undefined";
        String t = "undefined";
        String sut = "undefined";
        int id = 0;
        String desc = "undefined";
        StringTokenizer st1,st2;
        StringBuilder sb = new StringBuilder();
        GOwner = owner;
        name = Name;
        type = Type;
//        cmt = new HashMap<String, CDefinedComponent>();
        JCGSetup setup = JCGSetup.getInstance();
        coolDbBrowser = new CoolDatabaseBrowser();
        initComponents();
        nameTextField.setText(name);
        try {
            BufferedReader r = new BufferedReader(new FileReader(setup.getCoolHome()+
                    File.separator+ stp.getExpid()+
                    File.separator+"jcedit"+
                    File.separator+Type+".txt"));
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
                if(st2.hasMoreTokens()) n  = st2.nextToken();
                if(st2.hasMoreTokens()) t  = st2.nextToken();
                if(st2.hasMoreTokens()) sut = st2.nextToken();
                try{
                if(st2.hasMoreTokens()) id = Integer.parseInt(st2.nextToken());
                } catch ( NumberFormatException e){
                    System.out.println(e.getMessage());
                }
                if(st2.hasMoreTokens()) desc  = st2.nextToken();

                if(n.equals(name)){
                    descriptionTtextArea.setText(desc);
                    IdTextField.setText(Integer.toString(id));
                }

//                cmt.put(name,new CDefinedComponent(n,t,sut,id,desc));
            }

            r.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        nameTextField = new JTextField();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        descriptionTtextArea = new JTextArea();
        label3 = new JLabel();
        IdTextField = new JTextField();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        action1 = new OkAction();
        action2 = new CancelAction();

        //======== this ========
        setTitle("Refactor");
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

                //---- nameTextField ----
                nameTextField.setEditable(false);

                //---- label2 ----
                label2.setText("Description");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(descriptionTtextArea);
                }

                //---- label3 ----
                label3.setText("ID");

                //---- IdTextField ----
                IdTextField.setEditable(false);

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(label1)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nameTextField))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                            .addComponent(label2)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE))
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addComponent(label3)
                                            .addGap(18, 18, 18)
                                            .addComponent(IdTextField, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, Short.MAX_VALUE)))))
                            .addGap(3, 3, 3))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(label1)
                                .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label3)
                                .addComponent(IdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(9, 9, 9)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                .addComponent(label2))
                            .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setAction(action1);
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setAction(action2);
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
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
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTtextArea;
    private JLabel label3;
    private JTextField IdTextField;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private OkAction action1;
    private CancelAction action2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public class OkAction extends AbstractAction {
        private OkAction() {
            // JFormDesigner - Action initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
            // Generated using JFormDesigner non-commercial license
            putValue(NAME, "OK");
            // JFormDesigner - End of action initialization  //GEN-END:initComponents
        }

        public void actionPerformed(ActionEvent e) {
//            GOwner.refactor(type, nameTextField.getText().trim(),
//                    JCTools.isNumber(IdTextField.getText().trim()),
//                    descriptionTtextArea.getText());
//            GOwner.defineIDs();
            dispose();
        }
    }

    public class CancelAction extends AbstractAction {
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
