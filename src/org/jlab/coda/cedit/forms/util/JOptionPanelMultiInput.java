package org.jlab.coda.cedit.forms.util;

import javax.swing.*;

/**
 * Created by gurjyan on 10/25/17.
 */
public class JOptionPanelMultiInput extends JPanel{
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);

        public JOptionPanelMultiInput(String label1, String label2) {
            add(new JLabel(label1));
            add(xField);
            add(Box.createHorizontalStrut(15)); // a spacer
            add(new JLabel(label2));
            add(yField);
        }

    public JTextField getxField() {
        return xField;
    }

    public JTextField getyField() {
        return yField;
    }

    //        int result = JOptionPane.showConfirmDialog(null, myPanel,
//                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);
//        if (result == JOptionPane.OK_OPTION) {
//            System.out.println("x value: " + xField.getText());
//            System.out.println("y value: " + yField.getText());
//        }
}
