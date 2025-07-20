package gui_client.panel;

import entite.Membre;
import entite.Seance;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TicketDialog extends JDialog {

    private Membre membre;
    private Seance seance;

    public TicketDialog(Membre membre, Seance seance) {
        this.membre = membre;
        this.seance = seance;

        initializeDialog();
        createTicketContent();
    }

    private void initializeDialog() {
        setTitle("Ticket de Séance");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Style de la fenêtre
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
    }

    private void createTicketContent() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Titre du ticket
        JLabel titleLabel = new JLabel("🎫 TICKET DE SÉANCE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(74, 41, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Panel pour les informations
        JPanel infoPanel = createInfoPanel();

        // Panel pour le code QR
        JPanel qrPanel = createQRPanel();

        // Bouton fermer
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        // Assemblage
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(qrPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(74, 41, 0), 2),
                "Informations du Ticket",
                0,
                0,
                new Font("Arial", Font.BOLD, 16),
                new Color(74, 41, 0)
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 20);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Informations du membre
        addInfoRow(panel, gbc, 0, "👤 Nom :", membre.getClient().getNom() + " " + membre.getClient().getPrenom());
        addInfoRow(panel, gbc, 1, "📧 Email :", membre.getClient().getEmail());

        // Séparateur
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200, 200, 200));
        panel.add(separator, gbc);

        // Reset constraints
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 0, 8, 20);

        // Informations de la séance
        addInfoRow(panel, gbc, 3, "📅 Date :", seance.getDateDebut().format(dateFormatter));
        addInfoRow(panel, gbc, 4, "🕐 Heure de début :", seance.getDateDebut().format(timeFormatter));
        addInfoRow(panel, gbc, 5, "🕑 Heure de fin :", seance.getDateFin().format(timeFormatter));
        addInfoRow(panel, gbc, 6, "🏢 Salle :", seance.getSalle() != null ? seance.getSalle().getLibelle() : "Non définie");
        addInfoRow(panel, gbc, 7, "👥 Participants :", seance.getNombreMembres() + " membre(s) inscrit(s)");

        return panel;
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        labelComponent.setForeground(new Color(52, 73, 94));
        panel.add(labelComponent, gbc);

        // Value
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComponent.setForeground(Color.BLACK);
        panel.add(valueComponent, gbc);
    }

    private JPanel createQRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Titre du QR Code
        JLabel qrTitleLabel = new JLabel("Code QR d'accès", SwingConstants.CENTER);
        qrTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        qrTitleLabel.setForeground(new Color(74, 41, 0));
        qrTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Panel pour centrer le QR Code
        JPanel qrImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        qrImagePanel.setBackground(Color.WHITE);

        try {
            // Charger l'image du code QR depuis le dossier assets
            ImageIcon qrIcon = new ImageIcon(getClass().getClassLoader().getResource("assets/codeQr.png"));

            // Redimensionner l'image si nécessaire
            Image img = qrIcon.getImage();
            Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            JLabel qrLabel = new JLabel(scaledIcon);
            qrLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            qrImagePanel.add(qrLabel);

        } catch (Exception e) {
            // Si l'image n'est pas trouvée, afficher un placeholder
            JLabel placeholderLabel = new JLabel("📱", SwingConstants.CENTER);
            placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 80));
            placeholderLabel.setForeground(Color.GRAY);
            placeholderLabel.setPreferredSize(new Dimension(150, 150));
            placeholderLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            qrImagePanel.add(placeholderLabel);
        }

        // Message d'instruction
        JLabel instructionLabel = new JLabel("Présentez ce code QR à l'entrée de la salle", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        panel.add(qrTitleLabel, BorderLayout.NORTH);
        panel.add(qrImagePanel, BorderLayout.CENTER);
        panel.add(instructionLabel, BorderLayout.SOUTH);

        return panel;
    }
}
