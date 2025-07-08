/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui_util;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author TCHAMIE
 */
public class ButtonPanel extends JPanel{
    private JButton saveButton =  new JButton("Enregistrer");
    private JButton cancelButton = new JButton("Annuler");

    public ButtonPanel(){
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));

        this.add(saveButton);
        this.add(cancelButton);
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

}