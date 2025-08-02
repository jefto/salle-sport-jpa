package gui_client.panel;

import entite.Client;
import entite.Abonnement;
import entite.Membre;
import entite.TypeAbonnement;
import service.UserSessionManager;
import service.ClientService;
import gui_client.util.AbonnementSouscription;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfilPanel extends JPanel {
    
    private ClientService clientService;
    private AbonnementSouscription abonnementUtil;

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

    // Table pour l'historique des abonnements
    private JTable historiqueTable;
    private javax.swing.table.DefaultTableModel historiqueTableModel;

    public ProfilPanel() {
        // Initialiser les services
        clientService = new ClientService();
        abonnementUtil = AbonnementSouscription.getInstance();

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

        // Nouvelle section: Historique des abonnements
        gbc.gridy = 3;
        contentPanel.add(createHistoriqueSection(), gbc);

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

    private JPanel createHistoriqueSection() {
        JPanel section = createSectionPanel("Historique des Abonnements");

        // Table pour l'historique
        historiqueTableModel = new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Type d'Abonnement", "Date de Début", "Date de Fin", "Statut"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        historiqueTable = new JTable(historiqueTableModel);
        historiqueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historiqueTable.setRowHeight(30);
        historiqueTable.setSelectionBackground(new Color(52, 152, 219));
        historiqueTable.setSelectionForeground(Color.WHITE);
        historiqueTable.setDefaultEditor(Object.class, null); // Rendre les cellules non éditables

        // Charger les données de l'historique
        loadHistoriqueData();

        JScrollPane tableScrollPane = new JScrollPane(historiqueTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setPreferredSize(new Dimension(0, 150));

        section.add(tableScrollPane, BorderLayout.CENTER);
        return section;
    }

    private void loadSubscriptionInfo() {
        try {
            Client currentUser = UserSessionManager.getInstance().getCurrentUser();

            // Récupérer le membre via l'utilitaire
            Membre membre = abonnementUtil.getMembreByClientId(currentUser.getId());

            if (membre != null) {
                // Récupérer l'abonnement actif via l'utilitaire
                Abonnement abonnementActif = abonnementUtil.getAbonnementActif(membre);

                if (abonnementActif != null) {
                    // Afficher les informations d'abonnement
                    TypeAbonnement typeAbonnement = abonnementActif.getTypeAbonnement();
                    abonnementTypeLabel.setText(typeAbonnement != null ? typeAbonnement.getLibelle() : "Non défini");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dateDebutLabel.setText(abonnementActif.getDateDebut().format(formatter));
                    dateFinLabel.setText(abonnementActif.getDateFin().format(formatter));

                    // Vérifier si l'abonnement est encore actif
                    if (abonnementUtil.isAbonnementActif(abonnementActif)) {
                        long joursRestants = abonnementUtil.getJoursRestants(abonnementActif);
                        statutAbonnementLabel.setText("Actif (" + joursRestants + " jour(s) restant(s))");

                        if (joursRestants <= 7) {
                            statutAbonnementLabel.setForeground(new Color(255, 165, 0)); // Orange pour renouvellement proche
                        } else {
                            statutAbonnementLabel.setForeground(new Color(46, 204, 113)); // Vert pour actif
                        }
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

    private void loadHistoriqueData() {
        try {
            historiqueTableModel.setRowCount(0); // Réinitialiser les données de la table

            Client currentUser = UserSessionManager.getInstance().getCurrentUser();

            // Récupérer le membre via l'utilitaire
            Membre membre = abonnementUtil.getMembreByClientId(currentUser.getId());

            if (membre != null) {
                // Récupérer l'historique via l'utilitaire
                List<Abonnement> abonnements = abonnementUtil.getHistoriqueAbonnements(membre);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Abonnement abonnement : abonnements) {
                    // Déterminer le statut avec plus de détails
                    String statut;
                    if (abonnementUtil.isAbonnementActif(abonnement)) {
                        long joursRestants = abonnementUtil.getJoursRestants(abonnement);
                        if (joursRestants <= 7) {
                            statut = "Actif (expire bientôt)";
                        } else {
                            statut = "Actif";
                        }
                    } else {
                        statut = "Expiré";
                    }

                    // Ajouter une ligne pour chaque abonnement
                    historiqueTableModel.addRow(new Object[] {
                        abonnement.getTypeAbonnement() != null ? abonnement.getTypeAbonnement().getLibelle() : "Non défini",
                        abonnement.getDateDebut().format(formatter),
                        abonnement.getDateFin().format(formatter),
                        statut
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            loadHistoriqueData();
        }
    }
}
