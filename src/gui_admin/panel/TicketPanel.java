package gui_admin.panel;

import entite.Ticket;
import entite.Client;
import service.TicketService;
import service.ClientService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.List;

public class TicketPanel extends JPanel implements CrudOperationsInterface {

    private List<Ticket> tickets;
    private Ticket ticketSelectionne;
    private TicketService ticketService;
    private ClientService clientService;
    private DefaultTableModel tableModel;
    private JTable table;
    
    // Préconfigurations de tickets courantes
    private static final int[][] PRECONFIGURATIONS = {
        {5, 50},     // 5 séances pour 50€
        {10, 90},    // 10 séances pour 90€
        {20, 160},   // 20 séances pour 160€
        {30, 220},   // 30 séances pour 220€
        {50, 350}    // 50 séances pour 350€
    };

    public TicketPanel() {
        this.setLayout(new BorderLayout());
        this.ticketService = new TicketService();
        this.clientService = new ClientService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Nombre de séances", "Montant", "Prix/séance", "Client", "Statut"};

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
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        
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
                        ticketSelectionne = tickets.get(row);
                    } else {
                        ticketSelectionne = null;
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
        tickets = ticketService.listerTous();
        
        for (Ticket ticket : tickets) {
            // Calculer le prix par séance
            String prixParSeance = "N/A";
            if (ticket.getNombreDeSeance() > 0) {
                double prix = (double) ticket.getMontant() / ticket.getNombreDeSeance();
                prixParSeance = String.format("%.2f€", prix);
            }
            
            // Informations client
            String client = ticket.getClient() != null ? 
                ticket.getClient().getPrenom() + " " + ticket.getClient().getNom() : "Aucun client";
            
            // Déterminer le statut
            String statut = determinerStatut(ticket);
            
            tableModel.addRow(new Object[]{
                false,
                ticket.getId(),
                ticket.getNombreDeSeance(),
                ticket.getMontant() + "€",
                prixParSeance,
                client,
                statut
            });
        }
    }
    
    /**
     * Détermine le statut d'un ticket
     */
    private String determinerStatut(Ticket ticket) {
        if (ticket.getClient() == null) {
            return "Sans client";
        }
        
        if (ticket.getNombreDeSeance() <= 0) {
            return "Invalide";
        }
        
        if (ticket.getMontant() <= 0) {
            return "Gratuit";
        }
        
        // Vérifier si c'est une préconfiguration standard
        for (int[] config : PRECONFIGURATIONS) {
            if (ticket.getNombreDeSeance() == config[0] && ticket.getMontant() == config[1]) {
                return "Standard";
            }
        }
        
        // Calcul du rapport qualité-prix
        double prixParSeance = (double) ticket.getMontant() / ticket.getNombreDeSeance();
        if (prixParSeance <= 7.0) {
            return "Avantageux";
        } else if (prixParSeance >= 12.0) {
            return "Premium";
        } else {
            return "Personnalisé";
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
        
        // Créer un formulaire de saisie avec préconfiguration
        JPanel formulaire = new JPanel(new BorderLayout(10, 10));
        
        // Section choix du type
        JPanel choixPanel = new JPanel(new FlowLayout());
        JRadioButton preconfigureBouton = new JRadioButton("Ticket préconfiguré", true);
        JRadioButton personnaliseBouton = new JRadioButton("Ticket personnalisé");
        ButtonGroup groupe = new ButtonGroup();
        groupe.add(preconfigureBouton);
        groupe.add(personnaliseBouton);
        
        choixPanel.add(new JLabel("Type : "));
        choixPanel.add(preconfigureBouton);
        choixPanel.add(personnaliseBouton);
        
        // Section saisie
        JPanel saisiePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        // ComboBox pour préconfigurations
        JComboBox<String> preconfigCombo = new JComboBox<>();
        for (int[] config : PRECONFIGURATIONS) {
            double prixParSeance = (double) config[1] / config[0];
            preconfigCombo.addItem(String.format("%d séances - %d€ (%.2f€/séance)", 
                config[0], config[1], prixParSeance));
        }
        
        // Champs personnalisés
        JTextField nombreSeancesField = new JTextField();
        JTextField montantField = new JTextField();
        JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
        
        // Personaliser l'affichage des clients
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client) {
                    Client client = (Client) value;
                    setText(client.getPrenom() + " " + client.getNom() + " (" + client.getEmail() + ")");
                }
                return this;
            }
        });
        
        saisiePanel.add(new JLabel("Tickets prédéfinis :"));
        saisiePanel.add(preconfigCombo);
        saisiePanel.add(new JLabel("Nombre de séances :"));
        saisiePanel.add(nombreSeancesField);
        saisiePanel.add(new JLabel("Montant (€) :"));
        saisiePanel.add(montantField);
        saisiePanel.add(new JLabel("Client :"));
        saisiePanel.add(clientCombo);
        
        formulaire.add(choixPanel, BorderLayout.NORTH);
        formulaire.add(saisiePanel, BorderLayout.CENTER);
        
        // Gérer l'activation/désactivation des champs
        preconfigureBouton.addActionListener(e -> {
            preconfigCombo.setEnabled(true);
            nombreSeancesField.setEnabled(false);
            montantField.setEnabled(false);
        });
        
        personnaliseBouton.addActionListener(e -> {
            preconfigCombo.setEnabled(false);
            nombreSeancesField.setEnabled(true);
            montantField.setEnabled(true);
        });
        
        // État initial
        preconfigCombo.setEnabled(true);
        nombreSeancesField.setEnabled(false);
        montantField.setEnabled(false);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Créer un nouveau ticket", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            Client clientSelectionne = (Client) clientCombo.getSelectedItem();
            
            if (clientSelectionne != null) {
                try {
                    int nombreSeances;
                    int montant;
                    
                    if (preconfigureBouton.isSelected()) {
                        // Utiliser la préconfiguration
                        int configIndex = preconfigCombo.getSelectedIndex();
                        nombreSeances = PRECONFIGURATIONS[configIndex][0];
                        montant = PRECONFIGURATIONS[configIndex][1];
                    } else {
                        // Utiliser les valeurs personnalisées
                        String nombreSeancesText = nombreSeancesField.getText().trim();
                        String montantText = montantField.getText().trim();
                        
                        if (nombreSeancesText.isEmpty() || montantText.isEmpty()) {
                            JOptionPane.showMessageDialog(this, 
                                "Veuillez remplir le nombre de séances et le montant!", 
                                "Champs manquants", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        nombreSeances = Integer.parseInt(nombreSeancesText);
                        montant = Integer.parseInt(montantText);
                    }
                    
                    // Validations
                    if (nombreSeances <= 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Le nombre de séances doit être positif!", 
                            "Valeur invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    if (montant < 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Le montant ne peut pas être négatif!", 
                            "Valeur invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    if (nombreSeances > 100) {
                        JOptionPane.showMessageDialog(this, 
                            "Le nombre de séances ne peut pas dépasser 100!", 
                            "Limite dépassée", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    Ticket nouveauTicket = new Ticket(nombreSeances, montant, clientSelectionne);
                    ticketService.ajouter(nouveauTicket);
                    
                    double prixParSeance = (double) montant / nombreSeances;
                    JOptionPane.showMessageDialog(this, 
                        "Ticket créé avec succès!\n" +
                        "Séances: " + nombreSeances + "\n" +
                        "Montant: " + montant + "€\n" +
                        "Prix par séance: " + String.format("%.2f€", prixParSeance), 
                        "Création réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Veuillez saisir des nombres valides!", 
                        "Format invalide", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la création: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner un client!", 
                    "Client manquant", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (ticketSelectionne != null) {
            // Récupérer la liste des clients
            List<Client> clients = clientService.listerTous();
            
            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun client disponible pour la modification.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField nombreSeancesField = new JTextField(String.valueOf(ticketSelectionne.getNombreDeSeance()));
            JTextField montantField = new JTextField(String.valueOf(ticketSelectionne.getMontant()));
            JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
            
            // Personaliser l'affichage des clients
            clientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Client) {
                        Client client = (Client) value;
                        setText(client.getPrenom() + " " + client.getNom() + " (" + client.getEmail() + ")");
                    }
                    return this;
                }
            });
            
            // Sélectionner le client actuel
            if (ticketSelectionne.getClient() != null) {
                clientCombo.setSelectedItem(ticketSelectionne.getClient());
            }
            
            formulaire.add(new JLabel("Nombre de séances :"));
            formulaire.add(nombreSeancesField);
            formulaire.add(new JLabel("Montant (€) :"));
            formulaire.add(montantField);
            formulaire.add(new JLabel("Client :"));
            formulaire.add(clientCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier le ticket ID: " + ticketSelectionne.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String nombreSeancesText = nombreSeancesField.getText().trim();
                String montantText = montantField.getText().trim();
                Client clientSelectionne = (Client) clientCombo.getSelectedItem();
                
                if (!nombreSeancesText.isEmpty() && !montantText.isEmpty() && clientSelectionne != null) {
                    try {
                        int nombreSeances = Integer.parseInt(nombreSeancesText);
                        int montant = Integer.parseInt(montantText);
                        
                        // Validations
                        if (nombreSeances <= 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Le nombre de séances doit être positif!", 
                                "Valeur invalide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        if (montant < 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Le montant ne peut pas être négatif!", 
                                "Valeur invalide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        if (nombreSeances > 100) {
                            JOptionPane.showMessageDialog(this, 
                                "Le nombre de séances ne peut pas dépasser 100!", 
                                "Limite dépassée", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Mettre à jour les données du ticket sélectionné
                        ticketSelectionne.setNombreDeSeance(nombreSeances);
                        ticketSelectionne.setMontant(montant);
                        ticketSelectionne.setClient(clientSelectionne);
                        
                        ticketService.modifier(ticketSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Ticket modifié avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Veuillez saisir des nombres valides!", 
                            "Format invalide", 
                            JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Tous les champs sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (ticketSelectionne != null) {
            StringBuilder infoTicket = new StringBuilder();
            infoTicket.append("Ticket ID ").append(ticketSelectionne.getId());
            infoTicket.append("\nSéances: ").append(ticketSelectionne.getNombreDeSeance());
            infoTicket.append("\nMontant: ").append(ticketSelectionne.getMontant()).append("€");
            
            if (ticketSelectionne.getClient() != null) {
                infoTicket.append("\nClient: ").append(ticketSelectionne.getClient().getPrenom())
                         .append(" ").append(ticketSelectionne.getClient().getNom());
            }
            
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer le ticket :\n" + infoTicket.toString() + " ?", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    ticketService.supprimer(ticketSelectionne);
                    JOptionPane.showMessageDialog(this, 
                        "Ticket supprimé avec succès!", 
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    ticketSelectionne = null;
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return ticketSelectionne != null;
    }

    public Ticket getTicketSelectionne() {
        return ticketSelectionne;
    }
    
    /**
     * Méthode utilitaire pour créer des tickets en lot pour un client
     */
    public void creerTicketsEnLot() {
        // Récupérer la liste des clients
        List<Client> clients = clientService.listerTous();
        
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun client disponible.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Sélectionner le client
        Client client = (Client) JOptionPane.showInputDialog(this, 
            "Sélectionnez le client :", 
            "Création en lot", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            clients.toArray(), 
            clients.get(0));
        
        if (client != null) {
            StringBuilder resultats = new StringBuilder();
            int ajouts = 0;
            
            for (int[] config : PRECONFIGURATIONS) {
                try {
                    Ticket nouveau = new Ticket(config[0], config[1], client);
                    ticketService.ajouter(nouveau);
                    ajouts++;
                } catch (Exception e) {
                    resultats.append("Erreur pour ").append(config[0]).append(" séances: ")
                             .append(e.getMessage()).append("\n");
                }
            }
            
            // Recharger les données
            loadData();
            
            // Afficher le résumé
            StringBuilder message = new StringBuilder();
            message.append("=== CRÉATION EN LOT ===\n\n");
            message.append("Client: ").append(client.getPrenom()).append(" ").append(client.getNom()).append("\n");
            message.append("Tickets créés: ").append(ajouts).append(" / ").append(PRECONFIGURATIONS.length).append("\n");
            
            if (resultats.length() > 0) {
                message.append("\nErreurs:\n").append(resultats);
            }
            
            JOptionPane.showMessageDialog(this, 
                message.toString(), 
                "Résultats de la création", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Affiche les statistiques des tickets
     */
    public void afficherStatistiques() {
        int total = tickets.size();
        int standards = 0;
        int avantageux = 0;
        int premium = 0;
        int personnalises = 0;
        int invalides = 0;
        int sansClient = 0;
        
        int totalSeances = 0;
        int totalMontant = 0;
        
        for (Ticket ticket : tickets) {
            String statut = determinerStatut(ticket);
            switch (statut) {
                case "Standard" -> standards++;
                case "Avantageux" -> avantageux++;
                case "Premium" -> premium++;
                case "Personnalisé" -> personnalises++;
                case "Invalide" -> invalides++;
                case "Sans client" -> sansClient++;
            }
            
            totalSeances += ticket.getNombreDeSeance();
            totalMontant += ticket.getMontant();
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTIQUES DES TICKETS ===\n\n");
        stats.append("Total des tickets: ").append(total).append("\n");
        stats.append("Tickets standards: ").append(standards).append("\n");
        stats.append("Tickets avantageux: ").append(avantageux).append("\n");
        stats.append("Tickets premium: ").append(premium).append("\n");
        stats.append("Tickets personnalisés: ").append(personnalises).append("\n");
        stats.append("Tickets invalides: ").append(invalides).append("\n");
        stats.append("Tickets sans client: ").append(sansClient).append("\n\n");
        
        stats.append("=== TOTAUX ===\n");
        stats.append("Total séances vendues: ").append(totalSeances).append("\n");
        stats.append("Chiffre d'affaires: ").append(totalMontant).append("€\n");
        
        if (total > 0 && totalSeances > 0) {
            double prixMoyen = (double) totalMontant / totalSeances;
            double seancesMoyennes = (double) totalSeances / total;
            double montantMoyen = (double) totalMontant / total;
            
            stats.append("\n=== MOYENNES ===\n");
            stats.append("Prix moyen par séance: ").append(String.format("%.2f€", prixMoyen)).append("\n");
            stats.append("Séances par ticket: ").append(String.format("%.1f", seancesMoyennes)).append("\n");
            stats.append("Montant moyen par ticket: ").append(String.format("%.2f€", montantMoyen)).append("\n");
        }
        
        stats.append("\n=== TARIFS PRÉDEFINIS ===\n");
        for (int[] config : PRECONFIGURATIONS) {
            double prixParSeance = (double) config[1] / config[0];
            long nbTicketsConfig = tickets.stream()
                .filter(t -> t.getNombreDeSeance() == config[0] && t.getMontant() == config[1])
                .count();
            stats.append("• ").append(config[0]).append(" séances - ").append(config[1])
                 .append("€ (").append(String.format("%.2f€", prixParSeance)).append("/séance) : ")
                 .append(nbTicketsConfig).append(" ticket(s)\n");
        }
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Statistiques des tickets", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Méthode utilitaire pour dupliquer un ticket
     */
    public void dupliquerTicket() {
        if (ticketSelectionne != null) {
            // Demander le nouveau client
            List<Client> clients = clientService.listerTous();
            
            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun client disponible pour la duplication.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Client nouveauClient = (Client) JOptionPane.showInputDialog(this, 
                "Sélectionnez le client pour la copie :", 
                "Duplication de ticket", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                clients.toArray(), 
                ticketSelectionne.getClient());
            
            if (nouveauClient != null) {
                try {
                    Ticket copie = new Ticket(ticketSelectionne.getNombreDeSeance(), 
                                            ticketSelectionne.getMontant(), 
                                            nouveauClient);
                    ticketService.ajouter(copie);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Ticket dupliqué avec succès!", 
                        "Duplication réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la duplication: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Affiche les clients avec le plus de tickets
     */
    public void afficherTopClients() {
        if (tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun ticket disponible pour l'analyse.", 
                "Pas de données", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Compter les tickets par client
        var clientStats = tickets.stream()
            .filter(t -> t.getClient() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                Ticket::getClient,
                java.util.stream.Collectors.summarizingInt(t -> 1)
            ));
        
        StringBuilder top = new StringBuilder();
        top.append("=== TOP CLIENTS (PAR NOMBRE DE TICKETS) ===\n\n");
        
        clientStats.entrySet().stream()
            .sorted(java.util.Map.Entry.<Client, java.util.IntSummaryStatistics>comparingByValue(
                java.util.Comparator.comparing(s -> s.getCount())))
            .limit(10)
            .forEach(entry -> {
                Client client = entry.getKey();
                long nbTickets = entry.getValue().getCount();
                
                // Calculer totaux pour ce client
                int totalSeances = tickets.stream()
                    .filter(t -> t.getClient() != null && t.getClient().getId().equals(client.getId()))
                    .mapToInt(Ticket::getNombreDeSeance)
                    .sum();
                
                int totalMontant = tickets.stream()
                    .filter(t -> t.getClient() != null && t.getClient().getId().equals(client.getId()))
                    .mapToInt(Ticket::getMontant)
                    .sum();
                
                top.append("• ").append(client.getPrenom()).append(" ").append(client.getNom())
                   .append("\n  Tickets: ").append(nbTickets)
                   .append(" | Séances: ").append(totalSeances)
                   .append(" | Total: ").append(totalMontant).append("€\n\n");
            });
        
        JTextArea textArea = new JTextArea(top.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Top clients", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}