package gui_admin.view.tickets;

import entite.Client;
import entite.Ticket;
import gui_util.GenericEdit;
import service.ClientService;

import javax.swing.*;
import java.awt.*;

public class Edit extends GenericEdit<Ticket> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField nombreSeance = new JTextField();
    private JTextField montant = new JTextField();
    private JComboBox<Client> clients = new JComboBox<>();
    
    public Edit(Ticket ticket) {
        super(ticket);
        
        JLabel nombreSeanceLabel = new JLabel("Nombre de séances :");
        JLabel montantLabel = new JLabel("Montant :");
        JLabel clientLabel = new JLabel("Client :");

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Nombre de séances
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(nombreSeanceLabel, gbc);

        gbc.gridx = 1;
        this.form.add(nombreSeance, gbc);

        // Ligne 1 : Montant
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(montantLabel, gbc);

        gbc.gridx = 1;
        this.form.add(montant, gbc);

        // Ligne 2 : Client
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(clientLabel, gbc);

        gbc.gridx = 1;
        this.loadClients();
        this.form.add(clients, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        nombreSeance.setPreferredSize(fieldSize);
        montant.setPreferredSize(fieldSize);
        clients.setPreferredSize(fieldSize);

        // Ajouter des tooltips pour aider l'utilisateur
        nombreSeance.setToolTipText("Nombre de séances incluses dans le ticket (ex: 10)");
        montant.setToolTipText("Montant du ticket en unité monétaire (ex: 150)");

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
            
            // Conversion des champs texte vers entiers
            if (!nombreSeance.getText().trim().isEmpty()) {
                this.entity.setNombreDeSeance(Integer.parseInt(nombreSeance.getText().trim()));
            }
            if (!montant.getText().trim().isEmpty()) {
                this.entity.setMontant(Integer.parseInt(montant.getText().trim()));
            }
        } catch (NumberFormatException e) {
            // Gestion d'erreur - notification à l'utilisateur
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir des valeurs numériques valides pour le nombre de séances et le montant.", 
                "Erreur de saisie", 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Format numérique invalide", e);
        }
    }

    @Override
    public void initForm() {
        clients.setSelectedItem(this.entity.getClient());
        
        // Conversion des entiers vers chaînes de caractères
        nombreSeance.setText(String.valueOf(this.entity.getNombreDeSeance()));
        montant.setText(String.valueOf(this.entity.getMontant()));
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != 0 ?
                "Modification de ticket" : "Ajout d'un ticket";
    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un ticket";
    }

    @Override
    public String getTitreModification() {
        return "Modification de ticket";
    }
}