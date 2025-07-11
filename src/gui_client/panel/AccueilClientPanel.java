package gui_client.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccueilClientPanel extends JPanel {
    
    // *** AJOUTER CETTE DÉCLARATION EN HAUT DE LA CLASSE ***
    private gui_client.controller.ClientNavigationController navigationController;
    
    public AccueilClientPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Créer le contenu principal avec scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        
        // Hero section
        mainPanel.add(createHeroSection());
        
        // Qualités section
        mainPanel.add(createQualitiesSection());
        
        // Abonnements section
        mainPanel.add(createSubscriptionsSection());
        
        // Boutons d'action
        mainPanel.add(createActionButtonsSection());
        
        // Témoignages section
        mainPanel.add(createTestimonialsSection());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // *** AJOUTER CETTE MÉTHODE ***
    public void setNavigationController(gui_client.controller.ClientNavigationController controller) {
        this.navigationController = controller;
    }
    
    private JPanel createHeroSection() {
        JPanel heroPanel = new JPanel(new BorderLayout());
        heroPanel.setBackground(Color.WHITE);
        heroPanel.setMinimumSize(new Dimension(800, 400));
        heroPanel.setPreferredSize(new Dimension(1200, 400));
        heroPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        // Essayer de charger l'image avec getResource()
        ImageIcon mainImg = null;
        try {
            java.net.URL imageURL = getClass().getClassLoader().getResource("assets/main-image.jpg");
            if (imageURL != null) {
                mainImg = new ImageIcon(imageURL);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }
        
        // Créer un JLabel qui contient l'image ET le texte
        JLabel imageWithTextLabel;
        
        if (mainImg == null || mainImg.getIconWidth() == -1) {
            // Si l'image ne se charge pas, utiliser le placeholder
            imageWithTextLabel = createPlaceholderImageWithText();
        } else {
            // Créer un JLabel avec l'image et le texte superposé
            ImageIcon finalMainImg = mainImg;
            imageWithTextLabel = new JLabel("\"Rapprochez-vous d'avantage de votre hobby\"", SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    // Dessiner l'image d'abord
                    if (finalMainImg != null) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        
                        // Dessiner l'image redimensionnée pour couvrir tout le composant
                        g2d.drawImage(finalMainImg.getImage(), 0, 0, getWidth(), getHeight(), this);
                        g2d.dispose();
                    }
                    
                    // Ensuite dessiner le texte par-dessus
                    super.paintComponent(g);
                }
            };
            
            imageWithTextLabel.setFont(new Font("Arial", Font.BOLD, 32));
            imageWithTextLabel.setForeground(Color.WHITE);
            imageWithTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageWithTextLabel.setVerticalAlignment(SwingConstants.CENTER);
            imageWithTextLabel.setOpaque(false);
        }
        
        heroPanel.add(imageWithTextLabel, BorderLayout.CENTER);
        
        return heroPanel;
    }
    
    private JLabel createPlaceholderImageWithText() {
        JLabel imageLabel = new JLabel("Rapprochez-vous d'avantage de votre hobby", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Dégradé de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 41, 0),
                    width, height, new Color(255, 231, 77)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                
                // Ajouter des éléments décoratifs
                g2d.setColor(new Color(255, 255, 255, 40));
                
                // Formes géométriques modernes
                for (int i = 0; i < 5; i++) {
                    int x = (int) (Math.random() * width);
                    int y = (int) (Math.random() * height);
                    int size = 30 + (int) (Math.random() * 50);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Lignes décoratives
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(new Color(255, 255, 255, 60));
                for (int i = 0; i < 3; i++) {
                    int y_pos = height/4 + i * height/4;
                    g2d.drawLine(0, y_pos, width/3, y_pos);
                    g2d.drawLine(2*width/3, y_pos, width, y_pos);
                }
                
                g2d.dispose();
                
                // Dessiner le texte par-dessus
                super.paintComponent(g);
            }
        };
        
        imageLabel.setFont(new Font("Arial", Font.BOLD, 32));
        imageLabel.setForeground(Color.WHITE);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(false);
        
        return imageLabel;
    }
    
    private JLabel createPlaceholderImage() {
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Dégradé de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 41, 0),
                    width, height, new Color(255, 231, 77)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                
                // Ajouter des éléments décoratifs
                g2d.setColor(new Color(255, 244, 158, 40));
                
                // Formes géométriques modernes
                for (int i = 0; i < 5; i++) {
                    int x = (int) (Math.random() * width);
                    int y = (int) (Math.random() * height);
                    int size = 30 + (int) (Math.random() * 50);
                    g2d.fillOval(x, y, size, size);
                }
                
                // Lignes décoratives
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(new Color(255, 255, 255, 60));
                for (int i = 0; i < 3; i++) {
                    int y = height/4 + i * height/4;
                    g2d.drawLine(0, y, width/3, y);
                    g2d.drawLine(2*width/3, y, width, y);
                }
                
                g2d.dispose();
            }
        };
        
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        return imageLabel;
    }
    
    private JPanel createQualitiesSection() {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(new Color(255, 244, 158));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        
        // Titre
        JLabel titleLabel = new JLabel("Nos Qualités", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Container avec contraintes de taille
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(new Color(255, 244, 158));
        containerPanel.setMaximumSize(new Dimension(1000, Integer.MAX_VALUE));
        
        // Grille des qualités - responsive
        JPanel qualitiesGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        qualitiesGrid.setBackground(new Color(255, 244, 158));
        
        String[] qualities = {
            "Accès aux équipements de haute qualité",
            "Coaching personnalisé",
            "Paiement en ligne simple et sécurisé",
            "Suivi de vos séances et abonnements"
        };
        
        Color[] colors = {
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(155, 89, 182),
            new Color(230, 126, 34)
        };
        
        for (int i = 0; i < qualities.length; i++) {
            qualitiesGrid.add(createQualityCard(qualities[i], colors[i]));
        }
        
        containerPanel.add(qualitiesGrid, BorderLayout.CENTER);
        
        // Centrer le container
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centeringPanel.setBackground(new Color(255, 244, 158));
        centeringPanel.add(containerPanel);
        
        sectionPanel.add(titleLabel, BorderLayout.NORTH);
        sectionPanel.add(centeringPanel, BorderLayout.CENTER);
        
        return sectionPanel;
    }
    
    private JPanel createQualityCard(String text, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(220, 120));
        
        JLabel label = new JLabel("<html><center>" + text + "</center></html>");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(label, BorderLayout.CENTER);
        
        return card;
    }
    
private JPanel createSubscriptionsSection() {
    JPanel sectionPanel = new JPanel(new BorderLayout());
    sectionPanel.setBackground(Color.WHITE);
    sectionPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
    
    // Titre
    JLabel titleLabel = new JLabel("Nos Abonnements", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(new Color(74, 41, 0));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
    // Container pour les cartes d'abonnement
    JPanel containerPanel = new JPanel();
    containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
    containerPanel.setBackground(Color.WHITE);
    
    // Données des abonnements
    String[] types = {"Hebdomadaire", "Mensuel", "Annuel"};
    String[] prices = {"25€", "80€", "800€"};
    String[] descriptions = {
        "Accès 1 semaine\n• Toutes les salles\n• Équipements standard",
        "Accès 1 mois\n• Toutes les salles\n• Équipements premium\n• 2 séances coaching",
        "Accès 1 an\n• Toutes les salles\n• Équipements premium\n• Coaching illimité\n• Suivi personnalisé"
    };
    
    Color[] colors = {
        new Color(52, 152, 219),
        new Color(46, 204, 113),
        new Color(155, 89, 182)
    };
    
    for (int i = 0; i < types.length; i++) {
        containerPanel.add(createSubscriptionCard(types[i], prices[i], descriptions[i], colors[i]));
        if (i < types.length - 1) {
            containerPanel.add(Box.createHorizontalStrut(10));
        }
    }
    
    // Centrer le container des cartes
    JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    centeringPanel.setBackground(Color.WHITE);
    centeringPanel.add(containerPanel);
    
    // Assembler la section complète
    sectionPanel.add(titleLabel, BorderLayout.NORTH);
    sectionPanel.add(centeringPanel, BorderLayout.CENTER);
    
    return sectionPanel;
}
    
    private JPanel createSubscriptionCard(String type, String price, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(280, 300));
        card.setMaximumSize(new Dimension(300, 300));
        
        // En-tête
        JLabel typeLabel = new JLabel(type, SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        typeLabel.setForeground(color);
        
        // Prix
        JLabel priceLabel = new JLabel(price, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 32));
        priceLabel.setForeground(color);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>" + description.replace("\n", "<br>") + "</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Bouton
        JButton chooseButton = new JButton("Choisir");
        chooseButton.setFont(new Font("Arial", Font.BOLD, 14));
        chooseButton.setBackground(color);
        chooseButton.setForeground(Color.WHITE);
        chooseButton.setFocusPainted(false);
        chooseButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        chooseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        chooseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                chooseButton.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                chooseButton.setBackground(color);
            }
        });
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(typeLabel);
        headerPanel.add(priceLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(chooseButton);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createActionButtonsSection() {
        JPanel sectionPanel = new JPanel(new FlowLayout());
        sectionPanel.setBackground(new Color(255, 244, 158));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        JButton registerButton = createActionButton("S'inscrire", new Color(46, 204, 113));
        JButton loginButton = createActionButton("Se connecter", new Color(52, 152, 219));
        
        // *** CONFIGURER LES ACTIONS AVEC StyleUtil ***
        gui_util.StyleUtil.configureAuthenticationButtons(
            loginButton, 
            registerButton,
            () -> {
                // Action pour connexion
                if (navigationController != null) {
                    navigationController.navigateToPage("Connexion");
                } else {
                    // Fallback si le controller n'est pas disponible
                    Container parent = this.getParent();
                    while (parent != null && !(parent instanceof gui_client.ClientDashboard)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof gui_client.ClientDashboard) {
                        ((gui_client.ClientDashboard) parent).navigateToPage("Connexion");
                    }
                }
            },
            () -> {
                // Action pour inscription
                if (navigationController != null) {
                    navigationController.navigateToPage("Inscription");
                } else {
                    // Fallback si le controller n'est pas disponible
                    Container parent = this.getParent();
                    while (parent != null && !(parent instanceof gui_client.ClientDashboard)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof gui_client.ClientDashboard) {
                        ((gui_client.ClientDashboard) parent).navigateToPage("Inscription");
                    }
                }
            }
        );
        
        sectionPanel.add(registerButton);
        sectionPanel.add(Box.createHorizontalStrut(20));
        sectionPanel.add(loginButton);
        
        return sectionPanel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private JPanel createTestimonialsSection() {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        
        // Titre
        JLabel titleLabel = new JLabel("Témoignages de nos clients", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Container responsive
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
        containerPanel.setBackground(Color.WHITE);
        containerPanel.setMaximumSize(new Dimension(1000, Integer.MAX_VALUE));
        
        String[] testimonials = {
            "Excellent service et équipements de qualité ! Je recommande vivement.",
            "Le coaching personnalisé m'a vraiment aidé à atteindre mes objectifs.",
            "Une salle moderne avec une ambiance conviviale. Parfait pour se motiver !"
        };
        
        String[] authors = {"Marie D.", "Jean P.", "Sophie L."};
        
        for (int i = 0; i < testimonials.length; i++) {
            containerPanel.add(createTestimonialCard(testimonials[i], authors[i]));
            if (i < testimonials.length - 1) {
                containerPanel.add(Box.createHorizontalStrut(15));
            }
        }
        
        // Centrer le container
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centeringPanel.setBackground(Color.WHITE);
        centeringPanel.add(containerPanel);
        
        sectionPanel.add(titleLabel, BorderLayout.NORTH);
        sectionPanel.add(centeringPanel, BorderLayout.CENTER);
        
        return sectionPanel;
    }
    
    private JPanel createTestimonialCard(String text, String author) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 244, 158), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(280, 150));
        card.setMaximumSize(new Dimension(300, 150));
        
        JLabel testimonialLabel = new JLabel("<html><i>\"" + text + "\"</i></html>");
        testimonialLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        testimonialLabel.setForeground(Color.DARK_GRAY);
        
        JLabel authorLabel = new JLabel("- " + author, SwingConstants.RIGHT);
        authorLabel.setFont(new Font("Arial", Font.BOLD, 12));
        authorLabel.setForeground(new Color(74, 41, 0));
        
        card.add(testimonialLabel, BorderLayout.CENTER);
        card.add(authorLabel, BorderLayout.SOUTH);
        
        return card;
    }
}