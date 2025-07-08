package gui_admin.view.types_abonnement;

import entite.TypeAbonnement;
import gui_util.ButtonPanel;
import gui_util.GenericEdit;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author TCHAMIE
 */
public class Edit extends GenericEdit <TypeAbonnement>{

    private JTextField code = new JTextField();
    private JTextField libelle = new JTextField();
    private JTextField montant = new JTextField();
    private TypeAbonnement entite = new TypeAbonnement();
    private GridBagConstraints gbc = new GridBagConstraints();
    

    public Edit(TypeAbonnement typeAbonnement) {
        super(typeAbonnement);
        this.form.setLayout(new GridBagLayout());
        // Labels
        JLabel codeLabel = new JLabel("Code :");
        JLabel libelleLabel = new JLabel("Libellé :");
        JLabel montantLabel = new JLabel("Montant :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(codeLabel, gbc);

        gbc.gridx = 1;
        this.form.add(code, gbc);

        // Ligne 1 : Libellé
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(libelleLabel, gbc);

        gbc.gridx = 1;
        this.form.add(libelle, gbc);

        // Ligne 2 : Montant
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(montantLabel, gbc);

        gbc.gridx = 1;
        this.form.add(montant, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        code.setPreferredSize(fieldSize);
        libelle.setPreferredSize(fieldSize);
        montant.setPreferredSize(fieldSize);

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    

//    public void init() {
//        this.entite.setCode(code.getText());
//        this.entite.setLibelle(libelle.getText());
//        try {
//            this.entite.setMontant(Integer.parseInt(montant.getText()));
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Montant invalide ! Veuillez entrer un nombre.");
//        }
//    }
//
//    public TypeAbonnement getEntite() {
//        return entite;
//    }
    
    
    @Override
    public void initEntity() {
        this.entity.setCode(code.getText());
        this.entity.setLibelle(libelle.getText());
        try {
            this.entity.setMontant(Integer.parseInt(montant.getText()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Montant invalide ! Veuillez entrer un nombre.");
        }
    }

    @Override
    public void initForm() {
        this.entity.setCode(code.getText());
        this.entity.setLibelle(libelle.getText());
        this.entity.setMontant(Integer.parseInt(montant.getText()));

    }

    @Override
    public String getTitre() {
        return entity != null && entity.getCode() != null ?
                "Modification d'équipement" : "Ajout d'un type d'abonnement";

    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un type d'abonnement";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'un type d'abonnement";
    }
}