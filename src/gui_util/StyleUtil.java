package gui_util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleUtil {
    public static void styliserTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(255, 231, 77));
        header.setForeground(Color.BLACK);

        // rendre l'en-tête non déplaçable
        header.setReorderingAllowed(false);

        // définir une police personnalisée
        header.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    /**
     * Met en surbrillance le bouton de navigation de la page active (seulement le texte)
     * @param buttons Liste des boutons de navigation
     * @param currentPage Nom de la page actuelle
     */
    public static void highlightActiveNavigationButton(java.util.List<JButton> buttons, String currentPage) {
        for (JButton button : buttons) {
            if (button.getText().equals(currentPage)) {
                // Style du bouton actif - SEULEMENT le texte en jaune
                button.setForeground(new Color(255, 231, 77));
                button.setBackground(new Color(74, 41, 0));
                button.setContentAreaFilled(false);
                button.setFont(new Font("Arial", Font.BOLD, 14));
            } else {
                // Style des boutons inactifs - texte blanc
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(74, 41, 0));
                button.setContentAreaFilled(false);
                button.setFont(new Font("Arial", Font.BOLD, 14));
            }
        }
    }
    
    /**
     * Configure les actions pour les boutons d'authentification avec un callback
     * @param loginButton Bouton de connexion
     * @param registerButton Bouton d'inscription
     * @param onLogin Action à exécuter lors du clic sur connexion
     * @param onRegister Action à exécuter lors du clic sur inscription
     */
    public static void configureAuthenticationButtons(JButton loginButton, JButton registerButton, 
                                                     Runnable onLogin, Runnable onRegister) {
        
        // Action pour le bouton "Se connecter"
        loginButton.addActionListener(e -> onLogin.run());
        
        // Action pour le bouton "S'inscrire"
        registerButton.addActionListener(e -> onRegister.run());
    }
}