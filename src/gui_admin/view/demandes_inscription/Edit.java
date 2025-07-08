package gui_admin.view.demandes_inscription;

import entite.DemandeInscription;
import entite.Client;
import gui_util.GenericEdit;
import service.ClientService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<DemandeInscription> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField dateDemande = new JTextField();
    private JTextField dateTraitement = new JTextField();
    private JComboBox<Client> clients = new JComboBox<>();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Edit(DemandeInscription demandeInscription) {
        super(demandeInscription);
        
        JLabel dateDemandeLabel = new JLabel("Date demande :");
        JLabel dateTraitementLabel = new JLabel("Date traitement :");
        JLabel clientLabel = new JLabel("Client :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Date demande
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(dateDemandeLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateDemande, gbc);

        // Ligne 1 : Date traitement
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(dateTraitementLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateTraitement, gbc);

        // Ligne 2 : Client
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(clientLabel, gbc);

        gbc.gridx = 1;
        this.loadClients();
        this.form.add(clients, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        dateDemande.setPreferredSize(fieldSize);
        dateTraitement.setPreferredSize(fieldSize);
        clients.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        dateDemande.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 14:30)");
        dateTraitement.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 16:30)");

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void loadClients() {
        ClientService service = new ClientService();
        for (Client client : service.listerTous()) {
            clients.addItem(client);
        }
    }

    @Override
    public void initEntity() {
        try {
            Client client = (Client) clients.getSelectedItem();
            this.entity.setClient(client);
            
            // Conversion des chaînes de caractères vers LocalDateTime
            if (!dateDemande.getText().trim().isEmpty()) {
                this.entity.setDateDeDemande(LocalDateTime.parse(dateDemande.getText().trim(), formatter));
            }
            if (!dateTraitement.getText().trim().isEmpty()) {
                this.entity.setDateDeTraitement(LocalDateTime.parse(dateTraitement.getText().trim(), formatter));
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
        clients.setSelectedItem(this.entity.getClient());
        
        // Conversion des LocalDateTime vers chaînes de caractères
        if (this.entity.getDateDeDemande() != null) {
            dateDemande.setText(this.entity.getDateDeDemande().format(formatter));
        }
        if (this.entity.getDateDeTraitement() != null) {
            dateTraitement.setText(this.entity.getDateDeTraitement().format(formatter));
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != 0 ?
                "Modification de demande d'inscription" : "Ajout d'une demande d'inscription";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'une demande d'inscription";
    }

    @Override
    public String getTitreModification() {
        return "Modification de demande d'inscription";
    }
}