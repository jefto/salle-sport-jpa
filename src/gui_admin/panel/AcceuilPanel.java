package gui_admin.panel;

import service.*;
import gui_util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

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
    
    // Labels pour les box d'informations principales
    private JLabel totalCaisseLabel;
    private JLabel messagesNonLusLabel;

    // Composant pour le diagramme
    private BarChartPanel barChartPanel;

    public AcceuilPanel() {
        initializeServices();
        initializeComponents();
        loadData();
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
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Panel principal avec padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Titre de la page
        JLabel titleLabel = new JLabel("Tableau de Bord - Accueil", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Panel pour les deux box d'informations principales
        JPanel topInfoPanel = createTopInfoPanel();

        // Panel pour le diagramme en barres
        JPanel chartPanel = createChartPanel();

        // Assemblage
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.add(topInfoPanel, BorderLayout.NORTH);
        contentPanel.add(chartPanel, BorderLayout.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(new Color(248, 249, 250));
        panel.setPreferredSize(new Dimension(0, 120));

        // Box 1: Argent en caisse
        JPanel caisseBox = createInfoBox(
            "money-stack.png",
            "Argent en Caisse",
            "0 FCFA",
            new Color(40, 167, 69),
            new Color(34, 139, 58)
        );
        totalCaisseLabel = (JLabel) ((JPanel) caisseBox.getComponent(1)).getComponent(1);

        // Box 2: Messages non lus
        JPanel messagesBox = createInfoBox(
            "new-notification.png",
            "Messages Non Lus",
            "0",
            new Color(0, 123, 255),
            new Color(0, 105, 217)
        );
        messagesNonLusLabel = (JLabel) ((JPanel) messagesBox.getComponent(1)).getComponent(1);

        panel.add(caisseBox);
        panel.add(messagesBox);

        return panel;
    }
    
    private JPanel createInfoBox(String iconPath, String title, String value, Color bgColor, Color hoverColor) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(bgColor);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icône (utiliser image PNG au lieu d'emoji)
        JLabel iconLabel = createIconLabel(iconPath);
        iconLabel.setPreferredSize(new Dimension(60, 60));

        // Texte (titre + valeur)
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(bgColor);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(Color.WHITE);

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        box.add(iconLabel, BorderLayout.WEST);
        box.add(textPanel, BorderLayout.CENTER);

        // Effet hover
        box.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                box.setBackground(hoverColor);
                textPanel.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                box.setBackground(bgColor);
                textPanel.setBackground(bgColor);
            }
        });
        
        return box;
    }

    private JLabel createIconLabel(String iconPath) {
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        try {
            // Essayer de charger l'image depuis le package assets
            java.net.URL imageURL = getClass().getClassLoader().getResource("assets/" + iconPath);
            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                // Redimensionner l'image à 32x32 pixels
                Image img = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(img);
                iconLabel.setIcon(scaledIcon);
            } else {
                // Fallback en cas d'échec du chargement
                createFallbackIcon(iconLabel, iconPath);
            }
        } catch (Exception e) {
            // Fallback en cas d'erreur
            createFallbackIcon(iconLabel, iconPath);
        }

        return iconLabel;
    }

    private void createFallbackIcon(JLabel iconLabel, String iconPath) {
        // Créer une icône de fallback en fonction du type
        if (iconPath.contains("money")) {
            // Icône simple pour l'argent
            iconLabel.setText("$");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
            iconLabel.setForeground(Color.WHITE);
        } else if (iconPath.contains("notification")) {
            // Icône simple pour les notifications
            iconLabel.setText("@");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
            iconLabel.setForeground(Color.WHITE);
        } else {
            // Icône générique
            iconLabel.setText("?");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 28));
            iconLabel.setForeground(Color.WHITE);
        }
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Titre du graphique
        JLabel chartTitle = new JLabel("Statistiques des Tables de la Base de Données", SwingConstants.CENTER);
        chartTitle.setFont(new Font("Arial", Font.BOLD, 18));
        chartTitle.setForeground(new Color(74, 41, 0));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Créer le diagramme en barres
        barChartPanel = new BarChartPanel();

        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(barChartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        // Charger les données de manière asynchrone pour éviter de bloquer l'interface
        CompletableFuture.runAsync(() -> {
            try {
                loadCaisseData();
                loadMessagesData();
                loadChartData();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement des statistiques: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

    private void loadCaisseData() {
        try {
            // Calculer la somme totale des paiements
            int totalCaisse = paiementService.listerTous().stream()
                .mapToInt(paiement -> paiement.getMontant())
                .sum();

            SwingUtilities.invokeLater(() -> {
                NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
                totalCaisseLabel.setText(formatter.format(totalCaisse) + " FCFA");
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                totalCaisseLabel.setText("Erreur");
            });
        }
    }

    private void loadMessagesData() {
        // Pour le moment, simuler des messages non lus (fonctionnalité future)
        SwingUtilities.invokeLater(() -> {
            int messagesNonLus = (int) (Math.random() * 10); // Simulation
            messagesNonLusLabel.setText(String.valueOf(messagesNonLus));
        });
    }
    
    private void loadChartData() {
        try {
            Map<String, Integer> data = new HashMap<>();

            // Compter les enregistrements de chaque table
            data.put("Clients", clientService.listerTous().size());
            data.put("Membres", membreService.listerTous().size());
            data.put("Salles", salleService.listerTous().size());
            data.put("Équipements", equipementService.listerTous().size());
            data.put("Moyens Paiem.", moyenDePaiementService.listerTous().size());
            data.put("Abonnements", abonnementService.listerTous().size());
            data.put("Types Abon.", typeAbonnementService.listerTous().size());
            data.put("Paiements", paiementService.listerTous().size());
            data.put("Demandes", demandeInscriptionService.listerTous().size());
            data.put("Horaires", horaireService.listerTous().size());
            data.put("Séances", seanceService.listerTous().size());
            data.put("Tickets", ticketService.listerTous().size());

            SwingUtilities.invokeLater(() -> {
                barChartPanel.updateData(data);
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                Map<String, Integer> errorData = new HashMap<>();
                errorData.put("Erreur", 0);
                barChartPanel.updateData(errorData);
            });
        }
    }
}

