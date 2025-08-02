package gui_admin.panel;

import entite.DemandeInscription;
import entite.Client;
import entite.Membre;
import service.DemandeInscriptionService;
import service.ClientService;
import service.MembreService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DemandeInscriptionPanel extends JPanel implements CrudOperationsInterface {

    private List<DemandeInscription> demandesInscription;
    private DemandeInscription demandeSelectionnee;
    private DemandeInscriptionService demandeInscriptionService;
    private ClientService clientService;
    private MembreService membreService;
    private DefaultTableModel tableModel;
    private JTable table;

    public DemandeInscriptionPanel() {
        this.setLayout(new BorderLayout());
        this.demandeInscriptionService = new DemandeInscriptionService();
        this.clientService = new ClientService();
        this.membreService = new MembreService();

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

            @Override
            public boolean isCellEditable(int row, int column) {
                // Rendre la colonne statut éditable pour les boutons
                return column == 0 || column == 5;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        // Configurer le renderer et l'éditeur pour la colonne Statut
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new StatusCellEditor());

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

    // Classe pour le rendu des cellules de statut
    private class StatusCellRenderer extends JPanel implements TableCellRenderer {
        private JButton acceptButton;
        private JButton rejectButton;
        private JLabel statusLabel;

        public StatusCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

            // Utiliser du texte simple au lieu de caractères spéciaux pour éviter les problèmes d'encodage
            acceptButton = new JButton("OK");
            acceptButton.setBackground(Color.GREEN);
            acceptButton.setForeground(Color.WHITE);
            acceptButton.setPreferredSize(new Dimension(45, 25));
            acceptButton.setFont(new Font("Arial", Font.BOLD, 10));

            rejectButton = new JButton("NO");
            rejectButton.setBackground(Color.RED);
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setPreferredSize(new Dimension(45, 25));
            rejectButton.setFont(new Font("Arial", Font.BOLD, 10));

            statusLabel = new JLabel();
            statusLabel.setOpaque(true);
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statusLabel.setPreferredSize(new Dimension(100, 25));
            statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            removeAll();

            if (row < demandesInscription.size()) {
                DemandeInscription demande = demandesInscription.get(row);

                if (demande.getDateDeTraitement() == null) {
                    // Demande en attente - afficher les boutons
                    add(acceptButton);
                    add(rejectButton);
                    setBackground(Color.WHITE);
                } else {
                    // Demande traitée - afficher le statut
                    String statut = (String) value;
                    if ("Accepté".equals(statut)) {
                        statusLabel.setText("ACCEPTÉ");
                        statusLabel.setBackground(Color.GREEN);
                        statusLabel.setForeground(Color.WHITE);
                    } else {
                        statusLabel.setText("REJETÉ");
                        statusLabel.setBackground(Color.RED);
                        statusLabel.setForeground(Color.WHITE);
                    }
                    add(statusLabel);
                    setBackground(statusLabel.getBackground());
                }
            }

            return this;
        }
    }

    // Classe pour l'édition des cellules de statut
    private class StatusCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton acceptButton;
        private JButton rejectButton;
        private String currentValue;
        private int currentRow;

        public StatusCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            // Utiliser du texte simple au lieu de caractères spéciaux
            acceptButton = new JButton("OK");
            acceptButton.setBackground(Color.GREEN);
            acceptButton.setForeground(Color.WHITE);
            acceptButton.setPreferredSize(new Dimension(45, 25));
            acceptButton.setFont(new Font("Arial", Font.BOLD, 10));

            rejectButton = new JButton("NO");
            rejectButton.setBackground(Color.RED);
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setPreferredSize(new Dimension(45, 25));
            rejectButton.setFont(new Font("Arial", Font.BOLD, 10));

            acceptButton.addActionListener(e -> {
                accepterDemande(currentRow);
                currentValue = "Accepté"; // Forcer la valeur à "Accepté"
                fireEditingStopped();
            });

            rejectButton.addActionListener(e -> {
                rejeterDemande(currentRow);
                currentValue = "Rejeté"; // Forcer la valeur à "Rejeté"
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            currentValue = (String) value;
            currentRow = row;

            panel.removeAll();
            panel.add(acceptButton);
            panel.add(rejectButton);

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }
    }

    private void accepterDemande(int row) {
        try {
            DemandeInscription demande = demandesInscription.get(row);

            // Mettre à jour la date de traitement
            demande.setDateDeTraitement(LocalDateTime.now());

            // Essayer de mettre à jour le statut si la colonne existe
            try {
                demande.setStatut("ACCEPTE");
            } catch (Exception e) {
                System.out.println("Champ statut non disponible, utilisation de la logique de fallback");
            }

            // Créer un nouveau membre automatiquement
            if (demande.getClient() != null) {
                Membre nouveauMembre = new Membre();
                nouveauMembre.setClient(demande.getClient());
                nouveauMembre.setDateInscription(demande.getDateDeTraitement()); // Utiliser la date de traitement comme date d'inscription

                membreService.ajouter(nouveauMembre);
            }

            // Modifier la demande en base de données
            demandeInscriptionService.modifier(demande);

            // Mettre à jour l'affichage immédiatement
            tableModel.setValueAt("Accepté", row, 5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tableModel.setValueAt(demande.getDateDeTraitement().format(formatter), row, 3);

            // Rafraîchir l'affichage du tableau
            table.repaint();

            JOptionPane.showMessageDialog(this,
                "Demande acceptée avec succès!\nNouveau membre créé automatiquement.\nLe client peut maintenant se connecter à son espace membre.",
                "Demande acceptée",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'acceptation: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejeterDemande(int row) {
        try {
            DemandeInscription demande = demandesInscription.get(row);

            // Confirmer le rejet
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir rejeter cette demande ?\n" +
                "La demande de " + demande.getClient().getPrenom() + " " + demande.getClient().getNom() +
                " sera marquée comme rejetée.\n" +
                "Le client ne pourra pas se connecter mais ses données seront conservées.",
                "Confirmer le rejet",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            // Mettre à jour la date de traitement
            demande.setDateDeTraitement(LocalDateTime.now());

            // Essayer de mettre à jour le statut si la colonne existe
            try {
                demande.setStatut("REJETE");
            } catch (Exception e) {
                System.out.println("Champ statut non disponible, utilisation de la logique de fallback");
            }

            // Modifier la demande en base de données (SANS supprimer le client)
            demandeInscriptionService.modifier(demande);

            // Mettre à jour l'affichage immédiatement
            tableModel.setValueAt("Rejeté", row, 5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tableModel.setValueAt(demande.getDateDeTraitement().format(formatter), row, 3);

            // Rafraîchir l'affichage du tableau
            table.repaint();

            JOptionPane.showMessageDialog(this,
                "Demande rejetée avec succès.\nLes données du client ont été conservées.\nLe client ne pourra pas se connecter.",
                "Demande rejetée",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du rejet: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        // Vider le modèle
        tableModel.setRowCount(0);
        
        // Charger les données
        demandesInscription = demandeInscriptionService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (DemandeInscription demande : demandesInscription) {
            String statut;
            if (demande.getDateDeTraitement() == null) {
                statut = "En attente";
            } else {
                // Ici nous devons déterminer si c'est accepté ou rejeté
                // Pour cet exemple, nous supposons qu'il y a un champ statut dans DemandeInscription
                // Sinon, nous pouvons vérifier s'il y a un membre correspondant
                statut = verifierStatutDemande(demande);
            }

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

    private String verifierStatutDemande(DemandeInscription demande) {
        if (demande.getDateDeTraitement() == null) {
            return "En attente";
        }

        // Essayer d'utiliser le champ statut s'il existe, sinon utiliser la logique de fallback
        try {
            if (demande.getStatut() != null) {
                if ("ACCEPTE".equals(demande.getStatut())) {
                    return "Accepté";
                } else if ("REJETE".equals(demande.getStatut())) {
                    return "Rejeté";
                }
            }
        } catch (Exception e) {
            System.out.println("Champ statut non disponible, utilisation de la logique de fallback");
        }

        // Fallback : vérifier s'il existe un membre pour ce client
        try {
            List<Membre> membres = membreService.listerTous();
            for (Membre membre : membres) {
                if (membre.getClient() != null && demande.getClient() != null &&
                    membre.getClient().getId().equals(demande.getClient().getId())) {

                    // Si on trouve un membre avec le même client, c'est accepté
                    // Vérifier si la date d'inscription est proche de la date de traitement
                    if (membre.getDateInscription() != null &&
                        Math.abs(java.time.Duration.between(
                            membre.getDateInscription(),
                            demande.getDateDeTraitement()).toHours()) <= 24) {
                        return "Accepté";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du statut: " + e.getMessage());
        }

        // Si pas de membre trouvé et que la demande est traitée, c'est rejeté
        return "Rejeté";
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
