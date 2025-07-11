package gui_client;

import gui_client.controller.ClientNavigationController;
import gui_util.StyleUtil;
import service.UserSessionManager;
import entite.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientDashboard extends JFrame {
    
    private JPanel mainContent;
    private JPanel navbar;
    private JPanel authPanel;
    private ClientNavigationController navigationController;
    private java.util.List<JButton> navigationButtons;
    
    public ClientDashboard() {
        super("FITPlus+ - Espace Client");
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        
        // Initialiser la liste des boutons de navigation
        navigationButtons = new java.util.ArrayList<>();
        
        // Barre de navigation
        navbar = createNavbar();
        add(navbar, BorderLayout.NORTH);
        
        // Zone principale
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        add(mainContent, BorderLayout.CENTER);
        
        // Initialiser le contrôleur de navigation
        navigationController = new ClientNavigationController(mainContent);
        
        // Par défaut : Page d'accueil
        navigateToPage("Accueil");
    }
    
    private JPanel createNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(74, 41, 0));
        navbar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Logo
        JLabel logo = new JLabel("FITPlus+");
        Font logoFont = new Font("SansSerif", Font.BOLD, 24);
        logo.setFont(logoFont);
        logo.setForeground(new Color(255, 231, 77));
        navbar.add(logo, BorderLayout.WEST);
        
        // Menu de navigation
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        menuPanel.setBackground(new Color(74, 41, 0));
        
        String[] menuItems = {"Accueil", "Séances", "Abonnements", "Profil", "Notifications"};
        
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            menuPanel.add(menuButton);
            navigationButtons.add(menuButton);
        }
        
        navbar.add(menuPanel, BorderLayout.CENTER);
        
        // Panel d'authentification (changeable)
        authPanel = createAuthPanel();
        navbar.add(authPanel, BorderLayout.EAST);
        
        return navbar;
    }
    
    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(new Color(74, 41, 0));
        
        // Vérifier si l'utilisateur est connecté
        UserSessionManager sessionManager = UserSessionManager.getInstance();
        
        if (sessionManager.isLoggedIn()) {
            // Utilisateur connecté : afficher nom et bouton déconnexion
            Client currentUser = sessionManager.getCurrentUser();
            
            // Label avec le nom de l'utilisateur
            JLabel userLabel = new JLabel(currentUser.getPrenom() + " " + currentUser.getNom());
            userLabel.setFont(new Font("Arial", Font.BOLD, 12));
            userLabel.setForeground(Color.WHITE);
            
            // Bouton de déconnexion
            JButton logoutButton = createAuthButton("Déconnexion", new Color(220, 53, 69)); // Rouge
            logoutButton.addActionListener(e -> handleLogout());
            
            panel.add(userLabel);
            panel.add(logoutButton);
            
        } else {
            // Utilisateur non connecté : afficher boutons connexion/inscription
            JButton loginButton = createAuthButton("Se connecter", new Color(52, 152, 219));
            JButton registerButton = createAuthButton("S'inscrire", new Color(46, 204, 113));
            
            loginButton.addActionListener(e -> navigateToPage("Connexion"));
            registerButton.addActionListener(e -> navigateToPage("Inscription"));
            
            panel.add(loginButton);
            panel.add(registerButton);
        }
        
        return panel;
    }
    
    private void handleLogout() {
        // Demander confirmation
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir vous déconnecter ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Déconnecter l'utilisateur
            UserSessionManager.getInstance().logout();
            
            // Rafraîchir la navbar
            refreshNavbar();
            
            // Rediriger vers la page d'accueil
            navigateToPage("Accueil");
            
            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(this,
                "Vous avez été déconnecté avec succès.",
                "Déconnexion",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Rafraîchit la navbar (appelée après connexion/déconnexion)
     */
    public void refreshNavbar() {
        // Supprimer l'ancienne navbar
        remove(navbar);
        
        // Créer une nouvelle navbar
        navbar = createNavbar();
        add(navbar, BorderLayout.NORTH);
        
        // Rafraîchir l'affichage
        revalidate();
        repaint();
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(74, 41, 0));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.getText().equals(getCurrentPage())) {
                    button.setBackground(new Color(255, 231, 77));
                    button.setForeground(new Color(74, 41, 0));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getText().equals(getCurrentPage())) {
                    button.setForeground(new Color(255, 231, 77));
                    button.setBackground(new Color(74, 41, 0));
                    button.setContentAreaFilled(false);
                } else {
                    button.setForeground(Color.WHITE);
                    button.setBackground(new Color(74, 41, 0));
                    button.setContentAreaFilled(false);
                }
            }
        });
        
        button.addActionListener(e -> {
            navigateToPage(text);
        });
        
        return button;
    }
    
    private JButton createAuthButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    public void navigateToPage(String pageName) {
        navigationController.navigateToPage(pageName);
        gui_util.StyleUtil.highlightActiveNavigationButton(navigationButtons, pageName);
    }
    
    private String getCurrentPage() {
        return navigationController.getCurrentPage();
    }
    
    public ClientNavigationController getNavigationController() {
        return navigationController;
    }
}