package gui_client.panel;

import service.AuthService;
import service.AuthService.LoginResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InscriptionPanel extends GenericAuthentificationPanel {

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JSpinner dateNaissanceSpinner;

    public InscriptionPanel() {
        super("inscription");
        createFormFields();
        setupEventHandlers();
    }

    private void createFormFields() {
        // Créer les champs du formulaire
        nomField = addTextInput("Nom :");
        prenomField = addTextInput("Prénom :");
        emailField = addTextInput("Email :");
        passwordField = addPasswordInput("Mot de passe :");
        confirmPasswordField = addPasswordInput("Confirmer :");
        dateNaissanceSpinner = addDateTimeInput("Date naissance :");

        // Configurer le spinner de date pour afficher seulement la date
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateNaissanceSpinner, "dd/MM/yyyy");
        dateNaissanceSpinner.setEditor(dateEditor);

        // Définir une date par défaut (il y a 18 ans)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, -18);
        dateNaissanceSpinner.setValue(cal.getTime());
    }

    private void setupEventHandlers() {
        // Action du bouton d'inscription
        setActionButtonListener(e -> handleInscription());

        // Lien vers la page de connexion
        setSwitchModeListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToConnexion();
            }
        });
    }

    private void handleInscription() {
        // Nettoyer les messages d'erreur précédents
        clearErrorMessages();

        // Récupérer les valeurs des champs
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        Date dateNaissance = (Date) dateNaissanceSpinner.getValue();

        // Validation des champs
        boolean hasErrors = false;

        if (nom.isEmpty()) {
            addErrorMessage(nomField, "Le nom est obligatoire");
            hasErrors = true;
        }

        if (prenom.isEmpty()) {
            addErrorMessage(prenomField, "Le prénom est obligatoire");
            hasErrors = true;
        }

        if (email.isEmpty()) {
            addErrorMessage(emailField, "L'email est obligatoire");
            hasErrors = true;
        } else if (!isValidEmail(email)) {
            addErrorMessage(emailField, "Format d'email invalide");
            hasErrors = true;
        }

        if (password.isEmpty()) {
            addErrorMessage(passwordField, "Le mot de passe est obligatoire");
            hasErrors = true;
        } else if (password.length() < 6) {
            addErrorMessage(passwordField, "Le mot de passe doit contenir au moins 6 caractères");
            hasErrors = true;
        }

        if (!password.equals(confirmPassword)) {
            addErrorMessage(confirmPasswordField, "Les mots de passe ne correspondent pas");
            hasErrors = true;
        }

        // Vérifier l'âge minimum (13 ans)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, -13);
        if (dateNaissance.after(cal.getTime())) {
            JOptionPane.showMessageDialog(this,
                "Vous devez avoir au moins 13 ans pour vous inscrire.",
                "Âge insuffisant",
                JOptionPane.WARNING_MESSAGE);
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        // Tenter l'inscription
        try {
            AuthService authService = AuthService.getInstance();
            LoginResult result = authService.register(nom, prenom, email, password, dateNaissance);

            if (result.isSuccess()) {
                // Inscription réussie, mais en attente d'approbation
                JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center;'>" +
                    "<h3>Inscription en cours de traitement</h3>" +
                    "<p>Votre demande d'inscription a été envoyée avec succès !</p>" +
                    "<p>Un administrateur va examiner votre demande.</p>" +
                    "<p>Vous recevrez une notification une fois votre compte approuvé.</p>" +
                    "<p>Vous pourrez alors vous connecter à votre espace membre.</p>" +
                    "</div></html>",
                    "Inscription en attente",
                    JOptionPane.INFORMATION_MESSAGE);

                // Rediriger vers la page de connexion
                navigateToConnexion();

            } else {
                // Gérer les différents types d'erreurs
                handleRegistrationError(result.getErrorCode());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Une erreur inattendue s'est produite lors de l'inscription.\n" +
                "Veuillez réessayer plus tard.",
                "Erreur système",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegistrationError(String errorCode) {
        String message;
        String title = "Erreur d'inscription";

        switch (errorCode) {
            case "EMAIL_ALREADY_EXISTS":
                message = "Cette adresse email est déjà utilisée.\nVeuillez utiliser une autre adresse email.";
                addErrorMessage(emailField, "Cette adresse email est déjà utilisée");
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Méthode pour réinitialiser le formulaire
     */
    public void resetForm() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");

        // Remettre la date par défaut
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, -18);
        dateNaissanceSpinner.setValue(cal.getTime());

        clearErrorMessages();
    }
}
