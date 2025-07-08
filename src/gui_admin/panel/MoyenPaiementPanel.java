package gui_admin.panel;

import entite.MoyenDePaiement;
import service.MoyenDePaiementService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class MoyenPaiementPanel extends JPanel implements CrudOperationsInterface {

    private List<MoyenDePaiement> moyensPaiement;
    private MoyenDePaiement moyenPaiementSelectionne;
    private MoyenDePaiementService moyenPaiementService;
    private DefaultTableModel tableModel;
    private JTable table;
    
    // Moyens de paiement prédéfinis courants
    private static final String[] MOYENS_PREDEFINIS = {
        "Carte Bancaire", "Espèces", "Chèque", "Virement", 
        "PayPal", "Apple Pay", "Google Pay", "Carte de Crédit",
        "Prélèvement Automatique", "Mandat", "Bon Cadeau"
    };

    public MoyenPaiementPanel() {
        this.setLayout(new BorderLayout());
        this.moyenPaiementService = new MoyenDePaiementService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "Code", "Libellé", "Statut"};

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
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        
        // Styliser l'en-tête
        StyleUtil.styliserTableHeader(table);

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
                        moyenPaiementSelectionne = moyensPaiement.get(row);
                    } else {
                        moyenPaiementSelectionne = null;
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
        moyensPaiement = moyenPaiementService.listerTous();
        
        for (MoyenDePaiement moyen : moyensPaiement) {
            // Déterminer le statut basé sur des moyens prédéfinis
            String statut = determinerStatut(moyen.getLibelle());
            
            tableModel.addRow(new Object[]{
                false,
                moyen.getId(),
                moyen.getLibelle(),
                statut
            });
        }
    }
    
    /**
     * Détermine le statut d'un moyen de paiement
     */
    private String determinerStatut(String libelle) {
        if (libelle == null) return "Invalide";
        
        // Vérifier si c'est un moyen prédéfini
        boolean estPredefini = Arrays.stream(MOYENS_PREDEFINIS)
            .anyMatch(moyen -> moyen.equalsIgnoreCase(libelle.trim()));
        
        if (estPredefini) {
            return "Standard";
        } else if (libelle.length() > 50) {
            return "Nom long";
        } else {
            return "Personnalisé";
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie avec des options
        JPanel formulaire = new JPanel(new BorderLayout(10, 10));
        
        // Section choix du type
        JPanel choixPanel = new JPanel(new FlowLayout());
        JRadioButton predefiniBouton = new JRadioButton("Moyen prédéfini", true);
        JRadioButton personnaliseBouton = new JRadioButton("Moyen personnalisé");
        ButtonGroup groupe = new ButtonGroup();
        groupe.add(predefiniBouton);
        groupe.add(personnaliseBouton);
        
        choixPanel.add(new JLabel("Type : "));
        choixPanel.add(predefiniBouton);
        choixPanel.add(personnaliseBouton);
        
        // Section saisie
        JPanel saisiePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JComboBox<String> predefinisCombo = new JComboBox<>(MOYENS_PREDEFINIS);
        JTextField libelleField = new JTextField();
        
        saisiePanel.add(new JLabel("Moyens prédéfinis :"));
        saisiePanel.add(predefinisCombo);
        saisiePanel.add(new JLabel("Libellé personnalisé :"));
        saisiePanel.add(libelleField);
        
        formulaire.add(choixPanel, BorderLayout.NORTH);
        formulaire.add(saisiePanel, BorderLayout.CENTER);
        
        // Gérer l'activation/désactivation des champs
        predefiniBouton.addActionListener(e -> {
            predefinisCombo.setEnabled(true);
            libelleField.setEnabled(false);
        });
        
        personnaliseBouton.addActionListener(e -> {
            predefinisCombo.setEnabled(false);
            libelleField.setEnabled(true);
        });
        
        // État initial
        predefinisCombo.setEnabled(true);
        libelleField.setEnabled(false);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouveau moyen de paiement", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String libelle = "";
            
            if (predefiniBouton.isSelected()) {
                libelle = (String) predefinisCombo.getSelectedItem();
            } else {
                libelle = libelleField.getText().trim();
            }
            
            if (!libelle.isEmpty()) {
                try {
                    // Vérifier que le libellé n'est pas trop long (limite de 100 caractères selon l'entité)
                    if (libelle.length() > 100) {
                        JOptionPane.showMessageDialog(this, 
                            "Le libellé ne peut pas dépasser 100 caractères!", 
                            "Libellé trop long", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Vérifier l'unicité du libellé
                    if (libelleExiste(libelle)) {
                        JOptionPane.showMessageDialog(this, 
                            "Ce moyen de paiement existe déjà!", 
                            "Doublon détecté", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    MoyenDePaiement nouveauMoyen = new MoyenDePaiement(libelle);
                    moyenPaiementService.ajouter(nouveauMoyen);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Moyen de paiement ajouté avec succès!\nLibellé: " + libelle, 
                        "Ajout réussi", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Le libellé est obligatoire!", 
                    "Champ manquant", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (moyenPaiementSelectionne != null) {
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField libelleField = new JTextField(moyenPaiementSelectionne.getLibelle());
            JLabel statutLabel = new JLabel(determinerStatut(moyenPaiementSelectionne.getLibelle()));
            
            // Styliser le label de statut
            statutLabel.setOpaque(true);
            statutLabel.setHorizontalAlignment(SwingConstants.CENTER);
            switch (determinerStatut(moyenPaiementSelectionne.getLibelle())) {
                case "Standard":
                    statutLabel.setBackground(new Color(144, 238, 144)); // Vert clair
                    break;
                case "Personnalisé":
                    statutLabel.setBackground(new Color(173, 216, 230)); // Bleu clair
                    break;
                default:
                    statutLabel.setBackground(new Color(255, 218, 185)); // Orange clair
            }
            
            formulaire.add(new JLabel("Libellé :"));
            formulaire.add(libelleField);
            formulaire.add(new JLabel("Statut actuel :"));
            formulaire.add(statutLabel);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier le moyen de paiement: " + moyenPaiementSelectionne.getLibelle(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String libelle = libelleField.getText().trim();
                
                if (!libelle.isEmpty()) {
                    try {
                        // Vérifier que le libellé n'est pas trop long (limite de 100 caractères selon l'entité)
                        if (libelle.length() > 100) {
                            JOptionPane.showMessageDialog(this, 
                                "Le libellé ne peut pas dépasser 100 caractères!", 
                                "Libellé trop long", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Vérifier l'unicité du libellé (sauf si c'est le même)
                        if (!libelle.equals(moyenPaiementSelectionne.getLibelle()) && libelleExiste(libelle)) {
                            JOptionPane.showMessageDialog(this, 
                                "Ce moyen de paiement existe déjà!", 
                                "Doublon détecté", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Mettre à jour les données du moyen de paiement sélectionné
                        moyenPaiementSelectionne.setLibelle(libelle);
                        
                        moyenPaiementService.modifier(moyenPaiementSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Moyen de paiement modifié avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Le libellé est obligatoire!", 
                        "Champ manquant", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (moyenPaiementSelectionne != null) {
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer le moyen de paiement :\n\"" + 
                moyenPaiementSelectionne.getLibelle() + "\" ?", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    moyenPaiementService.supprimer(moyenPaiementSelectionne);
                    JOptionPane.showMessageDialog(this, 
                        "Moyen de paiement supprimé avec succès!", 
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    moyenPaiementSelectionne = null;
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return moyenPaiementSelectionne != null;
    }

    public MoyenDePaiement getMoyenPaiementSelectionne() {
        return moyenPaiementSelectionne;
    }
    
    /**
     * Vérifie si un libellé existe déjà
     * @param libelle Le libellé à vérifier
     * @return true s'il existe, false sinon
     */
    private boolean libelleExiste(String libelle) {
        return moyensPaiement.stream()
            .anyMatch(moyen -> moyen.getLibelle().equalsIgnoreCase(libelle.trim()));
    }
    
    /**
     * Méthode utilitaire pour ajouter rapidement des moyens de paiement standards
     */
    public void ajouterMoyensStandards() {
        StringBuilder resultats = new StringBuilder();
        int ajouts = 0;
        int doublons = 0;
        
        for (String moyenStandard : MOYENS_PREDEFINIS) {
            if (!libelleExiste(moyenStandard)) {
                try {
                    MoyenDePaiement nouveau = new MoyenDePaiement(moyenStandard);
                    moyenPaiementService.ajouter(nouveau);
                    ajouts++;
                } catch (Exception e) {
                    resultats.append("Erreur pour '").append(moyenStandard).append("': ").append(e.getMessage()).append("\n");
                }
            } else {
                doublons++;
            }
        }
        
        // Recharger les données
        loadData();
        
        // Afficher le résumé
        StringBuilder message = new StringBuilder();
        message.append("=== AJOUT DES MOYENS STANDARDS ===\n\n");
        message.append("Moyens ajoutés: ").append(ajouts).append("\n");
        message.append("Doublons ignorés: ").append(doublons).append("\n");
        
        if (resultats.length() > 0) {
            message.append("\nErreurs:\n").append(resultats);
        }
        
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Résultats de l'ajout", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Méthode utilitaire pour dupliquer un moyen de paiement
     */
    public void dupliquerMoyen() {
        if (moyenPaiementSelectionne != null) {
            String nouveauLibelle = JOptionPane.showInputDialog(this, 
                "Entrez le nouveau libellé pour la copie :", 
                moyenPaiementSelectionne.getLibelle() + " (Copie)");
            
            if (nouveauLibelle != null && !nouveauLibelle.trim().isEmpty()) {
                try {
                    // Vérifier la longueur
                    if (nouveauLibelle.length() > 100) {
                        JOptionPane.showMessageDialog(this, 
                            "Le libellé ne peut pas dépasser 100 caractères!", 
                            "Libellé trop long", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Vérifier l'unicité
                    if (libelleExiste(nouveauLibelle)) {
                        JOptionPane.showMessageDialog(this, 
                            "Ce moyen de paiement existe déjà!", 
                            "Doublon détecté", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    MoyenDePaiement copie = new MoyenDePaiement(nouveauLibelle.trim());
                    moyenPaiementService.ajouter(copie);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Moyen de paiement dupliqué avec succès!", 
                        "Duplication réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la duplication: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Affiche les statistiques des moyens de paiement
     */
    public void afficherStatistiques() {
        int total = moyensPaiement.size();
        int standards = 0;
        int personnalises = 0;
        int nomsLongs = 0;
        int invalides = 0;
        
        for (MoyenDePaiement moyen : moyensPaiement) {
            String statut = determinerStatut(moyen.getLibelle());
            switch (statut) {
                case "Standard" -> standards++;
                case "Personnalisé" -> personnalises++;
                case "Nom long" -> nomsLongs++;
                default -> invalides++;
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTIQUES DES MOYENS DE PAIEMENT ===\n\n");
        stats.append("Total des moyens: ").append(total).append("\n");
        stats.append("Moyens standards: ").append(standards).append("\n");
        stats.append("Moyens personnalisés: ").append(personnalises).append("\n");
        stats.append("Moyens à nom long: ").append(nomsLongs).append("\n");
        stats.append("Moyens invalides: ").append(invalides).append("\n\n");
        
        if (total > 0) {
            double pourcentageStandards = (double) standards / total * 100;
            stats.append("Taux de moyens standards: ").append(String.format("%.1f%%", pourcentageStandards)).append("\n");
        }
        
        stats.append("\n=== MOYENS STANDARDS DISPONIBLES ===\n");
        for (String moyenStandard : MOYENS_PREDEFINIS) {
            boolean existe = libelleExiste(moyenStandard);
            stats.append("• ").append(moyenStandard).append(existe ? " ✓" : " ✗").append("\n");
        }
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Statistiques des moyens de paiement", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}