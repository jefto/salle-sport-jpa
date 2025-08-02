package gui_client.panel;

import entite.*;
import gui_client.util.AbonnementSouscription;
import gui_client.util.AbonnementSouscription.ResultatValidation;
import gui_client.util.AbonnementSouscription.ResultatSouscription;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PaiementPanel extends JDialog {
    private TypeAbonnement typeAbonnement;
    private Membre membre;
    private JComboBox<MoyenDePaiement> moyenPaiementCombo;
    private JLabel nomAbonnementLabel;
    private JLabel montantLabel;
    private JLabel infoLabel;
    private JButton payerButton;
    private JButton annulerButton;

    // Utilitaire pour la logique métier
    private AbonnementSouscription abonnementUtil;

    public PaiementPanel(Frame parent, TypeAbonnement typeAbonnement, Membre membre) {
        super(parent, "FITPLUS+ Paiement", true);
        this.typeAbonnement = typeAbonnement;
        this.membre = membre;
        this.abonnementUtil = AbonnementSouscription.getInstance();

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadMoyensPaiement();
        validateSouscription();

        // Configuration de la fenêtre
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {
        // Nom de l'abonnement
        nomAbonnementLabel = new JLabel("Abonnement: " + typeAbonnement.getLibelle(), JLabel.CENTER);
        nomAbonnementLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Montant
        montantLabel = new JLabel("Montant: " + typeAbonnement.getMontant() + " FCFA", JLabel.CENTER);
        montantLabel.setFont(new Font("Arial", Font.BOLD, 16));
        montantLabel.setForeground(new Color(0, 150, 0));

        // Label d'information
        infoLabel = new JLabel("", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Moyen de paiement
        moyenPaiementCombo = new JComboBox<>();
        moyenPaiementCombo.setPreferredSize(new Dimension(200, 30));

        // Boutons
        payerButton = new JButton("Payer");
        payerButton.setBackground(new Color(0, 150, 0));
        payerButton.setForeground(Color.WHITE);
        payerButton.setFont(new Font("Arial", Font.BOLD, 14));
        payerButton.setPreferredSize(new Dimension(100, 35));

        annulerButton = new JButton("Annuler");
        annulerButton.setBackground(new Color(200, 50, 50));
        annulerButton.setForeground(Color.WHITE);
        annulerButton.setFont(new Font("Arial", Font.BOLD, 14));
        annulerButton.setPreferredSize(new Dimension(100, 35));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Titre
        JLabel titreLabel = new JLabel("FITPLUS+ Paiement", JLabel.CENTER);
        titreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titreLabel.setForeground(new Color(0, 102, 204));
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titreLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Informations de l'abonnement
        nomAbonnementLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nomAbonnementLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        montantLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(montantLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Label d'information
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Panel pour le moyen de paiement
        JPanel moyenPaiementPanel = new JPanel(new FlowLayout());
        moyenPaiementPanel.setBackground(Color.WHITE);
        JLabel moyenPaiementLabelText = new JLabel("Moyen de paiement:");
        moyenPaiementPanel.add(moyenPaiementLabelText);
        moyenPaiementPanel.add(moyenPaiementCombo);
        mainPanel.add(moyenPaiementPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(payerButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(annulerButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        payerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                effectuerPaiement();
            }
        });

        annulerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void validateSouscription() {
        // Vérifier si la souscription est possible
        ResultatValidation validation = abonnementUtil.peutSouscrire(membre);

        if (!validation.isAutorise() && !validation.isRenouvellementProche()) {
            // Souscription non autorisée
            infoLabel.setText("<html><div style='text-align: center; color: red;'>" +
                             validation.getMessage() + "</div></html>");
            payerButton.setEnabled(false);
            payerButton.setText("Non autorisé");
        } else if (validation.isRenouvellementProche()) {
            // Renouvellement autorisé
            infoLabel.setText("<html><div style='text-align: center; color: orange;'>" +
                             validation.getMessage() + "<br/>Renouvellement possible</div></html>");
            payerButton.setText("Renouveler");
        } else {
            // Souscription normale
            infoLabel.setText("<html><div style='text-align: center; color: green;'>" +
                             validation.getMessage() + "</div></html>");
        }
    }

    private void loadMoyensPaiement() {
        try {
            List<MoyenDePaiement> moyensPaiement = abonnementUtil.getMoyensPaiementDisponibles();
            for (MoyenDePaiement moyen : moyensPaiement) {
                moyenPaiementCombo.addItem(moyen);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des moyens de paiement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void effectuerPaiement() {
        try {
            // Vérifier qu'un moyen de paiement est sélectionné
            MoyenDePaiement moyenSelectionne = (MoyenDePaiement) moyenPaiementCombo.getSelectedItem();
            if (moyenSelectionne == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un moyen de paiement.",
                    "Attention", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Valider à nouveau la souscription
            ResultatValidation validation = abonnementUtil.peutSouscrire(membre);

            // Demander confirmation
            if (!abonnementUtil.confirmerSouscription(this, typeAbonnement, validation)) {
                return;
            }

            // Désactiver les boutons pendant le traitement
            payerButton.setEnabled(false);
            annulerButton.setEnabled(false);
            payerButton.setText("Traitement...");

            // Effectuer la souscription via l'utilitaire
            ResultatSouscription resultat = abonnementUtil.souscrireAbonnement(membre, typeAbonnement, moyenSelectionne);

            // Afficher le résultat
            abonnementUtil.afficherResultatSouscription(this, resultat);

            if (resultat.isSucces()) {
                // Fermer la fenêtre en cas de succès
                dispose();
            } else {
                // Réactiver les boutons en cas d'erreur
                payerButton.setEnabled(true);
                annulerButton.setEnabled(true);
                payerButton.setText("Payer");
            }

        } catch (Exception e) {
            // Réactiver les boutons en cas d'erreur
            payerButton.setEnabled(true);
            annulerButton.setEnabled(true);
            payerButton.setText("Payer");

            JOptionPane.showMessageDialog(this,
                "Erreur inattendue: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();
        }
    }

    // Méthode statique pour afficher la fenêtre de paiement
    public static void afficherPaiement(Frame parent, TypeAbonnement typeAbonnement, Membre membre) {
        SwingUtilities.invokeLater(() -> {
            PaiementPanel panel = new PaiementPanel(parent, typeAbonnement, membre);
            panel.setVisible(true);
        });
    }
}
