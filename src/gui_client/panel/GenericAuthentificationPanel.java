package gui_client.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GenericAuthentificationPanel extends JPanel {
    
    private JPanel inputContainer;
    private JButton actionButton;
    private JLabel switchModeLabel;
    private String mode; // "connexion" ou "inscription"
    
    // Ajouter une référence au contrôleur de navigation
    protected gui_client.controller.ClientNavigationController navigationController;
    
    public GenericAuthentificationPanel(String mode) {
        this.mode = mode;
        initializeComponents();
    }
    
    // Méthode pour définir le contrôleur de navigation
    public void setNavigationController(gui_client.controller.ClientNavigationController controller) {
        this.navigationController = controller;
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 231, 77)); // Couleur jaune
        
        // Container principal avec scroll
        JPanel mainContent = createMainContent();
        
        // Ajouter le scroll
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createMainContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(new Color(255, 231, 77));
        
        // Box blanche avec bordures incurvées
        JPanel authBox = createAuthBox();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(50, 50, 50, 50); // Top, Left, Bottom, Right margins
        gbc.anchor = GridBagConstraints.CENTER;
        
        content.add(authBox, gbc);
        return content;
    }
    
    private JPanel createAuthBox() {
        // Panel personnalisé avec coins arrondis
        JPanel box = new RoundedPanel(20);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        
        // Padding interne
        box.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Taille dynamique selon le mode
        int boxHeight = mode.equals("inscription") ? 750 : 500;
        box.setPreferredSize(new Dimension(400, boxHeight));
        box.setMinimumSize(new Dimension(400, boxHeight));
        
        // Titre FITPlus+
        JLabel titleLabel = new JLabel("FITPlus+", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(titleLabel);
        
        box.add(Box.createVerticalStrut(20));
        
        // Sous-titre (Connexion/Inscription)
        String subtitle = mode.equals("connexion") ? "Connexion" : "Inscription";
        JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(new Color(74, 41, 0));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(subtitleLabel);
        
        box.add(Box.createVerticalStrut(25));
        
        // Message d'information
        JPanel notePanel = createNotePanel();
        box.add(notePanel);
        
        box.add(Box.createVerticalStrut(20));
        
        // Ligne de séparation
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(new Color(200, 200, 200));
        box.add(separator);
        
        box.add(Box.createVerticalStrut(25));
        
        // Container pour les inputs
        inputContainer = new JPanel();
        inputContainer.setLayout(new BoxLayout(inputContainer, BoxLayout.Y_AXIS));
        inputContainer.setBackground(Color.WHITE);
        inputContainer.setOpaque(false); // Transparent pour voir le fond arrondi
        inputContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(inputContainer);
        
        box.add(Box.createVerticalStrut(30));
        
        // Bouton d'action
        String buttonText = mode.equals("connexion") ? "Se connecter" : "S'inscrire";
        actionButton = createActionButton(buttonText);
        box.add(actionButton);
        
        box.add(Box.createVerticalStrut(20));
        
        // Message de changement de mode
        String switchText = mode.equals("connexion") ? 
            "Pas encore de compte ? Créer un compte" : 
            "J'ai déjà un compte ? Me connecter";
        
        switchModeLabel = new JLabel(switchText, SwingConstants.CENTER);
        switchModeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        switchModeLabel.setForeground(new Color(52, 152, 219));
        switchModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchModeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        box.add(switchModeLabel);
        
        return box;
    }
    
    private JPanel createNotePanel() {
        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.setBackground(new Color(217, 217, 217)); // #D9D9D9
        notePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        notePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel noteLabel = new JLabel("<html><center><b>Note:</b> Prenez soin de bien entrer vos données pour éviter des problèmes de connexion</center></html>");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(60, 60, 60));
        
        notePanel.add(noteLabel, BorderLayout.CENTER);
        return notePanel;
    }
    
    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(74, 41, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(74, 41, 0).brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(74, 41, 0));
            }
        });
        
        return button;
    }
    
    // Méthodes pour ajouter les inputs avec labels alignés horizontalement
    protected JTextField addTextInput(String labelText) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setOpaque(false); // Transparent pour voir le fond arrondi
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(74, 41, 0));
        label.setPreferredSize(new Dimension(120, 35)); // Largeur fixe pour alignement
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setPreferredSize(new Dimension(200, 35));
        
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(textField, BorderLayout.CENTER);
        
        inputContainer.add(inputPanel);
        inputContainer.add(Box.createVerticalStrut(10));
        
        return textField;
    }
    
    protected JPasswordField addPasswordInput(String labelText) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setOpaque(false); // Transparent pour voir le fond arrondi
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(74, 41, 0));
        label.setPreferredSize(new Dimension(120, 35)); // Largeur fixe pour alignement
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setPreferredSize(new Dimension(200, 35));
        
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(passwordField, BorderLayout.CENTER);
        
        inputContainer.add(inputPanel);
        inputContainer.add(Box.createVerticalStrut(10));
        
        return passwordField;
    }
    
    protected JSpinner addDateTimeInput(String labelText) {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setOpaque(false); // Transparent pour voir le fond arrondi
        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(74, 41, 0));
        label.setPreferredSize(new Dimension(120, 35)); // Largeur fixe pour alignement
        label.setVerticalAlignment(SwingConstants.CENTER);
        
        // Spinner pour date et heure
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy HH:mm");
        dateSpinner.setEditor(editor);
        dateSpinner.setPreferredSize(new Dimension(200, 35));
        dateSpinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(dateSpinner, BorderLayout.CENTER);
        
        inputContainer.add(inputPanel);
        inputContainer.add(Box.createVerticalStrut(10));
        
        return dateSpinner;
    }
    
    // NOUVELLE MÉTHODE : Ajouter un message d'erreur sous un champ
    protected void addErrorMessage(JTextField field, String message) {
        // Trouver le panel parent du champ
        Container parent = field.getParent();
        if (parent instanceof JPanel) {
            JPanel inputPanel = (JPanel) parent;
            
            // Supprimer l'ancien message d'erreur s'il existe
            Component[] components = inputPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel && ((JLabel) comp).getForeground().equals(Color.RED)) {
                    inputPanel.remove(comp);
                    break;
                }
            }
            
            // Créer le nouveau message d'erreur
            JLabel errorLabel = new JLabel(message);
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            errorLabel.setForeground(Color.RED);
            errorLabel.setHorizontalAlignment(SwingConstants.LEFT);
            
            // Ajouter le message d'erreur au panel
            inputPanel.add(errorLabel, BorderLayout.SOUTH);
            
            // Rafraîchir l'affichage
            inputPanel.revalidate();
            inputPanel.repaint();
        }
    }
    
    // NOUVELLE MÉTHODE : Supprimer les messages d'erreur
    protected void clearErrorMessages() {
        Component[] components = inputContainer.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel inputPanel = (JPanel) comp;
                Component[] inputComponents = inputPanel.getComponents();
                for (Component inputComp : inputComponents) {
                    if (inputComp instanceof JLabel && ((JLabel) inputComp).getForeground().equals(Color.RED)) {
                        inputPanel.remove(inputComp);
                    }
                }
            }
        }
        inputContainer.revalidate();
        inputContainer.repaint();
    }
    
    // Méthodes pour configurer les actions
    public void setActionButtonListener(ActionListener listener) {
        actionButton.addActionListener(listener);
    }
    
    public void setSwitchModeListener(java.awt.event.MouseListener listener) {
        switchModeLabel.addMouseListener(listener);
    }
    
    // Méthodes pour la navigation entre les pages
    protected void navigateToConnexion() {
        if (navigationController != null) {
            navigationController.navigateToPage("Connexion");
        }
    }
    
    protected void navigateToInscription() {
        if (navigationController != null) {
            navigationController.navigateToPage("Inscription");
        }
    }
    
    public String getMode() {
        return mode;
    }
    
    // Classe pour créer un panel avec coins arrondis
    private static class RoundedPanel extends JPanel {
        private int radius;
        
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false); // Important pour que le fond soit transparent
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Dessiner le fond arrondi
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Dessiner la bordure arrondie
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2d.dispose();
        }
    }
}