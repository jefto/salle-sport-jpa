package gui_admin.view.abonnements;

import entite.Abonnement;
import entite.TypeAbonnement;
import entite.Membre;
import entite.Paiement;
import gui_util.GenericEdit;
import service.TypeAbonnementService;
import service.MembreService;
import service.PaiementService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<Abonnement> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField dateDebut = new JTextField();
    private JTextField dateFin = new JTextField();
    private JComboBox<TypeAbonnement> typesAbonnement = new JComboBox<>();
    private JComboBox<Membre> membres = new JComboBox<>();
    private JComboBox<Paiement> paiements = new JComboBox<>();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Edit(Abonnement abonnement) {
        super(abonnement);
        
        JLabel dateDebutLabel = new JLabel("Date début :");
        JLabel dateFinLabel = new JLabel("Date fin :");
        JLabel typeAbonnementLabel = new JLabel("Type d'abonnement :");
        JLabel membreLabel = new JLabel("Membre :");
        JLabel paiementLabel = new JLabel("Paiement :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Date début
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(dateDebutLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        gbc.weightx = 1.0;
        this.form.add(dateDebut, gbc);

        // Ligne 1 : Date fin
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(dateFinLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateFin, gbc);

        // Ligne 2 : Type d'abonnement
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(typeAbonnementLabel, gbc);

        gbc.gridx = 1;
        this.loadTypesAbonnement();
        this.form.add(typesAbonnement, gbc);

        // Ligne 3 : Membre
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.form.add(membreLabel, gbc);

        gbc.gridx = 1;
        this.loadMembres();
        this.form.add(membres, gbc);

        // Ligne 4 : Paiement
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.form.add(paiementLabel, gbc);

        gbc.gridx = 1;
        this.loadPaiements();
        this.form.add(paiements, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(350, 30);
        dateDebut.setPreferredSize(fieldSize);
        dateFin.setPreferredSize(fieldSize);
        typesAbonnement.setPreferredSize(fieldSize);
        membres.setPreferredSize(fieldSize);
        paiements.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        dateDebut.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 09:00)");
        dateFin.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/01/2025 23:59)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void loadTypesAbonnement() {
        TypeAbonnementService service = new TypeAbonnementService();
        for (TypeAbonnement type : service.listerTous()) {
            typesAbonnement.addItem(type);
        }
    }

    private void loadMembres() {
        MembreService service = new MembreService();
        for (Membre membre : service.listerTous()) {
            membres.addItem(membre);
        }
    }

    private void loadPaiements() {
        PaiementService service = new PaiementService();
        for (Paiement paiement : service.listerTous()) {
            paiements.addItem(paiement);
        }
    }

    @Override
    public void initEntity() {
        try {
            TypeAbonnement typeAbonnement = (TypeAbonnement) typesAbonnement.getSelectedItem();
            Membre membre = (Membre) membres.getSelectedItem();
            Paiement paiement = (Paiement) paiements.getSelectedItem();
            
            this.entity.setTypeAbonnement(typeAbonnement);
            this.entity.setMembre(membre);
            this.entity.setPaiement(paiement);
            
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
        typesAbonnement.setSelectedItem(this.entity.getTypeAbonnement());
        membres.setSelectedItem(this.entity.getMembre());
        paiements.setSelectedItem(this.entity.getPaiement());
        
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
        return entity != null && entity.getId() != 0 ?
                "Modification d'abonnement" : "Ajout d'un abonnement";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un abonnement";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'abonnement";
    }
}