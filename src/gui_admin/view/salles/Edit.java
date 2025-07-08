package gui_admin.view.salles;

import entite.Salle;
import gui_util.GenericEdit;

import javax.swing.*;
import java.awt.*;

public class Edit extends GenericEdit<Salle>{
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField libelle = new JTextField();
    private JTextField description = new JTextField();

    public Edit(Salle salle){
        super(salle);
        JLabel libelleLabel = new JLabel("Libellé :");
        JLabel descriptionLabel = new JLabel("Description:");


        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Libelle
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(libelleLabel, gbc);

        gbc.gridx = 1;
        this.form.add(libelle, gbc);

        // Ligne 1 : Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        this.form.add(description, gbc);


        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        libelle.setPreferredSize(fieldSize);
        description.setPreferredSize(fieldSize);

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    }




    @Override
    public void initEntity() {
        this.entity.setLibelle(libelle.getText());
        this.entity.setDescription(description.getText());
    }

    @Override
    public void initForm() {
        libelle.setText(this.entity.getLibelle());
        description.setText(this.entity.getDescription());

    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification d'équipement" : "Ajout d'une salle";

    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'une salle";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'une salle";
    }
}
