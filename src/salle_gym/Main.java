package salle_gym;

import entite.*;
import gui_admin.AdminDashboard;
import gui_client.ClientDashboard;
import gui_util.LoginDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    
    public static void main(String[] args) {
        // Lancer l'interface graphique dans le thread EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Utiliser le Look and Feel par défaut de Java (Metal) au lieu du système
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                
                // Ou encore mieux, utiliser Nimbus si disponible
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Créer un menu de sélection
            showInterfaceSelector();
        });
    }
    
    private static void showInterfaceSelector() {
        JFrame selectorFrame = new JFrame("FITPlus+ - Sélection d'interface");
        selectorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectorFrame.setSize(400, 300);
        selectorFrame.setLocationRelativeTo(null);
        selectorFrame.setLayout(new BorderLayout());
        
        // Logo et titre
        JLabel titleLabel = new JLabel("FITPlus+", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        JLabel subtitleLabel = new JLabel("Choisissez votre interface", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        JButton adminButton = new JButton("Interface Admin");
        JButton clientButton = new JButton("Interface Client");
        
        styleButton(adminButton, new Color(52, 73, 94));
        styleButton(clientButton, new Color(46, 204, 113));
        
        adminButton.addActionListener(e -> {
            selectorFrame.dispose();
            showAdminInterface();
        });
        
        clientButton.addActionListener(e -> {
            selectorFrame.dispose();
            showClientInterface();
        });
        
        buttonPanel.add(adminButton);
        buttonPanel.add(clientButton);
        
        selectorFrame.add(headerPanel, BorderLayout.NORTH);
        selectorFrame.add(buttonPanel, BorderLayout.CENTER);
        selectorFrame.setVisible(true);
    }
    
    private static void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
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
    }
    
    private static void showAdminInterface() {
        // Afficher la fenêtre de connexion admin
        LoginDialog loginDialog = new LoginDialog(null, "Connexion Admin");
        loginDialog.setVisible(true);

        // Si authentifié, ouvrir le dashboard admin
        if (loginDialog.isAuthenticated()) {
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);
        } else {
            showInterfaceSelector(); // Retourner au menu
        }
    }
    
    private static void showClientInterface() {
        // Ouvrir directement l'interface client
        ClientDashboard clientDashboard = new ClientDashboard();
        clientDashboard.setVisible(true);
    }
}