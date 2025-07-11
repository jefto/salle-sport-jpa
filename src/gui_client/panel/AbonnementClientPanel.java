package gui_client.panel;

import javax.swing.*;
import java.awt.*;

public class AbonnementClientPanel extends JPanel {
    
    public AbonnementClientPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Page de souscription d'abonnement en cours de développement", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.GRAY);
        
        add(label, BorderLayout.CENTER);
    }
}