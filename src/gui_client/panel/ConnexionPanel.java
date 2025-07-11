package gui_client.panel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnexionPanel extends GenericAuthentificationPanel {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    
    public ConnexionPanel() {
        super("connexion");
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
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        // Validation basique
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez remplir tous les champs", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Implémenter la logique de connexion
        JOptionPane.showMessageDialog(this, 
            "Tentative de connexion avec:\nEmail: " + email, 
            "Connexion", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}