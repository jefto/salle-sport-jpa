package gui_client.panel;

import javax.swing.*;
import java.awt.*;

public class AbonnementClientPanel extends JPanel {
    
    private AbonnementBoxPanel abonnementBoxPanel;

    public AbonnementClientPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializePanel();
    }

    private void initializePanel() {
        // Titre principal de la page
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Nos Abonnements", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(74, 41, 0));

        JLabel subtitleLabel = new JLabel("Choisissez l'abonnement qui vous convient", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Section des abonnements
        abonnementBoxPanel = new AbonnementBoxPanel();

        // Panel de contenu avec scroll
        JScrollPane scrollPane = new JScrollPane(abonnementBoxPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Assembler la page
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Méthode pour rafraîchir les abonnements (utile si les données changent)
     */
    public void rafraichir() {
        if (abonnementBoxPanel != null) {
            abonnementBoxPanel.rafraichirAbonnements();
        }
    }
}