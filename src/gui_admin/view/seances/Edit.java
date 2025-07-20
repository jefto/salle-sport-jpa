package gui_admin.view.seances;

import entite.Salle;
import entite.Seance;
import gui_util.GenericEdit;
import service.SalleService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<Seance> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField dateDebut = new JTextField();
    private JTextField dateFin = new JTextField();
    private JComboBox<Salle> salles = new JComboBox<>();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Edit(Seance seance) {
        super(seance);
        
        JLabel dateDebutLabel = new JLabel("Date début :");
        JLabel dateFinLabel = new JLabel("Date fin :");
        JLabel salleLabel = new JLabel("Salle :");

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

        // Ligne 2 : Salle
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(salleLabel, gbc);

        gbc.gridx = 1;
        this.loadSalles();
        this.form.add(salles, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        dateDebut.setPreferredSize(fieldSize);
        dateFin.setPreferredSize(fieldSize);
        salles.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        dateDebut.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 09:00)");
        dateFin.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 10:00)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void loadSalles() {
        SalleService service = new SalleService();
        for (Salle salle : service.listerTous()) {
            salles.addItem(salle);
        }
    }

    @Override
    public void initEntity() {
        try {
            Salle salle = (Salle) salles.getSelectedItem();
            
            this.entity.setSalle(salle);
            
            // Conversion des chaînes de caractères vers LocalDateTime
            if (!dateDebut.getText().trim().isEmpty()) {
                this.entity.setDateDebut(LocalDateTime.parse(dateDebut.getText().trim(), formatter));
            }
            if (!dateFin.getText().trim().isEmpty()) {
                this.entity.setDateFin(LocalDateTime.parse(dateFin.getText().trim(), formatter));
            }
        } catch (DateTimeParseException e) {
            // Gestion d'erreur - notification à l'utilisateur
            JOptionPane.showMessageDialog(this, 
                "Format de date invalide. Utilisez le format: jj/mm/aaaa hh:mm", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Format de date invalide", e);
        }
    }

    @Override
    public void initForm() {
        salles.setSelectedItem(this.entity.getSalle());
        
        // Conversion des LocalDateTime vers chaînes de caractères
        if (this.entity.getDateDebut() != null) {
            dateDebut.setText(this.entity.getDateDebut().format(formatter));
        }
        if (this.entity.getDateFin() != null) {
            dateFin.setText(this.entity.getDateFin().format(formatter));
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification de séance" : "Ajout d'une séance";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'une séance";
    }

    @Override
    public String getTitreModification() {
        return "Modification de séance";
    }
}