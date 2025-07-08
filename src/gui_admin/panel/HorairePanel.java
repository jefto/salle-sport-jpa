package gui_admin.panel;

import entite.Horaire;
import service.HoraireService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;

public class HorairePanel extends JPanel implements CrudOperationsInterface {

    private List<Horaire> horaires;
    private Horaire horaireSelectionne;
    private HoraireService horaireService;
    private DefaultTableModel tableModel;
    private JTable table;

    public HorairePanel() {
        this.setLayout(new BorderLayout());
        this.horaireService = new HoraireService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date début", "Heure début", "Date fin", "Heure fin", "Durée"};

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
                        horaireSelectionne = horaires.get(row);
                    } else {
                        horaireSelectionne = null;
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
        horaires = horaireService.listerTous();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Horaire horaire : horaires) {
            String dateDebut = horaire.getDebut() != null ? horaire.getDebut().format(dateFormatter) : "Non définie";
            String heureDebut = horaire.getDebut() != null ? horaire.getDebut().format(timeFormatter) : "Non définie";
            String dateFin = horaire.getFin() != null ? horaire.getFin().format(dateFormatter) : "Non définie";
            String heureFin = horaire.getFin() != null ? horaire.getFin().format(timeFormatter) : "Non définie";
            
            String duree = "Non calculable";
            if (horaire.getDebut() != null && horaire.getFin() != null) {
                Duration duration = Duration.between(horaire.getDebut(), horaire.getFin());
                long heures = duration.toHours();
                long minutes = duration.toMinutesPart();
                duree = String.format("%02dh%02dm", heures, minutes);
            }
            
            tableModel.addRow(new Object[]{
                false,
                horaire.getId(),
                dateDebut,
                heureDebut,
                dateFin,
                heureFin,
                duree
            });
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField debutField = new JTextField();
        JTextField finField = new JTextField();
        
        // Pré-remplir avec des exemples
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime maintenant = LocalDateTime.now();
        debutField.setText(maintenant.format(formatter));
        finField.setText(maintenant.plusHours(1).format(formatter)); // Par défaut 1 heure plus tard
        
        formulaire.add(new JLabel("Date/Heure début (dd/MM/yyyy HH:mm) :"));
        formulaire.add(debutField);
        formulaire.add(new JLabel("Date/Heure fin (dd/MM/yyyy HH:mm) :"));
        formulaire.add(finField);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouvel horaire", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String debutText = debutField.getText().trim();
            String finText = finField.getText().trim();
            
            if (!debutText.isEmpty() && !finText.isEmpty()) {
                try {
                    LocalDateTime debut = LocalDateTime.parse(debutText, formatter);
                    LocalDateTime fin = LocalDateTime.parse(finText, formatter);
                    
                    // Validation : la fin doit être après le début
                    if (fin.isBefore(debut) || fin.isEqual(debut)) {
                        JOptionPane.showMessageDialog(this, 
                            "La date/heure de fin doit être postérieure à la date/heure de début!", 
                            "Dates invalides", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Validation : vérifier que la durée n'est pas excessive (ex: max 24h)
                    Duration duration = Duration.between(debut, fin);
                    if (duration.toHours() > 24) {
                        JOptionPane.showMessageDialog(this, 
                            "La durée ne peut pas dépasser 24 heures!", 
                            "Durée excessive", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Validation : vérifier les chevauchements avec d'autres horaires
                    if (detecterChevauchement(debut, fin, null)) {
                        int confirmation = JOptionPane.showConfirmDialog(this, 
                            "Attention : cet horaire chevauche avec un horaire existant.\n" +
                            "Voulez-vous continuer malgré tout ?", 
                            "Chevauchement détecté", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                        if (confirmation != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    
                    Horaire nouvelHoraire = new Horaire(debut, fin);
                    horaireService.ajouter(nouvelHoraire);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Horaire ajouté avec succès!", 
                        "Ajout réussi", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage() + 
                        "\nFormat attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Les dates/heures de début et de fin sont obligatoires!", 
                    "Champs manquants", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (horaireSelectionne != null) {
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField debutField = new JTextField();
            JTextField finField = new JTextField();
            
            // Formatter et afficher les données existantes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (horaireSelectionne.getDebut() != null) {
                debutField.setText(horaireSelectionne.getDebut().format(formatter));
            }
            if (horaireSelectionne.getFin() != null) {
                finField.setText(horaireSelectionne.getFin().format(formatter));
            }
            
            formulaire.add(new JLabel("Date/Heure début (dd/MM/yyyy HH:mm) :"));
            formulaire.add(debutField);
            formulaire.add(new JLabel("Date/Heure fin (dd/MM/yyyy HH:mm) :"));
            formulaire.add(finField);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier l'horaire: " + horaireSelectionne.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String debutText = debutField.getText().trim();
                String finText = finField.getText().trim();
                
                if (!debutText.isEmpty() && !finText.isEmpty()) {
                    try {
                        LocalDateTime debut = LocalDateTime.parse(debutText, formatter);
                        LocalDateTime fin = LocalDateTime.parse(finText, formatter);
                        
                        // Validation : la fin doit être après le début
                        if (fin.isBefore(debut) || fin.isEqual(debut)) {
                            JOptionPane.showMessageDialog(this, 
                                "La date/heure de fin doit être postérieure à la date/heure de début!", 
                                "Dates invalides", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Validation : vérifier que la durée n'est pas excessive (ex: max 24h)
                        Duration duration = Duration.between(debut, fin);
                        if (duration.toHours() > 24) {
                            JOptionPane.showMessageDialog(this, 
                                "La durée ne peut pas dépasser 24 heures!", 
                                "Durée excessive", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Validation : vérifier les chevauchements avec d'autres horaires (exclure le courant)
                        if (detecterChevauchement(debut, fin, horaireSelectionne)) {
                            int confirmation = JOptionPane.showConfirmDialog(this, 
                                "Attention : cet horaire chevauche avec un autre horaire existant.\n" +
                                "Voulez-vous continuer malgré tout ?", 
                                "Chevauchement détecté", 
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                            if (confirmation != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                        
                        // Mettre à jour les données de l'horaire sélectionné
                        horaireSelectionne.setDebut(debut);
                        horaireSelectionne.setFin(fin);
                        
                        horaireService.modifier(horaireSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Horaire modifié avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage() + 
                            "\nFormat attendu: dd/MM/yyyy HH:mm (ex: 15/01/2024 14:30)", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Les dates/heures de début et de fin sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (horaireSelectionne != null) {
            try {
                horaireService.supprimer(horaireSelectionne);
                JOptionPane.showMessageDialog(this, 
                    "Horaire supprimé avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                horaireSelectionne = null;
                
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
        return horaireSelectionne != null;
    }

    public Horaire getHoraireSelectionne() {
        return horaireSelectionne;
    }
    
    /**
     * Détecte si un horaire chevauche avec des horaires existants
     * @param debut Date/heure de début du nouvel horaire
     * @param fin Date/heure de fin du nouvel horaire
     * @param horaireAExclure Horaire à exclure de la vérification (pour modification)
     * @return true s'il y a chevauchement, false sinon
     */
    private boolean detecterChevauchement(LocalDateTime debut, LocalDateTime fin, Horaire horaireAExclure) {
        for (Horaire horaire : horaires) {
            // Exclure l'horaire en cours de modification
            if (horaireAExclure != null && horaire.getId() == horaireAExclure.getId()) {
                continue;
            }
            
            if (horaire.getDebut() != null && horaire.getFin() != null) {
                // Vérifier le chevauchement
                if (debut.isBefore(horaire.getFin()) && fin.isAfter(horaire.getDebut())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Méthode utilitaire pour étendre un horaire
     */
    public void etendreHoraire() {
        if (horaireSelectionne != null && horaireSelectionne.getDebut() != null && horaireSelectionne.getFin() != null) {
            String[] options = {"Étendre de 30 minutes", "Étendre de 1 heure", "Étendre de 2 heures", "Durée personnalisée"};
            
            String choix = (String) JOptionPane.showInputDialog(
                this,
                "Comment voulez-vous étendre l'horaire ?",
                "Étendre l'horaire",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choix != null) {
                try {
                    LocalDateTime nouvelleFin = horaireSelectionne.getFin();
                    
                    switch (choix) {
                        case "Étendre de 30 minutes":
                            nouvelleFin = nouvelleFin.plusMinutes(30);
                            break;
                        case "Étendre de 1 heure":
                            nouvelleFin = nouvelleFin.plusHours(1);
                            break;
                        case "Étendre de 2 heures":
                            nouvelleFin = nouvelleFin.plusHours(2);
                            break;
                        case "Durée personnalisée":
                            String dureeStr = JOptionPane.showInputDialog(this, 
                                "Entrez le nombre de minutes à ajouter :", "30");
                            if (dureeStr != null && !dureeStr.trim().isEmpty()) {
                                int minutes = Integer.parseInt(dureeStr.trim());
                                if (minutes > 0 && minutes <= 1440) { // Max 24h
                                    nouvelleFin = nouvelleFin.plusMinutes(minutes);
                                } else {
                                    JOptionPane.showMessageDialog(this, 
                                        "Veuillez entrer un nombre de minutes entre 1 et 1440 (24h)!", 
                                        "Durée invalide", 
                                        JOptionPane.WARNING_MESSAGE);
                                    return;
                                }
                            } else {
                                return;
                            }
                            break;
                    }
                    
                    // Vérifier la durée totale
                    Duration duration = Duration.between(horaireSelectionne.getDebut(), nouvelleFin);
                    if (duration.toHours() > 24) {
                        JOptionPane.showMessageDialog(this, 
                            "La durée totale ne peut pas dépasser 24 heures!", 
                            "Durée excessive", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    horaireSelectionne.setFin(nouvelleFin);
                    horaireService.modifier(horaireSelectionne);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Horaire étendu avec succès!", 
                        "Extension réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Veuillez entrer un nombre valide!", 
                        "Format invalide", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'extension: " + e.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}