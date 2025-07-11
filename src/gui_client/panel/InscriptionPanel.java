package gui_client.panel;

import service.AuthService;
import service.UserSessionManager;
import entite.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class InscriptionPanel extends GenericAuthentificationPanel {
    
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JSpinner dateNaissanceSpinner;
    private AuthService authService;
    
    public InscriptionPanel() {
        super("inscription");
        this.authService = AuthService.getInstance();
        initializeInputs();
        configureActions();
    }
    
    private void initializeInputs() {
        // Ajouter les champs spécifiques à l'inscription
        nomField = addTextInput("Nom");
        prenomField = addTextInput("Prénom");
        emailField = addTextInput("Email");
        passwordField = addPasswordInput("Mot de passe");
        dateNaissanceSpinner = addDateTimeInput("Date et heure de naissance");
    }
    
    private void configureActions() {
        // Action du bouton d'inscription
        setActionButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleInscription();
            }
        });
        
        // Action pour changer vers la connexion
        setSwitchModeListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateToConnexion(); // Utilise la méthode de la classe parent
            }
        });
    }
    
    private void handleInscription() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Date dateNaissance = (Date) dateNaissanceSpinner.getValue();
        
        // Effacer les anciens messages d'erreur
        clearErrorMessages();
        
        // Validation basique
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validation email basique
        if (!email.contains("@") || !email.contains(".")) {
            addErrorMessage(emailField, "Format d'email invalide");
            return;
        }
        
        // Validation mot de passe (minimum 6 caractères)
        if (password.length() < 6) {
            addErrorMessage(passwordField, "Le mot de passe doit contenir au moins 6 caractères");
            return;
        }
        
        // Inscription
        AuthService.LoginResult result = authService.register(nom, prenom, email, password, dateNaissance);
        
        if (result.isSuccess()) {
            // Inscription réussie
            Client client = result.getClient();
            UserSessionManager.getInstance().login(client);
            
            // Afficher un message de succès
            JOptionPane.showMessageDialog(this,
                "Inscription réussie ! Bienvenue " + client.getPrenom() + " " + client.getNom(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Rediriger vers la page d'accueil
            if (navigationController != null) {
                navigationController.navigateToPage("Accueil");
            }
            
        } else {
            // Inscription échouée
            switch (result.getErrorCode()) {
                case "EMAIL_ALREADY_EXISTS":
                    addErrorMessage(emailField, "Cet email est déjà utilisé");
                    break;
                case "DATABASE_ERROR":
                    JOptionPane.showMessageDialog(this,
                        "Erreur de base de données. Veuillez réessayer.",
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