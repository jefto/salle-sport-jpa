/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui_util;

import entite.GenericEntity;
import service.GenericService;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author TCHAMIE
 */
public abstract class GenericEdit<Entity> extends JFrame {

    protected JPanel form = new JPanel();
    protected ButtonPanel buttonPanel = new ButtonPanel();
    protected Entity entity ;
    protected GridBagConstraints gridBagConstraints = new GridBagConstraints();
    public abstract String getTitreAjout();
    public abstract String getTitreModification();
    private boolean validated = false;

    public abstract void initEntity ();//Permet d'initialiser l'objet a partir des données du formulaire

    public abstract void initForm ();//Permet d'afficher les données de l'objet dans le formulaire

    public abstract String getTitre(); //Méthode abstraite pour obtenir le titre personnalisé

    public GenericEdit (){

    }

    public GenericEdit(Entity entity) {
        this.form.setLayout(new GridBagLayout());
        this.entity = entity ;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(760, 400);

        //Definir le titre personnalisé
        this.setTitle(getTitre());
        // Définir le titre initial (toujours ajout au début)
        this.setTitle(getTitreAjout());

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null); // Center the window on the screen
        form.setLayout(new GridBagLayout());

        c.add(buttonPanel, BorderLayout.SOUTH);
        c.add(form, BorderLayout.CENTER);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public JPanel getForm() {
        return form;
    }

    public JButton getSaveButton() {
        return buttonPanel.getSaveButton();
    }

    public JButton getCancelButton() {
        return buttonPanel.getCancelButton();
    }


    public void afficher(){
        this.setVisible(true);
    }

    // Méthode pour changer le titre en mode modification
    public void setModeModification() {
        this.setTitle(getTitreModification());
    }

    // Méthode pour changer le titre en mode ajout
    public void setModeAjout() {
        this.setTitle(getTitreAjout());
    }

    // Nouvelle méthode pour vérifier si le formulaire a été validé
    public boolean isValidated() {
        return validated;
    }


}