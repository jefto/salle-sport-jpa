package gui_client.panel;

import entite.TypeAbonnement;
import service.TypeAbonnementService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AbonnementBoxPanel extends JPanel {

    private TypeAbonnementService typeAbonnementService;

    public AbonnementBoxPanel() {
        this.typeAbonnementService = new TypeAbonnementService();
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new GridLayout(1, 0, 20, 0)); // Disposition horizontale avec espacement
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        chargerTypesAbonnements();
    }

    private void chargerTypesAbonnements() {
        try {
            List<TypeAbonnement> typesAbonnements = typeAbonnementService.listerTous();

            // Vider le panel avant de le remplir
            removeAll();

            if (typesAbonnements.isEmpty()) {
                add(createMessagePanel("Aucun abonnement disponible"));
            } else {
                for (TypeAbonnement type : typesAbonnements) {
                    add(createAbonnementBox(type));
                }
            }

            // Rafraîchir l'affichage
            revalidate();
            repaint();

        } catch (Exception e) {
            removeAll();
            add(createMessagePanel("Erreur lors du chargement des abonnements"));
            revalidate();
            repaint();
            System.err.println("Erreur lors du chargement des types d'abonnements: " + e.getMessage());
        }
    }

    private JPanel createAbonnementBox(TypeAbonnement type) {
        JPanel box = new JPanel();
        box.setLayout(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        // Titre de l'abonnement
        JLabel titreLabel = new JLabel(type.getLibelle(), SwingConstants.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titreLabel.setForeground(new Color(74, 41, 0));

        // Prix de l'abonnement
        JLabel prixLabel = new JLabel(type.getMontant() + " €", SwingConstants.CENTER);
        prixLabel.setFont(new Font("Arial", Font.BOLD, 24));
        prixLabel.setForeground(new Color(0, 128, 0));

        // Code de l'abonnement (optionnel, plus discret)
        JLabel codeLabel = new JLabel("Code: " + type.getCode(), SwingConstants.CENTER);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        codeLabel.setForeground(Color.GRAY);

        // Bouton de souscription
        JButton souscrireBtn = new JButton("Souscrire");
        souscrireBtn.setBackground(new Color(74, 41, 0));
        souscrireBtn.setForeground(Color.WHITE);
        souscrireBtn.setFont(new Font("Arial", Font.BOLD, 14));
        souscrireBtn.setFocusPainted(false);
        souscrireBtn.setBorderPainted(false);
        souscrireBtn.setPreferredSize(new Dimension(120, 35));

        // Action du bouton
        souscrireBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                souscrireAbonnement(type);
            }
        });

        // Panel pour organiser les informations
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(titreLabel);
        infoPanel.add(prixLabel);
        infoPanel.add(codeLabel);

        // Panel pour le bouton
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(souscrireBtn);

        // Assembler la box
        box.add(infoPanel, BorderLayout.CENTER);
        box.add(buttonPanel, BorderLayout.SOUTH);

        return box;
    }

    private JPanel createMessagePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setForeground(Color.GRAY);

        panel.add(messageLabel, BorderLayout.CENTER);
        return panel;
    }

    private void souscrireAbonnement(TypeAbonnement type) {
        // Afficher une boîte de dialogue de confirmation
        int result = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous souscrire à l'abonnement :\n" +
            type.getLibelle() + " - " + type.getMontant() + " FCFA",
            "Confirmation de souscription",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // Ici, vous pouvez ajouter la logique pour traiter la souscription
            // Par exemple, rediriger vers une page de paiement ou créer un abonnement
            JOptionPane.showMessageDialog(
                this,
                "Souscription à " + type.getLibelle() + " en cours de traitement...\n" +
                "Vous serez redirigé vers la page de paiement.",
                "Souscription",
                JOptionPane.INFORMATION_MESSAGE
            );

            // TODO: Implémenter la logique de souscription
            // - Créer un nouvel abonnement
            // - Rediriger vers la page de paiement
            // - Mettre à jour l'interface utilisateur
        }
    }

    /**
     * Méthode publique pour rafraîchir la liste des abonnements
     */
    public void rafraichirAbonnements() {
        chargerTypesAbonnements();
    }
}
