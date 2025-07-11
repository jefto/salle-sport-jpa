package gui_util;

import service.AuthenticationService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean authenticated = false;
    private AuthenticationService authService;
    
    public LoginDialog(Frame parent, String title) {
        super(parent, title, true);
        authService = AuthenticationService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(getParent());
        
        // Panel central avec les champs
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Nom d'utilisateur
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        centerPanel.add(new JLabel("Nom d'utilisateur :"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        centerPanel.add(usernameField, gbc);
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(new JLabel("Mot de passe :"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        centerPanel.add(passwordField, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Connexion");
        cancelButton = new JButton("Annuler");
        
        loginButton.addActionListener(new LoginActionListener());
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Validation avec Enter
        getRootPane().setDefaultButton(loginButton);
    }
    
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginDialog.this, 
                    "Veuillez remplir tous les champs.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Vérifier l'authentification
            if (authService.authenticateAdmin(username, password)) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this, 
                    "Nom d'utilisateur ou mot de passe incorrect.", 
                    "Erreur d'authentification", 
                    JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
}