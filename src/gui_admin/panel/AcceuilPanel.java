package gui_admin.panel;

import service.*;
import gui_util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

public class AcceuilPanel extends JPanel {
    
    // Services pour toutes les entités
    private ClientService clientService;
    private MembreService membreService;
    private SalleService salleService;
    private EquipementService equipementService;
    private MoyenDePaiementService moyenDePaiementService;
    private AbonnementService abonnementService;
    private TypeAbonnementService typeAbonnementService;
    private PaiementService paiementService;
    private DemandeInscriptionService demandeInscriptionService;
    private HoraireService horaireService;
    private SeanceService seanceService;
    private TicketService ticketService;
    
    // Labels pour les compteurs
    private JLabel clientsCountLabel;
    private JLabel membresCountLabel;
    private JLabel sallesCountLabel;
    private JLabel equipementsCountLabel;
    private JLabel moyensPaiementCountLabel;
    private JLabel abonnementsCountLabel;
    private JLabel typesAbonnementCountLabel;
    private JLabel paiementsCountLabel;
    private JLabel demandesInscriptionCountLabel;
    private JLabel horairesCountLabel;
    private JLabel seancesCountLabel;
    private JLabel ticketsCountLabel;
    
    // Panneau principal
    private JPanel statsPanel;
    
    public AcceuilPanel() {
        initializeServices();
        initializeUI();
        loadStatistics();
    }
    
    private void initializeServices() {
        this.clientService = new ClientService();
        this.membreService = new MembreService();
        this.salleService = new SalleService();
        this.equipementService = new EquipementService();
        this.moyenDePaiementService = new MoyenDePaiementService();
        this.abonnementService = new AbonnementService();
        this.typeAbonnementService = new TypeAbonnementService();
        this.paiementService = new PaiementService();
        this.demandeInscriptionService = new DemandeInscriptionService();
        this.horaireService = new HoraireService();
        this.seanceService = new SeanceService();
        this.ticketService = new TicketService();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        
        // En-tête avec titre et statistiques générales
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panneau principal avec scroll
        JScrollPane scrollPane = new JScrollPane(createMainStatsPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bouton de rafraîchissement
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 231, 77));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Titre
        JLabel titleLabel = new JLabel("Dashboard - Gestion Salle de Sport", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Vue d'ensemble des données", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0, 0, 0));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(new Color(255, 231, 77));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createMainStatsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Créer le panneau de statistiques en grille bento
        createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void createStatsPanel() {
        statsPanel = new JPanel();
        statsPanel.setLayout(new GridBagLayout());
        statsPanel.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Définir les couleurs pour chaque catégorie
        Color[] colors = {
            new Color(52, 152, 219),   // Bleu - Clients
            new Color(46, 204, 113),   // Vert - Membres
            new Color(155, 89, 182),   // Violet - Salles
            new Color(230, 126, 34),   // Orange - Équipements
            new Color(241, 196, 15),   // Jaune - Moyens de paiement
            new Color(231, 76, 60),    // Rouge - Abonnements
            new Color(26, 188, 156),   // Turquoise - Types d'abonnement
            new Color(142, 68, 173),   // Violet foncé - Paiements
            new Color(52, 73, 94),     // Bleu foncé - Demandes inscription
            new Color(211, 84, 0),     // Orange foncé - Horaires
            new Color(39, 174, 96),    // Vert foncé - Séances
            new Color(192, 57, 43)     // Rouge foncé - Tickets
        };
        
        // Ligne 1 - Entités principales (plus grandes)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.25;
        statsPanel.add(createStatsCard("Clients", "0", colors[0]), gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.25;
        statsPanel.add(createStatsCard("Membres", "0", colors[1]), gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.25;
        statsPanel.add(createStatsCard("Abonnements", "0", colors[5]), gbc);
        
        // Ligne 2 - Entités moyennes
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.2;
        statsPanel.add(createStatsCard("Salles", "0", colors[2]), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.2;
        statsPanel.add(createStatsCard("Équipements", "0", colors[3]), gbc);
        
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.2;
        statsPanel.add(createStatsCard("Paiements", "0", colors[7]), gbc);
        
        gbc.gridx = 5; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.2;
        statsPanel.add(createStatsCard("Séances", "0", colors[10]), gbc);
        
        // Ligne 3 - Entités plus petites
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.18;
        statsPanel.add(createStatsCard("Types\nAbonnement", "0", colors[6]), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.18;
        statsPanel.add(createStatsCard("Moyens\nPaiement", "0", colors[4]), gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.gridwidth = 2; gbc.gridheight = 1;
        gbc.weightx = 0.33; gbc.weighty = 0.18;
        statsPanel.add(createStatsCard("Demandes Inscription", "0", colors[8]), gbc);
        
        gbc.gridx = 4; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.18;
        statsPanel.add(createStatsCard("Horaires", "0", colors[9]), gbc);
        
        gbc.gridx = 5; gbc.gridy = 2;
        gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.weightx = 0.25; gbc.weighty = 0.18;
        statsPanel.add(createStatsCard("Tickets", "0", colors[11]), gbc);
    }
    
    private JPanel createStatsCard(String title, String count, Color primaryColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(primaryColor);
        
        // Créer un effet de dégradé
        Color secondaryColor = primaryColor.darker();
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(secondaryColor, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Titre
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        // Compteur principal
        JLabel countLabel = new JLabel(count, JLabel.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 36));
        countLabel.setForeground(Color.WHITE);
        
        // Texte d'information
        JLabel infoLabel = new JLabel("Total", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(255, 255, 255, 180));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        card.add(infoLabel, BorderLayout.SOUTH);
        
        // Sauvegarder la référence du label pour les mises à jour
        assignCountLabel(title, countLabel);
        
        // Effet de survol
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(secondaryColor);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(primaryColor);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return card;
    }
    
    private void assignCountLabel(String title, JLabel countLabel) {
        switch (title) {
            case "Clients":
                clientsCountLabel = countLabel;
                break;
            case "Membres":
                membresCountLabel = countLabel;
                break;
            case "Salles":
                sallesCountLabel = countLabel;
                break;
            case "Équipements":
                equipementsCountLabel = countLabel;
                break;
            case "Moyens\nPaiement":
                moyensPaiementCountLabel = countLabel;
                break;
            case "Abonnements":
                abonnementsCountLabel = countLabel;
                break;
            case "Types\nAbonnement":
                typesAbonnementCountLabel = countLabel;
                break;
            case "Paiements":
                paiementsCountLabel = countLabel;
                break;
            case "Demandes Inscription":
                demandesInscriptionCountLabel = countLabel;
                break;
            case "Horaires":
                horairesCountLabel = countLabel;
                break;
            case "Séances":
                seancesCountLabel = countLabel;
                break;
            case "Tickets":
                ticketsCountLabel = countLabel;
                break;
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JButton refreshButton = new JButton("Rafraîchir les statistiques");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 73, 94));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol pour le bouton
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(44, 62, 80));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(52, 73, 94));
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStatistics();
            }
        });
        
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    
    private void loadStatistics() {
        showLoadingIndicator();
        
        CompletableFuture.runAsync(() -> {
            try {
                // Charger toutes les statistiques
                int clientsCount = clientService.listerTous().size();
                int membresCount = membreService.listerTous().size();
                int sallesCount = salleService.listerTous().size();
                int equipementsCount = equipementService.listerTous().size();
                int moyensPaiementCount = moyenDePaiementService.listerTous().size();
                int abonnementsCount = abonnementService.listerTous().size();
                int typesAbonnementCount = typeAbonnementService.listerTous().size();
                int paiementsCount = paiementService.listerTous().size();
                int demandesInscriptionCount = demandeInscriptionService.listerTous().size();
                int horairesCount = horaireService.listerTous().size();
                int seancesCount = seanceService.listerTous().size();
                int ticketsCount = ticketService.listerTous().size();
                
                SwingUtilities.invokeLater(() -> {
                    updateAllStatistics(clientsCount, membresCount, sallesCount, equipementsCount, 
                                      moyensPaiementCount, abonnementsCount, typesAbonnementCount,
                                      paiementsCount, demandesInscriptionCount, horairesCount,
                                      seancesCount, ticketsCount);
                    hideLoadingIndicator();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Erreur lors du chargement des statistiques: " + e.getMessage());
                    hideLoadingIndicator();
                });
            }
        });
    }
    
    private void updateAllStatistics(int clients, int membres, int salles, int equipements, 
                                   int moyensPaiement, int abonnements, int typesAbonnement,
                                   int paiements, int demandesInscription, int horaires,
                                   int seances, int tickets) {
        if (clientsCountLabel != null) clientsCountLabel.setText(String.valueOf(clients));
        if (membresCountLabel != null) membresCountLabel.setText(String.valueOf(membres));
        if (sallesCountLabel != null) sallesCountLabel.setText(String.valueOf(salles));
        if (equipementsCountLabel != null) equipementsCountLabel.setText(String.valueOf(equipements));
        if (moyensPaiementCountLabel != null) moyensPaiementCountLabel.setText(String.valueOf(moyensPaiement));
        if (abonnementsCountLabel != null) abonnementsCountLabel.setText(String.valueOf(abonnements));
        if (typesAbonnementCountLabel != null) typesAbonnementCountLabel.setText(String.valueOf(typesAbonnement));
        if (paiementsCountLabel != null) paiementsCountLabel.setText(String.valueOf(paiements));
        if (demandesInscriptionCountLabel != null) demandesInscriptionCountLabel.setText(String.valueOf(demandesInscription));
        if (horairesCountLabel != null) horairesCountLabel.setText(String.valueOf(horaires));
        if (seancesCountLabel != null) seancesCountLabel.setText(String.valueOf(seances));
        if (ticketsCountLabel != null) ticketsCountLabel.setText(String.valueOf(tickets));
    }
    
    private void showLoadingIndicator() {
        JLabel[] allLabels = {clientsCountLabel, membresCountLabel, sallesCountLabel, 
                             equipementsCountLabel, moyensPaiementCountLabel, abonnementsCountLabel,
                             typesAbonnementCountLabel, paiementsCountLabel, demandesInscriptionCountLabel,
                             horairesCountLabel, seancesCountLabel, ticketsCountLabel};
        
        for (JLabel label : allLabels) {
            if (label != null) {
                label.setText("...");
            }
        }
    }
    
    private void hideLoadingIndicator() {
        // Méthode pour masquer l'indicateur de chargement si nécessaire
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
        
        // Réinitialiser tous les compteurs à 0 en cas d'erreur
        JLabel[] allLabels = {clientsCountLabel, membresCountLabel, sallesCountLabel, 
                             equipementsCountLabel, moyensPaiementCountLabel, abonnementsCountLabel,
                             typesAbonnementCountLabel, paiementsCountLabel, demandesInscriptionCountLabel,
                             horairesCountLabel, seancesCountLabel, ticketsCountLabel};
        
        for (JLabel label : allLabels) {
            if (label != null) {
                label.setText("0");
            }
        }
    }
    
    /**
     * Méthode publique pour rafraîchir les statistiques depuis l'extérieur
     */
    public void refreshStatistics() {
        loadStatistics();
    }
}