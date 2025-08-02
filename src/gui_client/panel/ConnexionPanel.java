package gui_client.panel;

import service.AuthService;
import service.AuthService.LoginResult;
import service.UserSessionManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConnexionPanel extends GenericAuthentificationPanel {

    private JTextField emailField;
    private JPasswordField passwordField;

    public ConnexionPanel() {
        super("connexion");
        createFormFields();
        setupEventHandlers();
    }

    private void createFormFields() {
        // Créer les champs du formulaire
        emailField = addTextInput("Email :");
        passwordField = addPasswordInput("Mot de passe :");
    }

    private void setupEventHandlers() {
        // Action du bouton de connexion
        setActionButtonListener(e -> handleConnexion());

        // Lien vers la page d'inscription
        setSwitchModeListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToInscription();
            }
        });
    }

    private void handleConnexion() {
        // Nettoyer les messages d'erreur précédents
        clearErrorMessages();

        // Récupérer les valeurs des champs
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation des champs
        boolean hasErrors = false;

        if (email.isEmpty()) {
            addErrorMessage(emailField, "L'email est obligatoire");
            hasErrors = true;
        }

        if (password.isEmpty()) {
            addErrorMessage(passwordField, "Le mot de passe est obligatoire");
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        // Tenter la connexion
        try {
            AuthService authService = AuthService.getInstance();
            LoginResult result = authService.authenticate(email, password);

            if (result.isSuccess()) {
                // Connexion réussie
                UserSessionManager.getInstance().login(result.getClient());

                JOptionPane.showMessageDialog(this,
                    "Connexion réussie !\nBienvenue " + result.getClient().getPrenom() + " !",
                    "Connexion réussie",
                    JOptionPane.INFORMATION_MESSAGE);

                // Rediriger vers la page d'accueil du client
                if (navigationController != null) {
                    navigationController.navigateToPage("Accueil");
                }

            } else {
                // Gérer les différents types d'erreurs
                handleLoginError(result.getErrorCode());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Une erreur inattendue s'est produite lors de la connexion.\n" +
                "Veuillez réessayer plus tard.",
                "Erreur système",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLoginError(String errorCode) {
        String message;
        String title = "Erreur de connexion";

        switch (errorCode) {
            case "EMAIL_NOT_FOUND":
                message = "Cette adresse email n'est pas enregistrée.\nVérifiez votre email ou créez un nouveau compte.";
                addErrorMessage(emailField, "Email non trouvé");
                break;

            case "WRONG_PASSWORD":
                message = "Mot de passe incorrect.\nVérifiez votre mot de passe et réessayez.";
                addErrorMessage(passwordField, "Mot de passe incorrect");
                break;

            case "NOT_APPROVED_MEMBER":
                message = "<html><div style='text-align: center;'>" +
                         "<h3>Compte en attente d'approbation</h3>" +
                         "<p>Votre demande d'inscription est en cours de traitement.</p>" +
                         "<p>Un administrateur doit approuver votre compte avant que vous puissiez vous connecter.</p>" +
                         "<p>Vous recevrez une notification une fois votre compte approuvé.</p>" +
                         "</div></html>";
                title = "Compte non approuvé";
                break;

            case "DATABASE_CONNECTION_ERROR":
                message = "Impossible de se connecter à la base de données.\nVérifiez votre connexion et réessayez.";
                title = "Erreur de connexion";
                break;

            case "DATABASE_ERROR":
                message = "Une erreur de base de données s'est produite.\nVeuillez réessayer plus tard.";
                title = "Erreur de base de données";
                break;

            default:
                message = "Une erreur inattendue s'est produite.\nVeuillez réessayer plus tard.";
                title = "Erreur";
                break;
        }

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Méthode pour réinitialiser le formulaire
     */
    public void resetForm() {
        emailField.setText("");
        passwordField.setText("");
        clearErrorMessages();
    }

    /**
     * Méthode pour pré-remplir l'email (utile après inscription)
     */
    public void setEmail(String email) {
        emailField.setText(email);
    }
}
