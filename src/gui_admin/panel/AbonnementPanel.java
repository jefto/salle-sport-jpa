package gui_admin.panel;

import entite.Abonnement;
import entite.TypeAbonnement;
import entite.Paiement;
import entite.Membre;
import service.AbonnementService;
import service.TypeAbonnementService;
import service.PaiementService;
import service.MembreService;
import gui_util.StyleUtil;
import gui_client.util.AbonnementSouscription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AbonnementPanel extends JPanel implements CrudOperationsInterface {

    private List<Abonnement> abonnements;
    private Abonnement abonnementSelectionne;
    private AbonnementService abonnementService;
    private TypeAbonnementService typeAbonnementService;
    private PaiementService paiementService;
    private MembreService membreService;
    private AbonnementSouscription abonnementUtil;
    private DefaultTableModel tableModel;
    private JTable table;

    public AbonnementPanel() {
        this.setLayout(new BorderLayout());
        this.abonnementService = new AbonnementService();
        this.typeAbonnementService = new TypeAbonnementService();
        this.paiementService = new PaiementService();
        this.membreService = new MembreService();
        this.abonnementUtil = AbonnementSouscription.getInstance();

        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date début", "Date fin", "Type", "Membre", "Paiement", "Statut"};

        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(120); // Colonne Statut

        // Styliser l'en-tête
        StyleUtil.styliserTableHeader(table);

        // Renderer personnalisé pour la colonne Statut avec couleurs
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String statut = value.toString();
                    if (statut.contains("Actif")) {
                        if (statut.contains("expire bientôt")) {
                            c.setForeground(new Color(255, 165, 0)); // Orange
                        } else {
                            c.setForeground(new Color(46, 204, 113)); // Vert
                        }
                    } else if (statut.equals("Expiré")) {
                        c.setForeground(new Color(231, 76, 60)); // Rouge
                    } else {
                        c.setForeground(Color.GRAY);
                    }
                } else {
                    c.setForeground(Color.WHITE);
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Ajouter le listener pour les cases à cocher
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                    int row = e.getFirstRow();
                    boolean isChecked = (Boolean) tableModel.getValueAt(row, 0);
                    
                    if (isChecked) {
                        // Décocher les autres cases
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (i != row) {
                                tableModel.setValueAt(false, i, 0);
                            }
                        }
                        abonnementSelectionne = abonnements.get(row);
                    } else {
                        abonnementSelectionne = null;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        // Vider le modèle
        tableModel.setRowCount(0);
        
        // Charger les données
        abonnements = abonnementService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Abonnement abonnement : abonnements) {
            // Déterminer le statut de l'abonnement
            String statut = determinerStatutAbonnement(abonnement);

            tableModel.addRow(new Object[]{
                false,
                abonnement.getId(),
                abonnement.getDateDebut() != null ? abonnement.getDateDebut().format(formatter) : "Non définie",
                abonnement.getDateFin() != null ? abonnement.getDateFin().format(formatter) : "Non définie",
                abonnement.getTypeAbonnement() != null ? abonnement.getTypeAbonnement().getLibelle() : "Aucun",
                abonnement.getMembre() != null ? "Membre #" + abonnement.getMembre().getId() : "Aucun",
                abonnement.getPaiement() != null ? "Paiement #" + abonnement.getPaiement().getId() : "Aucun",
                statut
            });
        }
    }

    /**
     * Détermine le statut d'un abonnement en utilisant la classe utilitaire
     * @param abonnement L'abonnement à analyser
     * @return Le statut formaté
     */
    private String determinerStatutAbonnement(Abonnement abonnement) {
        if (abonnement == null || abonnement.getDateFin() == null) {
            return "Indéterminé";
        }

        try {
            if (abonnementUtil.isAbonnementActif(abonnement)) {
                long joursRestants = abonnementUtil.getJoursRestants(abonnement);
                if (joursRestants <= 7) {
                    return "Actif (expire bientôt)";
                } else {
                    return "Actif (" + joursRestants + " jours)";
                }
            } else {
                return "Expiré";
            }
        } catch (Exception e) {
            return "Erreur";
        }
    }

    @Override
    public void ajouter() {
        // Récupérer les listes nécessaires
        List<TypeAbonnement> typesAbonnement = typeAbonnementService.listerTous();
        List<Membre> membres = membreService.listerTous();
        List<Paiement> paiements = paiementService.listerTous();
        
        if (typesAbonnement.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun type d'abonnement disponible. Veuillez d'abord créer un type d'abonnement.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (membres.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun membre disponible. Veuillez d'abord créer un membre.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField dateDebutField = new JTextField();
        JTextField dateFinField = new JTextField();
        JComboBox<TypeAbonnement> typeCombo = new JComboBox<>(typesAbonnement.toArray(new TypeAbonnement[0]));
        JComboBox<Membre> membreCombo = new JComboBox<>(membres.toArray(new Membre[0]));
        JComboBox<Paiement> paiementCombo = new JComboBox<>();
        
        // Ajouter option "Aucun" pour paiement
        paiementCombo.addItem(null);
        for (Paiement p : paiements) {
            paiementCombo.addItem(p);
        }
        
        // Pré-remplir les dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        dateDebutField.setText(now.format(formatter));
        dateFinField.setText(now.plusMonths(1).format(formatter)); // Par défaut 1 mois
        
        formulaire.add(new JLabel("Date début (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateDebutField);
        formulaire.add(new JLabel("Date fin (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateFinField);
        formulaire.add(new JLabel("Type d'abonnement :"));
        formulaire.add(typeCombo);
        formulaire.add(new JLabel("Membre :"));
        formulaire.add(membreCombo);
        formulaire.add(new JLabel("Paiement (optionnel) :"));
        formulaire.add(paiementCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouvel abonnement", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String dateDebutText = dateDebutField.getText().trim();
            String dateFinText = dateFinField.getText().trim();
            
            if (!dateDebutText.isEmpty() && !dateFinText.isEmpty()) {
                try {
                    LocalDateTime dateDebut = LocalDateTime.parse(dateDebutText, formatter);
                    LocalDateTime dateFin = LocalDateTime.parse(dateFinText, formatter);
                    TypeAbonnement typeSelectionne = (TypeAbonnement) typeCombo.getSelectedItem();
                    Membre membreSelectionne = (Membre) membreCombo.getSelectedItem();
                    Paiement paiementSelectionne = (Paiement) paiementCombo.getSelectedItem();
                    
                    if (dateFin.isAfter(dateDebut)) {
                        Abonnement nouvelAbonnement = new Abonnement(dateDebut, dateFin, typeSelectionne, paiementSelectionne, membreSelectionne);
                        abonnementService.ajouter(nouvelAbonnement);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Abonnement ajouté avec succès!", 
                            "Ajout réussi", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "La date de fin doit être postérieure à la date de début!", 
                            "Dates invalides", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage() + 
                        "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Les dates de début et de fin sont obligatoires!", 
                    "Champs manquants", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (abonnementSelectionne != null) {
            // Récupérer les listes nécessaires
            List<TypeAbonnement> typesAbonnement = typeAbonnementService.listerTous();
            List<Membre> membres = membreService.listerTous();
            List<Paiement> paiements = paiementService.listerTous();
            
            if (typesAbonnement.isEmpty() || membres.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Données de référence manquantes.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField dateDebutField = new JTextField();
            JTextField dateFinField = new JTextField();
            JComboBox<TypeAbonnement> typeCombo = new JComboBox<>(typesAbonnement.toArray(new TypeAbonnement[0]));
            JComboBox<Membre> membreCombo = new JComboBox<>(membres.toArray(new Membre[0]));
            JComboBox<Paiement> paiementCombo = new JComboBox<>();
            
            // Ajouter option "Aucun" pour paiement
            paiementCombo.addItem(null);
            for (Paiement p : paiements) {
                paiementCombo.addItem(p);
            }
            
            // Formatter et afficher les données existantes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (abonnementSelectionne.getDateDebut() != null) {
                dateDebutField.setText(abonnementSelectionne.getDateDebut().format(formatter));
            }
            if (abonnementSelectionne.getDateFin() != null) {
                dateFinField.setText(abonnementSelectionne.getDateFin().format(formatter));
            }
            
            // Sélectionner les valeurs actuelles
            if (abonnementSelectionne.getTypeAbonnement() != null) {
                typeCombo.setSelectedItem(abonnementSelectionne.getTypeAbonnement());
            }
            if (abonnementSelectionne.getMembre() != null) {
                membreCombo.setSelectedItem(abonnementSelectionne.getMembre());
            }
            if (abonnementSelectionne.getPaiement() != null) {
                paiementCombo.setSelectedItem(abonnementSelectionne.getPaiement());
            }
            
            formulaire.add(new JLabel("Date début (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateDebutField);
            formulaire.add(new JLabel("Date fin (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateFinField);
            formulaire.add(new JLabel("Type d'abonnement :"));
            formulaire.add(typeCombo);
            formulaire.add(new JLabel("Membre :"));
            formulaire.add(membreCombo);
            formulaire.add(new JLabel("Paiement (optionnel) :"));
            formulaire.add(paiementCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier l'abonnement: " + abonnementSelectionne.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String dateDebutText = dateDebutField.getText().trim();
                String dateFinText = dateFinField.getText().trim();
                
                if (!dateDebutText.isEmpty() && !dateFinText.isEmpty()) {
                    try {
                        LocalDateTime dateDebut = LocalDateTime.parse(dateDebutText, formatter);
                        LocalDateTime dateFin = LocalDateTime.parse(dateFinText, formatter);
                        TypeAbonnement typeSelectionne = (TypeAbonnement) typeCombo.getSelectedItem();
                        Membre membreSelectionne = (Membre) membreCombo.getSelectedItem();
                        Paiement paiementSelectionne = (Paiement) paiementCombo.getSelectedItem();
                        
                        if (dateFin.isAfter(dateDebut)) {
                            // Mettre à jour les données de l'abonnement sélectionné
                            abonnementSelectionne.setDateDebut(dateDebut);
                            abonnementSelectionne.setDateFin(dateFin);
                            abonnementSelectionne.setTypeAbonnement(typeSelectionne);
                            abonnementSelectionne.setMembre(membreSelectionne);
                            abonnementSelectionne.setPaiement(paiementSelectionne);
                            
                            abonnementService.modifier(abonnementSelectionne);
                            
                            JOptionPane.showMessageDialog(this, 
                                "Abonnement modifié avec succès!", 
                                "Modification réussie", 
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Recharger les données
                            loadData();
                            
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "La date de fin doit être postérieure à la date de début!", 
                                "Dates invalides", 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage() + 
                            "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Les dates de début et de fin sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (abonnementSelectionne != null) {
            try {
                abonnementService.supprimer(abonnementSelectionne);
                JOptionPane.showMessageDialog(this, 
                    "Abonnement supprimé avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                abonnementSelectionne = null;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return abonnementSelectionne != null;
    }

    public Abonnement getAbonnementSelectionne() {
        return abonnementSelectionne;
    }
}