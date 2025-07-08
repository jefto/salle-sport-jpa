package gui_admin.panel;

import entite.Salle;
import service.SalleService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.List;

public class SallePanel extends JPanel implements CrudOperationsInterface {

    private List<Salle> salles;
    private Salle salleSelectionnee;
    private SalleService salleService;
    private DefaultTableModel tableModel;
    private JTable table;

    public SallePanel() {
        this.setLayout(new BorderLayout());
        this.salleService = new SalleService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Libellé", "Description"};

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
                        salleSelectionnee = salles.get(row);
                    } else {
                        salleSelectionnee = null;
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
        salles = salleService.listerTous();
        
        for (Salle salle : salles) {
            tableModel.addRow(new Object[]{
                false,
                salle.getId(),
                salle.getLibelle(),
                salle.getDescription()
            });
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField libelleField = new JTextField();
        JTextField descriptionField = new JTextField();
        
        formulaire.add(new JLabel("Libellé :"));
        formulaire.add(libelleField);
        formulaire.add(new JLabel("Description :"));
        formulaire.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter une nouvelle salle", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String libelle = libelleField.getText().trim();
            String description = descriptionField.getText().trim();
            
            if (!libelle.isEmpty()) {
                try {
                    Salle nouvelleSalle = new Salle(libelle, description);
                    salleService.ajouter(nouvelleSalle);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Salle ajoutée avec succès!", 
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
    if (salleSelectionnee != null) {
        // Pré-remplir le formulaire avec les données existantes
        JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField libelleField = new JTextField(salleSelectionnee.getLibelle());
        JTextField descriptionField = new JTextField(salleSelectionnee.getDescription());
        
        formulaire.add(new JLabel("Libellé :"));
        formulaire.add(libelleField);
        formulaire.add(new JLabel("Description :"));
        formulaire.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Modifier la salle: " + salleSelectionnee.getLibelle(), 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String libelle = libelleField.getText().trim();
            String description = descriptionField.getText().trim();
            
            if (!libelle.isEmpty()) {
                try {
                    // Mettre à jour les données de la salle sélectionnée
                    salleSelectionnee.setLibelle(libelle);
                    salleSelectionnee.setDescription(description);
                    
                    salleService.modifier(salleSelectionnee);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Salle modifiée avec succès!", 
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
        if (salleSelectionnee != null) {
            try {
                salleService.supprimer(salleSelectionnee);
                JOptionPane.showMessageDialog(this, 
                    "Salle supprimée avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                salleSelectionnee = null;
                
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
        return salleSelectionnee != null;
    }

    public Salle getSalleSelectionnee() {
        return salleSelectionnee;
    }
}