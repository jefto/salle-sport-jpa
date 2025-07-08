package gui_admin.view.moyens_de_paiement;

import entite.MoyenDePaiement;
import gui_util.GenericEdit;

import javax.swing.*;
import java.awt.*;

public class Edit extends GenericEdit<MoyenDePaiement> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField libelle = new JTextField();

    public Edit(MoyenDePaiement moyenDePaiement) {
        super(moyenDePaiement);
        
        JLabel libelleLabel = new JLabel("Libellé :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Libellé
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(libelleLabel, gbc);

        gbc.gridx = 1;
        this.form.add(libelle, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        libelle.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        libelle.setToolTipText("Nom du moyen de paiement (ex: Carte bancaire, Espèces, Chèque)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    public void initEntity() {
        this.entity.setLibelle(libelle.getText().trim());
    }

    @Override
    public void initForm() {
        if (this.entity.getLibelle() != null) {
            libelle.setText(this.entity.getLibelle());
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification de moyen de paiement" : "Ajout d'un moyen de paiement";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un moyen de paiement";
    }

    @Override
    public String getTitreModification() {
        return "Modification de moyen de paiement";
    }
}