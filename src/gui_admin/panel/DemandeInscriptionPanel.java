package gui_admin.panel;

import entite.DemandeInscription;
import entite.Client;
import service.DemandeInscriptionService;
import service.ClientService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DemandeInscriptionPanel extends JPanel implements CrudOperationsInterface {

    private List<DemandeInscription> demandesInscription;
    private DemandeInscription demandeSelectionnee;
    private DemandeInscriptionService demandeInscriptionService;
    private ClientService clientService;
    private DefaultTableModel tableModel;
    private JTable table;

    public DemandeInscriptionPanel() {
        this.setLayout(new BorderLayout());
        this.demandeInscriptionService = new DemandeInscriptionService();
        this.clientService = new ClientService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date demande", "Date traitement", "Client", "Statut"};

        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        
        // Styliser l'en-tête
        StyleUtil.styliserTableHeader(table);

        // Ajouter le listener pour les cases à cocher
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                    int row = e.getFirstRow();
                    boolean isChecked = (Boolean) tableModel.getValueAt(row, 0);
                    
                    if (isChecked) {
                        // Décocher les autres cases
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (i != row) {
                                tableModel.setValueAt(false, i, 0);
                            }
                        }
                        demandeSelectionnee = demandesInscription.get(row);
                    } else {
                        demandeSelectionnee = null;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        // Vider le modèle
        tableModel.setRowCount(0);
        
        // Charger les données
        demandesInscription = demandeInscriptionService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (DemandeInscription demande : demandesInscription) {
            String statut = demande.getDateDeTraitement() != null ? "Traitée" : "En attente";
            
            tableModel.addRow(new Object[]{
                false,
                demande.getId(),
                demande.getDateDeDemande() != null ? demande.getDateDeDemande().format(formatter) : "Non définie",
                demande.getDateDeTraitement() != null ? demande.getDateDeTraitement().format(formatter) : "Non traitée",
                demande.getClient() != null ? demande.getClient().getNom() + " " + demande.getClient().getPrenom() : "Aucun",
                statut
            });
        }
    }

    @Override
    public void ajouter() {
        // Récupérer la liste des clients
        List<Client> clients = clientService.listerTous();
        
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun client disponible. Veuillez d'abord créer un client.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField dateDemandeField = new JTextField();
        JTextField dateTraitementField = new JTextField();
        JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
        
        // Pré-remplir la date de demande avec la date actuelle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateDemandeField.setText(LocalDateTime.now().format(formatter));
        
        // Laisser la date de traitement vide par défaut (demande en attente)
        dateTraitementField.setText("");
        
        formulaire.add(new JLabel("Date de demande (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateDemandeField);
        formulaire.add(new JLabel("Date de traitement (optionnel) :"));
        formulaire.add(dateTraitementField);
        formulaire.add(new JLabel("Client :"));
        formulaire.add(clientCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter une nouvelle demande d'inscription", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String dateDemandeText = dateDemandeField.getText().trim();
            String dateTraitementText = dateTraitementField.getText().trim();
            
            if (!dateDemandeText.isEmpty()) {
                try {
                    LocalDateTime dateDemande = LocalDateTime.parse(dateDemandeText, formatter);
                    LocalDateTime dateTraitement = null;
                    
                    // Parser la date de traitement si elle est fournie
                    if (!dateTraitementText.isEmpty()) {
                        dateTraitement = LocalDateTime.parse(dateTraitementText, formatter);
                        
                        // Vérifier que la date de traitement est postérieure à la date de demande
                        if (dateTraitement.isBefore(dateDemande)) {
                            JOptionPane.showMessageDialog(this, 
                                "La date de traitement doit être postérieure à la date de demande!", 
                                "Dates invalides", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    
                    Client clientSelectionne = (Client) clientCombo.getSelectedItem();
                    
                    DemandeInscription nouvelleDemande = new DemandeInscription(dateDemande, dateTraitement, clientSelectionne);
                    demandeInscriptionService.ajouter(nouvelleDemande);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Demande d'inscription ajoutée avec succès!", 
                        "Ajout réussi", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage() + 
                        "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "La date de demande est obligatoire!", 
                    "Champ manquant", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (demandeSelectionnee != null) {
            // Récupérer la liste des clients
            List<Client> clients = clientService.listerTous();
            
            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun client disponible.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField dateDemandeField = new JTextField();
            JTextField dateTraitementField = new JTextField();
            JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
            
            // Formatter et afficher les données existantes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (demandeSelectionnee.getDateDeDemande() != null) {
                dateDemandeField.setText(demandeSelectionnee.getDateDeDemande().format(formatter));
            }
            if (demandeSelectionnee.getDateDeTraitement() != null) {
                dateTraitementField.setText(demandeSelectionnee.getDateDeTraitement().format(formatter));
            }
            
            // Sélectionner le client actuel
            if (demandeSelectionnee.getClient() != null) {
                clientCombo.setSelectedItem(demandeSelectionnee.getClient());
            }
            
            formulaire.add(new JLabel("Date de demande (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateDemandeField);
            formulaire.add(new JLabel("Date de traitement (optionnel) :"));
            formulaire.add(dateTraitementField);
            formulaire.add(new JLabel("Client :"));
            formulaire.add(clientCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier la demande d'inscription: " + demandeSelectionnee.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String dateDemandeText = dateDemandeField.getText().trim();
                String dateTraitementText = dateTraitementField.getText().trim();
                
                if (!dateDemandeText.isEmpty()) {
                    try {
                        LocalDateTime dateDemande = LocalDateTime.parse(dateDemandeText, formatter);
                        LocalDateTime dateTraitement = null;
                        
                        // Parser la date de traitement si elle est fournie
                        if (!dateTraitementText.isEmpty()) {
                            dateTraitement = LocalDateTime.parse(dateTraitementText, formatter);
                            
                            // Vérifier que la date de traitement est postérieure à la date de demande
                            if (dateTraitement.isBefore(dateDemande)) {
                                JOptionPane.showMessageDialog(this, 
                                    "La date de traitement doit être postérieure à la date de demande!", 
                                    "Dates invalides", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                        
                        Client clientSelectionne = (Client) clientCombo.getSelectedItem();
                        
                        // Mettre à jour les données de la demande sélectionnée
                        demandeSelectionnee.setDateDeDemande(dateDemande);
                        demandeSelectionnee.setDateDeTraitement(dateTraitement);
                        demandeSelectionnee.setClient(clientSelectionne);
                        
                        demandeInscriptionService.modifier(demandeSelectionnee);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Demande d'inscription modifiée avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage() + 
                            "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "La date de demande est obligatoire!", 
                        "Champ manquant", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (demandeSelectionnee != null) {
            try {
                demandeInscriptionService.supprimer(demandeSelectionnee);
                JOptionPane.showMessageDialog(this, 
                    "Demande d'inscription supprimée avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                demandeSelectionnee = null;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return demandeSelectionnee != null;
    }

    public DemandeInscription getDemandeSelectionnee() {
        return demandeSelectionnee;
    }
    
    /**
     * Méthode utilitaire pour traiter une demande (marquer comme traitée)
     */
    public void traiterDemande() {
        if (demandeSelectionnee != null && demandeSelectionnee.getDateDeTraitement() == null) {
            try {
                demandeSelectionnee.setDateDeTraitement(LocalDateTime.now());
                demandeInscriptionService.modifier(demandeSelectionnee);
                
                JOptionPane.showMessageDialog(this, 
                    "Demande marquée comme traitée!", 
                    "Traitement réussi", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du traitement: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else if (demandeSelectionnee != null && demandeSelectionnee.getDateDeTraitement() != null) {
            JOptionPane.showMessageDialog(this, 
                "Cette demande a déjà été traitée!", 
                "Demande déjà traitée", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}