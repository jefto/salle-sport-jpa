package gui_admin.view.membres;

import entite.Membre;
import entite.Client;
import gui_util.GenericEdit;
import service.ClientService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Edit extends GenericEdit<Membre> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField dateInscription = new JTextField();
    private JComboBox<Client> clients = new JComboBox<>();
    
    // Formatter pour les dates
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Edit(Membre membre) {
        super(membre);
        
        JLabel dateInscriptionLabel = new JLabel("Date d'inscription :");
        JLabel clientLabel = new JLabel("Client :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Date d'inscription
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(dateInscriptionLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateInscription, gbc);

        // Ligne 1 : Client
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(clientLabel, gbc);

        gbc.gridx = 1;
        this.loadClients();
        this.form.add(clients, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        dateInscription.setPreferredSize(fieldSize);
        clients.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        dateInscription.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 14:30)");

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
            if (!dateInscription.getText().trim().isEmpty()) {
                this.entity.setDateInscription(LocalDateTime.parse(dateInscription.getText().trim(), formatter));
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
        if (this.entity.getDateInscription() != null) {
            dateInscription.setText(this.entity.getDateInscription().format(formatter));
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification de membre" : "Ajout d'un membre";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un membre";
    }

    @Override
    public String getTitreModification() {
        return "Modification de membre";
    }
}