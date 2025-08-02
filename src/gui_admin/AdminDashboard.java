package gui_admin;

import gui_admin.controller.NavigationController;
import gui_admin.panel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class AdminDashboard extends JFrame {

    Font font = new Font("Arial", Font.BOLD, 14);
    Dimension taille = new Dimension(100, 20);
    private JPanel mainContent; // zone centrale dynamique
    private JPanel sidebar; // Sidebar pour les menus
    private JButton menuButton; // Bouton burger menu
    private boolean sidebarVisible = false;
    private JPanel profilPart;
    private NavigationController navigationController; // Contrôleur de navigation

    public AdminDashboard() {
        super("FITPlus+ - Tableau de bord Admin");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // --- Barre supérieure (NORTH) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(74, 41, 0));

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

        // Section gauche avec logo et menu burger
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftSection.setBackground(new Color(74, 41, 0));

        // Menu burger button
        menuButton = createMenuButton();
        leftSection.add(menuButton);

        // Logo
        JLabel logo = new JLabel("FITPlus+");
        Font logoFont = new Font("SansSerif", Font.BOLD, 22);
        logo.setFont(logoFont);
        logo.setForeground(new Color(255, 254, 242));
        leftSection.add(logo);

        topBar.add(leftSection, BorderLayout.WEST);

        // Profil à droite
        profilPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        profilPart.setBackground(new Color(74, 41, 0));
        JButton deconnexionBtn = new JButton();
        configurerBouton(deconnexionBtn, "Déconnexion", new Color(220, 53, 69), Color.WHITE, font, taille);

        // Ajouter l'action de déconnexion
        deconnexionBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                salle_gym.Main.showInterfaceSelector();
            }
        });

        // Charger l'icône de notification
        JLabel notificationIcon = createNotificationIcon();

        profilPart.add(deconnexionBtn);
        profilPart.add(notificationIcon);
        topBar.add(profilPart, BorderLayout.EAST);

        this.add(bottomBar, BorderLayout.SOUTH);
        this.add(topBar, BorderLayout.NORTH);

        // --- Sidebar ---
        createSidebar();

        // --- Zone principale (CENTER) ---
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        this.add(mainContent, BorderLayout.CENTER);

        // Initialiser le contrôleur de navigation
        navigationController = new NavigationController(mainContent);

        // Par défaut : Accueil
        navigationController.navigateToPage("Accueil");
    }

    private JButton createMenuButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(40, 30));
        button.setBackground(new Color(74, 41, 0));
        button.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Créer l'icône burger menu
        button.setIcon(createBurgerIcon());

        button.addActionListener(e -> toggleSidebar());

        return button;
    }

    private Icon createBurgerIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));

                // Trois lignes horizontales
                g2.drawLine(x + 3, y + 5, x + 21, y + 5);
                g2.drawLine(x + 3, y + 12, x + 21, y + 12);
                g2.drawLine(x + 3, y + 19, x + 21, y + 19);

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return 24; }

            @Override
            public int getIconHeight() { return 24; }
        };
    }

    private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(248, 249, 250));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));

        // Créer un panel scrollable pour le contenu de la sidebar
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(new Color(248, 249, 250));

        // Titre de la sidebar
        JLabel sidebarTitle = new JLabel("Navigation");
        sidebarTitle.setFont(new Font("Arial", Font.BOLD, 16));
        sidebarTitle.setForeground(new Color(73, 80, 87));
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidebarContent.add(sidebarTitle);

        // Bouton Hide
        JButton hideButton = createSidebarButton("x Masquer", new Color(220, 53, 69));
        hideButton.addActionListener(e -> toggleSidebar());
        sidebarContent.add(hideButton);
        sidebarContent.add(Box.createVerticalStrut(10));

        // Boutons de navigation - Ajout du menu "Message"
        String[] menuItems = {
            "Accueil", "Abonnements", "Clients", "Demande Inscription",
            "Equipments", "Horaire", "Membres", "Message", "Moyen de Paiement",
            "Notifications", "Paiements", "Salles", "Seance",
            "Tickets", "Types Abonnements"
        };

        for (String item : menuItems) {
            JButton menuBtn = createSidebarButton(item, new Color(74, 41, 0));
            menuBtn.addActionListener(e -> {
                // Gérer le cas spécial du menu "Message" qui doit ouvrir les Notifications
                String targetPage = item.equals("Message") ? "Notifications" : item;
                navigationController.navigateToPage(targetPage);
                toggleSidebar(); // Fermer la sidebar après sélection
            });
            sidebarContent.add(menuBtn);
            sidebarContent.add(Box.createVerticalStrut(5));
        }

        // Ajouter un espace flexible à la fin pour pousser le contenu vers le haut
        sidebarContent.add(Box.createVerticalGlue());

        // Créer un JScrollPane pour rendre la sidebar scrollable
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarContent);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setBorder(null);
        sidebarScrollPane.setBackground(new Color(248, 249, 250));
        sidebarScrollPane.getViewport().setBackground(new Color(248, 249, 250));

        // Personnaliser la scrollbar
        sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        sidebarScrollPane.getVerticalScrollBar().setBlockIncrement(48);

        sidebar.add(sidebarScrollPane);
        sidebar.setVisible(false);
    }

    private JButton createSidebarButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 35));
        button.setPreferredSize(new Dimension(220, 35));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = button.getBackground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);

        if (sidebarVisible) {
            this.add(sidebar, BorderLayout.WEST);
        } else {
            this.remove(sidebar);
        }

        this.revalidate();
        this.repaint();
    }

    private JLabel createNotificationIcon() {
        JLabel notificationIcon = new JLabel();
        try {
            URL notificationUrl = getClass().getClassLoader().getResource("assets/notification.png");
            if (notificationUrl != null) {
                ImageIcon originalIcon = new ImageIcon(notificationUrl);
                Image img = originalIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(img);
                notificationIcon.setIcon(scaledIcon);
                notificationIcon.setToolTipText("Notifications");
                notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

                notificationIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        navigationController.navigateToPage("Notifications");
                    }
                });
            } else {
                notificationIcon.setText("🔔");
                notificationIcon.setForeground(new Color(255, 254, 242));
                notificationIcon.setFont(new Font("Arial", Font.PLAIN, 20));
            }
        } catch (Exception e) {
            notificationIcon.setText("🔔");
            notificationIcon.setForeground(new Color(255, 254, 242));
            notificationIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        }
        return notificationIcon;
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
    }

    /**
     * Obtient le contrôleur de navigation pour des utilisations avancées
     */
    public NavigationController getNavigationController() {
        return navigationController;
    }
}
