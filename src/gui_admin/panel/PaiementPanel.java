package gui_admin.panel;

import entite.Paiement;
import entite.MoyenDePaiement;
import service.PaiementService;
import service.MoyenDePaiementService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaiementPanel extends JPanel implements CrudOperationsInterface {

    private List<Paiement> paiements;
    private Paiement paiementSelectionne;
    private PaiementService paiementService;
    private MoyenDePaiementService moyenDePaiementService;
    private DefaultTableModel tableModel;
    private JTable table;

    public PaiementPanel() {
        this.setLayout(new BorderLayout());
        this.paiementService = new PaiementService();
        this.moyenDePaiementService = new MoyenDePaiementService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date de paiement", "Montant", "Moyen de Paiement"};

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
                        paiementSelectionne = paiements.get(row);
                    } else {
                        paiementSelectionne = null;
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
        paiements = paiementService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Paiement paiement : paiements) {
            tableModel.addRow(new Object[]{
                false,
                paiement.getId(),
                paiement.getDateDePaiement() != null ? paiement.getDateDePaiement().format(formatter) : "Non définie",
                paiement.getMontant() + " €",
                paiement.getMoyenDePaiement() != null ? paiement.getMoyenDePaiement().getLibelle() : "Aucun"
            });
        }
    }

    @Override
    public void ajouter() {
        // Récupérer la liste des moyens de paiement
        List<MoyenDePaiement> moyensPaiement = moyenDePaiementService.listerTous();
        
        if (moyensPaiement.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun moyen de paiement disponible. Veuillez d'abord créer un moyen de paiement.", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField dateField = new JTextField();
        JTextField montantField = new JTextField();
        JComboBox<MoyenDePaiement> moyenCombo = new JComboBox<>(moyensPaiement.toArray(new MoyenDePaiement[0]));
        
        // Pré-remplir la date avec la date actuelle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateField.setText(LocalDateTime.now().format(formatter));
        
        formulaire.add(new JLabel("Date de paiement (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateField);
        formulaire.add(new JLabel("Montant (FCFA) :"));
        formulaire.add(montantField);
        formulaire.add(new JLabel("Moyen de paiement :"));
        formulaire.add(moyenCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouveau paiement", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String dateText = dateField.getText().trim();
            String montantText = montantField.getText().trim();
            
            if (!dateText.isEmpty() && !montantText.isEmpty()) {
                try {
                    // Parser la date
                    LocalDateTime datePaiement = LocalDateTime.parse(dateText, formatter);
                    int montant = Integer.parseInt(montantText);
                    MoyenDePaiement moyenSelectionne = (MoyenDePaiement) moyenCombo.getSelectedItem();
                    
                    if (montant > 0) {
                        Paiement nouveauPaiement = new Paiement(datePaiement, montant, moyenSelectionne);
                        paiementService.ajouter(nouveauPaiement);
                    
                        JOptionPane.showMessageDialog(this, 
                            "Paiement ajouté avec succès!", 
                            "Ajout réussi", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                        // Recharger les données
                        loadData();
                    
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Le montant doit être supérieur à 0!", 
                            "Montant invalide", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Veuillez saisir un montant valide (nombre entier)!", 
                        "Format invalide", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage() + 
                        "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Tous les champs sont obligatoires!", 
                    "Champs manquants", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (paiementSelectionne != null) {
            // Récupérer la liste des moyens de paiement
            List<MoyenDePaiement> moyensPaiement = moyenDePaiementService.listerTous();
        
            if (moyensPaiement.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Aucun moyen de paiement disponible.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField dateField = new JTextField();
            JTextField montantField = new JTextField(String.valueOf(paiementSelectionne.getMontant()));
            JComboBox<MoyenDePaiement> moyenCombo = new JComboBox<>(moyensPaiement.toArray(new MoyenDePaiement[0]));
        
            // Formatter et afficher la date existante
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (paiementSelectionne.getDateDePaiement() != null) {
                dateField.setText(paiementSelectionne.getDateDePaiement().format(formatter));
            }
        
            // Sélectionner le moyen de paiement actuel
            if (paiementSelectionne.getMoyenDePaiement() != null) {
                moyenCombo.setSelectedItem(paiementSelectionne.getMoyenDePaiement());
            }
        
            formulaire.add(new JLabel("Date de paiement (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateField);
            formulaire.add(new JLabel("Montant (FCFA) :"));
            formulaire.add(montantField);
            formulaire.add(new JLabel("Moyen de paiement :"));
            formulaire.add(moyenCombo);
        
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier le paiement: " + paiementSelectionne.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
        
            if (result == JOptionPane.OK_OPTION) {
                String dateText = dateField.getText().trim();
                String montantText = montantField.getText().trim();
            
                if (!dateText.isEmpty() && !montantText.isEmpty()) {
                    try {
                        // Parser la date
                        LocalDateTime datePaiement = LocalDateTime.parse(dateText, formatter);
                        int montant = Integer.parseInt(montantText);
                        MoyenDePaiement moyenSelectionne = (MoyenDePaiement) moyenCombo.getSelectedItem();
                    
                        if (montant > 0) {
                            // Mettre à jour les données du paiement sélectionné
                            paiementSelectionne.setDateDePaiement(datePaiement);
                            paiementSelectionne.setMontant(montant);
                            paiementSelectionne.setMoyenDePaiement(moyenSelectionne);
                        
                            paiementService.modifier(paiementSelectionne);
                        
                            JOptionPane.showMessageDialog(this, 
                                "Paiement modifié avec succès!", 
                                "Modification réussie", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                            // Recharger les données
                            loadData();
                        
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Le montant doit être supérieur à 0!", 
                                "Montant invalide", 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Veuillez saisir un montant valide (nombre entier)!", 
                            "Format invalide", 
                            JOptionPane.WARNING_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage() + 
                            "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Tous les champs sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (paiementSelectionne != null) {
            try {
                paiementService.supprimer(paiementSelectionne);
                JOptionPane.showMessageDialog(this, 
                    "Paiement supprimé avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                paiementSelectionne = null;
                
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
        return paiementSelectionne != null;
    }

    public Paiement getPaiementSelectionne() {
        return paiementSelectionne;
    }
}