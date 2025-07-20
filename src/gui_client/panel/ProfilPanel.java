package gui_client.panel;

import entite.Client;
import entite.Abonnement;
import entite.Membre;
import entite.TypeAbonnement;
import service.UserSessionManager;
import service.ClientService;
import service.AbonnementService;
import service.MembreService;
import dao.AbonnementDao;
import dao.MembreDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ProfilPanel extends JPanel {
    
    private ClientService clientService;
    private AbonnementService abonnementService;
    private MembreService membreService;
    private AbonnementDao abonnementDao;
    private MembreDao membreDao;

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private JLabel abonnementTypeLabel;
    private JLabel dateDebutLabel;
    private JLabel dateFinLabel;
    private JLabel statutAbonnementLabel;

    public ProfilPanel() {
        // Initialiser les services
        clientService = new ClientService();
        abonnementService = new AbonnementService();
        membreService = new MembreService();
        abonnementDao = new AbonnementDao();
        membreDao = new MembreDao();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Vérifier si l'utilisateur est connecté
        if (!UserSessionManager.getInstance().isLoggedIn()) {
            add(createNotLoggedInPanel(), BorderLayout.CENTER);
            return;
        }

        // Créer l'interface principale
        createMainInterface();
    }

    private JPanel createNotLoggedInPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("Veuillez vous connecter pour accéder à votre profil", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.GRAY);

        panel.add(messageLabel, BorderLayout.CENTER);
        return panel;
    }

    private void createMainInterface() {
        // Titre principal
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Mon Profil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(74, 41, 0));
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Contenu principal avec scroll
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Section Informations personnelles
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        contentPanel.add(createPersonalInfoSection(), gbc);

        // Section Modification du mot de passe
        gbc.gridy = 1;
        contentPanel.add(createPasswordSection(), gbc);

        // Section Abonnement
        gbc.gridy = 2;
        contentPanel.add(createSubscriptionSection(), gbc);

        return contentPanel;
    }

    private JPanel createPersonalInfoSection() {
        JPanel section = createSectionPanel("Informations personnelles");

        Client currentUser = UserSessionManager.getInstance().getCurrentUser();

        // Champs d'information
        nomField = createInfoField("Nom :", currentUser.getNom(), true);
        prenomField = createInfoField("Prénom :", currentUser.getPrenom(), true);
        emailField = createInfoField("Email :", currentUser.getEmail(), true);

        // Layout des champs
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Nom
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(nomField, gbc);

        // Prénom
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Prénom :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(prenomField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Email :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(emailField, gbc);

        section.add(fieldsPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createPasswordSection() {
        JPanel section = createSectionPanel("Modification du mot de passe");

        // Champs de mot de passe
        currentPasswordField = new JPasswordField();
        newPasswordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        stylePasswordField(currentPasswordField);
        stylePasswordField(newPasswordField);
        stylePasswordField(confirmPasswordField);

        // Layout des champs
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Mot de passe actuel
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Mot de passe actuel :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(currentPasswordField, gbc);

        // Nouveau mot de passe
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Nouveau mot de passe :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(newPasswordField, gbc);

        // Confirmer mot de passe
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(new JLabel("Confirmer mot de passe :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(confirmPasswordField, gbc);

        // Bouton de modification
        JButton changePasswordButton = createActionButton("Modifier le mot de passe", new Color(52, 152, 219));
        changePasswordButton.addActionListener(e -> handlePasswordChange());

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 0, 5, 0);
        fieldsPanel.add(changePasswordButton, gbc);

        section.add(fieldsPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createSubscriptionSection() {
        JPanel section = createSectionPanel("Mon Abonnement");

        // Labels pour les informations d'abonnement
        abonnementTypeLabel = new JLabel();
        dateDebutLabel = new JLabel();
        dateFinLabel = new JLabel();
        statutAbonnementLabel = new JLabel();

        styleInfoLabel(abonnementTypeLabel);
        styleInfoLabel(dateDebutLabel);
        styleInfoLabel(dateFinLabel);
        styleInfoLabel(statutAbonnementLabel);

        // Charger les informations d'abonnement
        loadSubscriptionInfo();

        // Layout des informations
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Type d'abonnement
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Type d'abonnement :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        infoPanel.add(abonnementTypeLabel, gbc);

        // Date de début
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        infoPanel.add(new JLabel("Date de début :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        infoPanel.add(dateDebutLabel, gbc);

        // Date de fin
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        infoPanel.add(new JLabel("Date de fin :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        infoPanel.add(dateFinLabel, gbc);

        // Statut
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        infoPanel.add(new JLabel("Statut :"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        infoPanel.add(statutAbonnementLabel, gbc);

        section.add(infoPanel, BorderLayout.CENTER);
        return section;
    }

    private void loadSubscriptionInfo() {
        try {
            Client currentUser = UserSessionManager.getInstance().getCurrentUser();

            // Récupérer le membre correspondant au client en utilisant la méthode générique
            Membre membre = membreDao.trouverPar("client.id", currentUser.getId());

            if (membre != null) {
                // Récupérer l'abonnement actuel du membre en utilisant une requête JPQL personnalisée
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("membreId", membre.getId());
                parameters.put("now", java.time.LocalDateTime.now());

                String jpql = "SELECT a FROM Abonnement a WHERE a.membre.id = :membreId AND a.dateFin >= :now ORDER BY a.dateFin DESC";
                List<Abonnement> abonnements = abonnementDao.listerParRequete(jpql, parameters);

                if (!abonnements.isEmpty()) {
                    Abonnement abonnement = abonnements.get(0); // Prendre le plus récent

                    // Afficher les informations d'abonnement
                    TypeAbonnement typeAbonnement = abonnement.getTypeAbonnement();
                    abonnementTypeLabel.setText(typeAbonnement != null ? typeAbonnement.getLibelle() : "Non défini");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateDebutLabel.setText(abonnement.getDateDebut().format(formatter));
                    dateFinLabel.setText(abonnement.getDateFin().format(formatter));

                    // Vérifier si l'abonnement est encore actif
                    if (abonnement.getDateFin().isAfter(java.time.LocalDateTime.now())) {
                        statutAbonnementLabel.setText("Actif");
                        statutAbonnementLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        statutAbonnementLabel.setText("Expiré");
                        statutAbonnementLabel.setForeground(new Color(231, 76, 60));
                    }
                } else {
                    // Aucun abonnement actif
                    abonnementTypeLabel.setText("Aucun abonnement actif");
                    dateDebutLabel.setText("-");
                    dateFinLabel.setText("-");
                    statutAbonnementLabel.setText("Inactif");
                    statutAbonnementLabel.setForeground(new Color(149, 165, 166));
                }
            } else {
                // Client non encore membre
                abonnementTypeLabel.setText("Vous n'êtes pas encore membre");
                dateDebutLabel.setText("-");
                dateFinLabel.setText("-");
                statutAbonnementLabel.setText("Non membre");
                statutAbonnementLabel.setForeground(new Color(149, 165, 166));
            }

        } catch (Exception e) {
            e.printStackTrace();
            abonnementTypeLabel.setText("Erreur lors du chargement");
            dateDebutLabel.setText("-");
            dateFinLabel.setText("-");
            statutAbonnementLabel.setText("Erreur");
            statutAbonnementLabel.setForeground(new Color(231, 76, 60));
        }
    }

    private void handlePasswordChange() {
        try {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validations
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client currentUser = UserSessionManager.getInstance().getCurrentUser();

            // Vérifier le mot de passe actuel
            if (!currentUser.getMotDePasse().equals(currentPassword)) {
                JOptionPane.showMessageDialog(this, "Le mot de passe actuel est incorrect.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier que les nouveaux mots de passe correspondent
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les nouveaux mots de passe ne correspondent pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier la longueur du nouveau mot de passe
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Le nouveau mot de passe doit contenir au moins 6 caractères.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mettre à jour le mot de passe
            currentUser.setMotDePasse(newPassword);
            clientService.modifier(currentUser);

            // Réinitialiser les champs
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");

            JOptionPane.showMessageDialog(this, "Mot de passe modifié avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification du mot de passe : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthodes utilitaires pour le style
    private JPanel createSectionPanel(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));

        // Titre de la section
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        section.add(titleLabel, BorderLayout.NORTH);
        return section;
    }

    private JTextField createInfoField(String placeholder, String value, boolean readOnly) {
        JTextField field = new JTextField(value);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        if (readOnly) {
            field.setEditable(false);
            field.setBackground(new Color(248, 249, 250));
        }

        return field;
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void styleInfoLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
    }

    private JButton createActionButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    /**
     * Méthode pour rafraîchir les informations du profil
     */
    public void refreshProfile() {
        if (UserSessionManager.getInstance().isLoggedIn()) {
            Client currentUser = UserSessionManager.getInstance().getCurrentUser();
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
            loadSubscriptionInfo();
        }
    }
}

