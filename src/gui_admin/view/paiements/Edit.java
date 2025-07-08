package gui_admin.view.paiements;

import entite.MoyenDePaiement;
import entite.Paiement;
import gui_util.GenericEdit;
import service.MoyenDePaiementService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<Paiement> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField datePaiement = new JTextField();
    private JTextField montant = new JTextField();
    private JComboBox<MoyenDePaiement> moyensPaiement = new JComboBox<>();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public Edit(Paiement paiement) {
        super(paiement);
        
        JLabel datePaiementLabel = new JLabel("Date du paiement :");
        JLabel montantLabel = new JLabel("Montant :");
        JLabel moyenPaiementLabel = new JLabel("Moyen de paiement :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Date du paiement
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(datePaiementLabel, gbc);

        gbc.gridx = 1;
        this.form.add(datePaiement, gbc);

        // Ligne 1 : Montant
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(montantLabel, gbc);

        gbc.gridx = 1;
        this.form.add(montant, gbc);

        // Ligne 2 : Moyen de paiement
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(moyenPaiementLabel, gbc);

        gbc.gridx = 1;
        this.loadMoyensPaiement();
        this.form.add(moyensPaiement, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        datePaiement.setPreferredSize(fieldSize);
        montant.setPreferredSize(fieldSize);
        moyensPaiement.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        datePaiement.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 14:30)");
        montant.setToolTipText("Montant en unité monétaire (ex: 150)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void loadMoyensPaiement() {
        MoyenDePaiementService service = new MoyenDePaiementService();
        for (MoyenDePaiement moyen : service.listerTous()) {
            moyensPaiement.addItem(moyen);
        }
    }

    @Override
    public void initEntity() {
        try {
            MoyenDePaiement moyenDePaiement = (MoyenDePaiement) moyensPaiement.getSelectedItem();
            this.entity.setMoyenDePaiement(moyenDePaiement);
            
            // Conversion de la chaîne de caractères vers LocalDateTime
            if (!datePaiement.getText().trim().isEmpty()) {
                this.entity.setDateDePaiement(LocalDateTime.parse(datePaiement.getText().trim(), formatter));
            }
            
            // Conversion du montant vers entier
            if (!montant.getText().trim().isEmpty()) {
                this.entity.setMontant(Integer.parseInt(montant.getText().trim()));
            }
        } catch (DateTimeParseException e) {
            // Gestion d'erreur pour les dates
            JOptionPane.showMessageDialog(this, 
                "Format de date invalide. Utilisez le format: jj/mm/aaaa hh:mm", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Format de date invalide", e);
        } catch (NumberFormatException e) {
            // Gestion d'erreur pour le montant
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir une valeur numérique valide pour le montant.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Format numérique invalide", e);
        }
    }

    @Override
    public void initForm() {
        moyensPaiement.setSelectedItem(this.entity.getMoyenDePaiement());
        
        // Conversion des données de l'entité vers les champs du formulaire
        if (this.entity.getDateDePaiement() != null) {
            datePaiement.setText(this.entity.getDateDePaiement().format(formatter));
        }
        montant.setText(String.valueOf(this.entity.getMontant()));
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification de paiement" : "Ajout d'un paiement";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un paiement";
    }

    @Override
    public String getTitreModification() {
        return "Modification de paiement";
    }
}