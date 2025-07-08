package gui_admin.view.equipements;

import entite.Equipement;
import entite.GenericEntity;
import entite.Salle;
import gui_util.GenericEdit;
import service.SalleService;

import javax.swing.*;
import javax.swing.text.html.parser.Entity;
import java.awt.*;

public class Edit extends GenericEdit<Equipement> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField libelle = new JTextField();
    private JTextField description = new JTextField();
    private JComboBox<Salle> salles  = new JComboBox<>();

    public Edit(Equipement equipement){
        super(equipement);
        JLabel libelleLabel = new JLabel("Libellé :");
        JLabel descriptionLabel = new JLabel("Description:");
        JLabel salleLabel = new JLabel("Salle:");


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

        // Ligne 2 : Salle
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(salleLabel, gbc);

        gbc.gridx = 1;
        this.loadSalles();
        this.form.add(salles, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        libelle.setPreferredSize(fieldSize);
        description.setPreferredSize(fieldSize);
        salles.setPreferredSize(fieldSize);

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    }
    private void loadSalles (){
        SalleService service = new SalleService();
        for (Salle salle : service.listerTous()) {
            salles.addItem(salle);
        }
    }



    @Override
    public void initEntity() {
        Salle salle = (Salle) salles.getSelectedItem();
        this.entity.setSalle(salle);
        this.entity.setLibelle(libelle.getText());
        this.entity.setDescription(description.getText());;
    }

    @Override
    public void initForm() {
        salles.setSelectedItem(this.entity.getSalle());
        libelle.setText(this.entity.getLibelle());
        description.setText(this.entity.getDescription());

    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification d'équipement" : "Ajout d'équipement";

    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'équipement";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'équipement";
    }

}
