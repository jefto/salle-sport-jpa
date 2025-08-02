package gui_client.panel;

import entite.TypeAbonnement;
import entite.Membre;
import service.TypeAbonnementService;
import service.UserSessionManager;
import gui_client.util.AbonnementSouscription;
import gui_client.util.AbonnementSouscription.ResultatValidation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AbonnementBoxPanel extends JPanel {

    private TypeAbonnementService typeAbonnementService;
    private AbonnementSouscription abonnementUtil;
    private gui_client.controller.ClientNavigationController navigationController;

    public AbonnementBoxPanel() {
        this.typeAbonnementService = new TypeAbonnementService();
        this.abonnementUtil = AbonnementSouscription.getInstance();
        initializePanel();
    }

    // Méthode pour définir le contrôleur de navigation
    public void setNavigationController(gui_client.controller.ClientNavigationController controller) {
        this.navigationController = controller;
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
        JLabel prixLabel = new JLabel(type.getMontant() + " FCFA", SwingConstants.CENTER);
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
        // Vérifier si l'utilisateur est connecté
        if (!UserSessionManager.getInstance().isLoggedIn()) {
            showAuthenticationRequiredDialog(type);
            return;
        }

        try {
            // Récupérer le membre connecté via l'utilitaire
            Integer clientId = UserSessionManager.getInstance().getCurrentUserId();
            Membre membre = abonnementUtil.getMembreByClientId(clientId);

            if (membre == null) {
                JOptionPane.showMessageDialog(this,
                    "Erreur : Aucun profil membre trouvé pour votre compte.\n" +
                    "Veuillez contacter l'administration.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si la souscription est possible
            ResultatValidation validation = abonnementUtil.peutSouscrire(membre);

            if (!validation.isAutorise() && !validation.isRenouvellementProche()) {
                // Afficher le message d'erreur avec les détails
                String message = validation.getMessage() + "\n\n";

                if (validation.getAbonnementActif() != null) {
                    long joursRestants = abonnementUtil.getJoursRestants(validation.getAbonnementActif());
                    message += "Votre abonnement actuel expire dans " + joursRestants + " jour(s).\n";
                    message += "Vous pourrez renouveler quand il restera 7 jours ou moins.";
                }

                JOptionPane.showMessageDialog(this,
                    message,
                    "Souscription non autorisée",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Trouver la fenêtre parent
            Frame parentFrame = null;
            Container parent = this.getTopLevelAncestor();
            if (parent instanceof Frame) {
                parentFrame = (Frame) parent;
            }

            // Ouvrir la fenêtre de paiement
            PaiementPanel.afficherPaiement(parentFrame, type, membre);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de la fenêtre de paiement:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Affiche un dialogue demandant à l'utilisateur de se connecter d'abord
     */
    private void showAuthenticationRequiredDialog(TypeAbonnement type) {
        // Créer un panel personnalisé pour le message
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ic��ne d'avertissement
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));

        // Message principal
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center; width: 300px;'>" +
            "<h3>Authentification requise</h3>" +
            "<p>Pour souscrire à l'abonnement <b>" + type.getLibelle() + "</b>, " +
            "vous devez d'abord vous connecter ou créer un compte.</p>" +
            "<p>Souhaitez-vous vous connecter maintenant ?</p>" +
            "</div></html>"
        );
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Options personnalisées
        String[] options = {"Se connecter", "S'inscrire", "Annuler"};

        int choice = JOptionPane.showOptionDialog(
            this,
            messagePanel,
            "Authentification requise",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0] // Option par défaut
        );

        // Gérer la réponse de l'utilisateur
        switch (choice) {
            case 0: // Se connecter
                navigateToLogin();
                break;
            case 1: // S'inscrire
                navigateToRegister();
                break;
            case 2: // Annuler
                // Ne rien faire, fermer la boîte de dialogue
                break;
        }
    }

    /**
     * Navigate vers la page de connexion
     */
    private void navigateToLogin() {
        if (navigationController != null) {
            navigationController.navigateToPage("Connexion");
        } else {
            // Fallback si le contrôleur n'est pas disponible
            Container parent = this.getParent();
            while (parent != null && !(parent instanceof gui_client.ClientDashboard)) {
                parent = parent.getParent();
            }
            if (parent instanceof gui_client.ClientDashboard) {
                ((gui_client.ClientDashboard) parent).navigateToPage("Connexion");
            }
        }
    }

    /**
     * Navigate vers la page d'inscription
     */
    private void navigateToRegister() {
        if (navigationController != null) {
            navigationController.navigateToPage("Inscription");
        } else {
            // Fallback si le contrôleur n'est pas disponible
            Container parent = this.getParent();
            while (parent != null && !(parent instanceof gui_client.ClientDashboard)) {
                parent = parent.getParent();
            }
            if (parent instanceof gui_client.ClientDashboard) {
                ((gui_client.ClientDashboard) parent).navigateToPage("Inscription");
            }
        }
    }

    /**
     * Méthode publique pour rafraîchir la liste des abonnements
     */
    public void rafraichirAbonnements() {
        chargerTypesAbonnements();
    }
}
