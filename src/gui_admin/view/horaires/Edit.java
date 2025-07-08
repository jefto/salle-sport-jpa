package gui_admin.view.horaires;

import entite.Horaire;
import gui_util.GenericEdit;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<Horaire> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField dateDebut = new JTextField();
    private JTextField dateFin = new JTextField();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Edit(Horaire horaire) {
        super(horaire);
        
        JLabel dateDebutLabel = new JLabel("Date début :");
        JLabel dateFinLabel = new JLabel("Date fin :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Date début
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(dateDebutLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateDebut, gbc);

        // Ligne 1 : Date fin
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(dateFinLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateFin, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        dateDebut.setPreferredSize(fieldSize);
        dateFin.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        dateDebut.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 14:30)");
        dateFin.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 16:30)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    public void initEntity() {
        try {
            // Conversion des chaînes de caractères vers LocalDateTime
            if (!dateDebut.getText().trim().isEmpty()) {
                this.entity.setDebut(LocalDateTime.parse(dateDebut.getText().trim(), formatter));
            }
            if (!dateFin.getText().trim().isEmpty()) {
                this.entity.setFin(LocalDateTime.parse(dateFin.getText().trim(), formatter));
            }
        } catch (DateTimeParseException e) {
            // Gestion d'erreur - vous pouvez ajouter une notification à l'utilisateur
            JOptionPane.showMessageDialog(this, 
                "Format de date invalide. Utilisez le format: jj/mm/aaaa hh:mm", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Format de date invalide", e);
        }
    }

    @Override
    public void initForm() {
        // Conversion des LocalDateTime vers chaînes de caractères
        if (this.entity.getDebut() != null) {
            dateDebut.setText(this.entity.getDebut().format(formatter));
        }
        if (this.entity.getFin() != null) {
            dateFin.setText(this.entity.getFin().format(formatter));
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != 0 ?
                "Modification d'horaire" : "Ajout d'un horaire";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un horaire";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'un horaire";
    }
}