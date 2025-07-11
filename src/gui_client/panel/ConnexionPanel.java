package gui_client.panel;

import service.AuthService;
import service.UserSessionManager;
import entite.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnexionPanel extends GenericAuthentificationPanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private AuthService authService;

    public ConnexionPanel() {
        super("connexion");
        this.authService = AuthService.getInstance();
        initializeInputs();
        configureActions();
    }

    private void initializeInputs() {
        // Ajouter les champs spécifiques à la connexion
        emailField = addTextInput("Email");
        passwordField = addPasswordInput("Mot de passe");
    }

    private void configureActions() {
        // Action du bouton de connexion
        setActionButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Action pour changer vers l'inscription
        setSwitchModeListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateToInscription(); // Utilise la méthode de la classe parent
            }
        });
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Effacer les anciens messages d'erreur
        clearErrorMessages();

        // Validation basique
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Authentification
        AuthService.LoginResult result = authService.authenticate(email, password);

        if (result.isSuccess()) {
            // Connexion réussie
            Client client = result.getClient();
            UserSessionManager.getInstance().login(client);

            // Afficher un message de succès
            JOptionPane.showMessageDialog(this,
                "Connexion réussie ! Bienvenue " + client.getPrenom() + " " + client.getNom(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);

            // Rediriger vers la page d'accueil
            if (navigationController != null) {
                navigationController.navigateToPage("Accueil");
            }

        } else {
            // Connexion échouée
            switch (result.getErrorCode()) {
                case "EMAIL_NOT_FOUND":
                    addErrorMessage(emailField, "Email entré incorrect");
                    break;
                case "WRONG_PASSWORD":
                    addErrorMessage(passwordField, "Mot de passe incorrect");
                    break;
                case "DATABASE_CONNECTION_ERROR":
                    JOptionPane.showMessageDialog(this,
                        "Impossible de se connecter à la base de données.\n\n" +
                        "Veuillez vérifier que :\n" +
                        "1. Le service MySQL/MariaDB est démarré\n" +
                        "2. Votre connexion réseau fonctionne correctement\n" +
                        "3. Les paramètres de connexion sont corrects\n\n" +
                        "Pour démarrer le service MySQL/MariaDB :\n" +
                        "- Ouvrez les Services Windows (services.msc)\n" +
                        "- Recherchez le service 'MySQL' ou 'MariaDB'\n" +
                        "- Cliquez-droit et sélectionnez 'Démarrer'",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case "DATABASE_ACCESS_DENIED":
                    JOptionPane.showMessageDialog(this,
                        "Accès à la base de données refusé. Veuillez contacter l'administrateur.",
                        "Erreur d'accès",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case "DATABASE_NOT_FOUND":
                    JOptionPane.showMessageDialog(this,
                        "Base de données introuvable. Veuillez contacter l'administrateur.",
                        "Erreur de configuration",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case "DATABASE_ERROR":
                    JOptionPane.showMessageDialog(this,
                        "Erreur de base de données. Veuillez réessayer ultérieurement.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                case "UNEXPECTED_ERROR":
                    JOptionPane.showMessageDialog(this,
                        "Une erreur inattendue s'est produite. Veuillez réessayer ultérieurement.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this,
                        "Erreur inconnue. Veuillez réessayer.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
