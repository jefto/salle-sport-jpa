package gui_admin;

import gui_admin.controller.NavigationController;
import gui_admin.panel.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminDashboard extends JFrame {

    Font font = new Font("Arial", Font.BOLD, 14);
    Dimension taille = new Dimension(100, 20);
    private JPanel mainContent; // zone centrale dynamique
    private JComboBox<String> comboPages; // menu déroulant
    private JPanel profilPart;
    private NavigationController navigationController; // Contrôleur de navigation

    public AdminDashboard() {
        super("Tableau de bord Admin");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // --- Barre supérieure (NORTH) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(74, 41, 0)); // Jaune clair

        // --- Inférieur (SOUTH) ---
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomBar.setBackground(new Color(74, 41, 0));
        JButton ajouterBtn = new JButton();
        JButton modifierBtn = new JButton();
        JButton supprimerBtn = new JButton();

        configurerBouton(ajouterBtn, "Ajouter", new Color(0, 128, 0), Color.WHITE, font, taille);
        configurerBouton(modifierBtn, "Modifier", new Color(25, 25, 112), Color.WHITE, font, taille);
        configurerBouton(supprimerBtn, "Supprimer", new Color(255, 0, 0), Color.WHITE, font, taille);

        // Ajouter les listeners aux boutons
        ajouterBtn.addActionListener(e -> executerAction("ajouter"));
        modifierBtn.addActionListener(e -> executerAction("modifier"));
        supprimerBtn.addActionListener(e -> executerAction("supprimer"));

        bottomBar.add(ajouterBtn);
        bottomBar.add(modifierBtn);
        bottomBar.add(supprimerBtn);

        // Logo à gauche
        JLabel logo = new JLabel("FITPlus+");
        Font logoFont = new Font("SansSerif", Font.BOLD, 22);
        logo.setFont(logoFont);
        logo.setForeground(new Color(255, 254, 242));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topBar.add(logo, BorderLayout.WEST);

        // Profil
        profilPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        profilPart.setBackground(new Color(74, 41, 0));
        JButton deconnexionBtn = new JButton();
        configurerBouton(deconnexionBtn, "Deconnexion", new Color(255, 0, 0), Color.WHITE, font, taille);

        // Ajouter l'action de déconnexion
        deconnexionBtn.addActionListener(e -> {
            // Demander confirmation
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Fermer la fenêtre actuelle
                this.dispose();

                // Afficher le sélecteur d'interface
                salle_gym.Main.showInterfaceSelector();
            }
        });

        // Charger l'image de notification
        JLabel notificationIcon = new JLabel();
        try {
            URL notificationUrl = getClass().getClassLoader().getResource("assets/notification.png");
            if (notificationUrl != null) {
                ImageIcon originalIcon = new ImageIcon(notificationUrl);
                // Redimensionner l'icône pour qu'elle s'adapte bien à la barre
                Image img = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(img);
                notificationIcon.setIcon(scaledIcon);
                notificationIcon.setToolTipText("Notifications");
                notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Ajouter un listener pour les clics sur l'icône de notification
                notificationIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // Naviguer vers la page des notifications
                        navigationController.navigateToPage("Notifications");
                        // Synchroniser le ComboBox
                        comboPages.setSelectedItem("Notifications");
                    }
                });
            } else {
                // Si l'image n'est pas trouvée, afficher un texte de fallback
                notificationIcon.setText("🔔");
                notificationIcon.setForeground(new Color(255, 254, 242));
                notificationIcon.setFont(new Font("Arial", Font.PLAIN, 20));
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser un emoji comme fallback
            notificationIcon.setText("🔔");
            notificationIcon.setForeground(new Color(255, 254, 242));
            notificationIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        }

        // Menu déroulant à droite
        comboPages = new JComboBox<>(new String[]{
                "Accueil", "Abonnements", "Clients", "Demande Inscription",
                "Equipments", "Horaire", "Membres", "Moyen de Paiement",
                "Notifications", "Paiements", "Salles", "Seance", "Tickets", "Types Abonnements",
        });
        StyleUtil.styliserComboBox(comboPages);

        profilPart.add(deconnexionBtn);
        profilPart.add(notificationIcon);
        profilPart.add(comboPages);
        topBar.add(profilPart, BorderLayout.EAST);
        this.add(bottomBar, BorderLayout.SOUTH);
        this.add(topBar, BorderLayout.NORTH);

        // --- Zone principale (CENTER) ---
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        this.add(mainContent, BorderLayout.CENTER);

        // Initialiser le contrôleur de navigation
        navigationController = new NavigationController(mainContent);

        // Par défaut : Accueil
        navigationController.navigateToPage("Accueil");

        // --- Changement de panel au choix dans le ComboBox
        comboPages.addActionListener(e -> {
            String selection = (String) comboPages.getSelectedItem();
            navigationController.navigateToPage(selection);
        });
    }

    /**
     * Exécute l'action demandée sur le panel actuel
     */
    private void executerAction(String action) {
        Component currentPanel = getCurrentPanel();

        if (currentPanel instanceof CrudOperationsInterface) {
            CrudOperationsInterface crudPanel = (CrudOperationsInterface) currentPanel;

            switch (action.toLowerCase()) {
                case "ajouter":
                    crudPanel.ajouter();
                    break;
                case "modifier":
                    if (crudPanel.hasSelection()) {
                        crudPanel.modifier();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Veuillez sélectionner un élément à modifier.", 
                            "Aucune sélection", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                case "supprimer":
                    if (crudPanel.hasSelection()) {
                        int confirm = JOptionPane.showConfirmDialog(this, 
                            "Êtes-vous sûr de vouloir supprimer cet élément ?", 
                            "Confirmer la suppression", 
                            JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            crudPanel.supprimer();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Veuillez sélectionner un élément à supprimer.", 
                            "Aucune sélection", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Cette page ne supporte pas les opérations CRUD.", 
                "Opération non supportée", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Récupère le panel actuellement affiché
     */
    private Component getCurrentPanel() {
        if (mainContent.getComponentCount() > 0) {
            return mainContent.getComponent(0);
        }
        return null;
    }

    public void configurerBouton(JButton bouton, String texte, Color background, Color texteColor, Font police, Dimension taille) {
        bouton.setText(texte);
        bouton.setBackground(background);
        bouton.setForeground(texteColor);
        bouton.setFont(police);
        bouton.setPreferredSize(taille);
        bouton.setFocusPainted(false);
        bouton.setBorderPainted(false);
        bouton.setContentAreaFilled(true);
    }

    public class StyleUtil {
        public static void styliserComboBox(JComboBox<?> comboBox) {
            comboBox.setPreferredSize(new Dimension(250, 20));
            comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
            comboBox.setBackground(new Color(245, 245, 245));
            comboBox.setForeground(Color.BLACK);
            comboBox.setFocusable(false);
        }
    }

    /**
     * Méthode publique pour naviguer vers une page (utilisable depuis l'extérieur)
     */
    public void navigateToPage(String pageName) {
        navigationController.navigateToPage(pageName);
        // Synchroniser le ComboBox avec la navigation
        comboPages.setSelectedItem(pageName);
    }

    /**
     * Obtient le contrôleur de navigation pour des utilisations avancées
     */
    public NavigationController getNavigationController() {
        return navigationController;
    }
}
