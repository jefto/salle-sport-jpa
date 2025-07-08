package gui_admin.panel;

import entite.Equipement;
import entite.Salle;
import service.EquipementService;
import service.SalleService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.List;

public class EquipementPanel extends JPanel implements CrudOperationsInterface {

    private List<Equipement> equipements;
    private Equipement equipementSelectionne;
    private EquipementService equipementService;
    private SalleService salleService;
    private DefaultTableModel tableModel;
    private JTable table;

    public EquipementPanel() {
        this.setLayout(new BorderLayout());
        this.equipementService = new EquipementService();
        this.salleService = new SalleService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Libellé", "Description", "Salle"};

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
                        equipementSelectionne = equipements.get(row);
                    } else {
                        equipementSelectionne = null;
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
        equipements = equipementService.listerTous();
        
        for (Equipement equipement : equipements) {
            tableModel.addRow(new Object[]{
                false,
                equipement.getId(),
                equipement.getLibelle(),
                equipement.getDescription() != null ? equipement.getDescription() : "Aucune description",
                equipement.getSalle() != null ? equipement.getSalle().getLibelle() : "Aucune salle"
            });
        }
    }

    @Override
    public void ajouter() {
        // Récupérer la liste des salles
        List<Salle> salles = salleService.listerTous();
        
        if (salles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucune salle disponible. Veuillez d'abord créer une salle.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField libelleField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JComboBox<Salle> salleCombo = new JComboBox<>(salles.toArray(new Salle[0]));
        
        // Améliorer l'affichage de la description
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        formulaire.add(new JLabel("Libellé :"));
        formulaire.add(libelleField);
        formulaire.add(new JLabel("Description :"));
        formulaire.add(descriptionScrollPane);
        formulaire.add(new JLabel("Salle :"));
        formulaire.add(salleCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouvel équipement", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String libelle = libelleField.getText().trim();
            String description = descriptionArea.getText().trim();
            Salle salleSelectionnee = (Salle) salleCombo.getSelectedItem();
            
            if (!libelle.isEmpty() && salleSelectionnee != null) {
                try {
                    // Vérifier que le libellé n'est pas trop long (limite de 100 caractères selon l'entité)
                    if (libelle.length() > 100) {
                        JOptionPane.showMessageDialog(this, 
                            "Le libellé ne peut pas dépasser 100 caractères!", 
                            "Libellé trop long", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Si description vide, on la met à null
                    if (description.isEmpty()) {
                        description = null;
                    }
                    
                    Equipement nouvelEquipement = new Equipement(libelle, description, salleSelectionnee);
                    equipementService.ajouter(nouvelEquipement);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Équipement ajouté avec succès!", 
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
                    "Le libellé et la salle sont obligatoires!", 
                    "Champs manquants", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (equipementSelectionne != null) {
            // Récupérer la liste des salles
            List<Salle> salles = salleService.listerTous();
            
            if (salles.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucune salle disponible.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField libelleField = new JTextField(equipementSelectionne.getLibelle());
            JTextArea descriptionArea = new JTextArea(3, 20);
            JComboBox<Salle> salleCombo = new JComboBox<>(salles.toArray(new Salle[0]));
            
            // Pré-remplir la description
            if (equipementSelectionne.getDescription() != null) {
                descriptionArea.setText(equipementSelectionne.getDescription());
            }
            
            // Améliorer l'affichage de la description
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
            descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            
            // Sélectionner la salle actuelle
            if (equipementSelectionne.getSalle() != null) {
                salleCombo.setSelectedItem(equipementSelectionne.getSalle());
            }
            
            formulaire.add(new JLabel("Libellé :"));
            formulaire.add(libelleField);
            formulaire.add(new JLabel("Description :"));
            formulaire.add(descriptionScrollPane);
            formulaire.add(new JLabel("Salle :"));
            formulaire.add(salleCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier l'équipement: " + equipementSelectionne.getLibelle(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String libelle = libelleField.getText().trim();
                String description = descriptionArea.getText().trim();
                Salle salleSelectionnee = (Salle) salleCombo.getSelectedItem();
                
                if (!libelle.isEmpty() && salleSelectionnee != null) {
                    try {
                        // Vérifier que le libellé n'est pas trop long (limite de 100 caractères selon l'entité)
                        if (libelle.length() > 100) {
                            JOptionPane.showMessageDialog(this, 
                                "Le libellé ne peut pas dépasser 100 caractères!", 
                                "Libellé trop long", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Si description vide, on la met à null
                        if (description.isEmpty()) {
                            description = null;
                        }
                        
                        // Mettre à jour les données de l'équipement sélectionné
                        equipementSelectionne.setLibelle(libelle);
                        equipementSelectionne.setDescription(description);
                        equipementSelectionne.setSalle(salleSelectionnee);
                        
                        equipementService.modifier(equipementSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Équipement modifié avec succès!", 
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
                        "Le libellé et la salle sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (equipementSelectionne != null) {
            try {
                equipementService.supprimer(equipementSelectionne);
                JOptionPane.showMessageDialog(this, 
                    "Équipement supprimé avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                equipementSelectionne = null;
                
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
        return equipementSelectionne != null;
    }

    public Equipement getEquipementSelectionne() {
        return equipementSelectionne;
    }
    
    /**
     * Méthode utilitaire pour changer la salle d'un équipement
     */
    public void changerSalle() {
        if (equipementSelectionne != null) {
            List<Salle> salles = salleService.listerTous();
            
            if (salles.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucune salle disponible.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Retirer la salle actuelle de la liste des options
            salles.removeIf(salle -> salle.equals(equipementSelectionne.getSalle()));
            
            if (salles.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucune autre salle disponible pour ce transfert.", 
                    "Pas d'alternatives", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Salle nouvelleSalle = (Salle) JOptionPane.showInputDialog(
                this,
                "Sélectionnez la nouvelle salle pour l'équipement: " + equipementSelectionne.getLibelle(),
                "Changer de salle",
                JOptionPane.QUESTION_MESSAGE,
                null,
                salles.toArray(new Salle[0]),
                salles.get(0)
            );
            
            if (nouvelleSalle != null) {
                try {
                    equipementSelectionne.setSalle(nouvelleSalle);
                    equipementService.modifier(equipementSelectionne);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Équipement transféré avec succès vers la salle: " + nouvelleSalle.getLibelle(), 
                        "Transfert réussi", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors du transfert: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}