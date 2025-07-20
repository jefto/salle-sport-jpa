package gui_admin.panel;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel {

    public NotificationPanel() {
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Titre du panel
        JLabel titleLabel = new JLabel("Notifications", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Zone principale des notifications
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Liste des notifications (exemple)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("📧 Nouvelle demande d'inscription de Jean Dupont");
        listModel.addElement("💳 Paiement reçu pour l'abonnement de Marie Martin");
        listModel.addElement("⚠️ Équipement 'Tapis de course 3' nécessite une maintenance");
        listModel.addElement("👤 Nouveau membre inscrit: Pierre Durand");
        listModel.addElement("📅 Séance de yoga programmée pour demain 14h");

        JList<String> notificationList = new JList<>(listModel);
        notificationList.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style de la liste
        notificationList.setBackground(new Color(248, 249, 250));
        notificationList.setSelectionBackground(new Color(74, 41, 0));
        notificationList.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Notifications récentes"));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton markAllReadButton = new JButton("Marquer tout comme lu");
        markAllReadButton.setBackground(new Color(74, 41, 0));
        markAllReadButton.setForeground(Color.WHITE);
        markAllReadButton.setFont(new Font("Arial", Font.BOLD, 12));
        markAllReadButton.setFocusPainted(false);

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);

        // Actions des boutons
        markAllReadButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Toutes les notifications ont été marquées comme lues.",
                "Notifications",
                JOptionPane.INFORMATION_MESSAGE);
        });

        refreshButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Notifications actualisées.",
                "Actualisation",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(markAllReadButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }
}
