package gui_admin.panel;

import entite.Seance;
import entite.Salle;
import entite.Membre;
import service.SeanceService;
import service.SalleService;
import service.MembreService;
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

public class SeancePanel extends JPanel implements CrudOperationsInterface {

    private List<Seance> seances;
    private Seance seanceSelectionnee;
    private SeanceService seanceService;
    private SalleService salleService;
    private MembreService membreService;
    private DefaultTableModel tableModel;
    private JTable table;

    public SeancePanel() {
        this.setLayout(new BorderLayout());
        this.seanceService = new SeanceService();
        this.salleService = new SalleService();
        this.membreService = new MembreService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date début", "Date fin", "Durée", "Salle", "Membre", "Statut"};

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
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        
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
                        seanceSelectionnee = seances.get(row);
                    } else {
                        seanceSelectionnee = null;
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
        seances = seanceService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Seance seance : seances) {
            String dateDebut = seance.getDateDebut() != null ? 
                seance.getDateDebut().format(formatter) : "Non définie";
            String dateFin = seance.getDateFin() != null ? 
                seance.getDateFin().format(formatter) : "Non définie";
            
            // Calculer la durée
            String duree = "N/A";
            if (seance.getDateDebut() != null && seance.getDateFin() != null) {
                Duration duration = Duration.between(seance.getDateDebut(), seance.getDateFin());
                long heures = duration.toHours();
                long minutes = duration.toMinutesPart();
                duree = String.format("%dh%02dm", heures, minutes);
            }
            
            // Informations salle
            String salle = seance.getSalle() != null ? 
                seance.getSalle().getLibelle() : "Aucune salle";
            
            // Informations membre
            String membre = "Aucun membre";
            if (seance.getMembre() != null && seance.getMembre().getClient() != null) {
                membre = seance.getMembre().getClient().getPrenom() + " " + 
                        seance.getMembre().getClient().getNom();
            } else if (seance.getMembre() != null) {
                membre = "Membre ID " + seance.getMembre().getId();
            }
            
            // Déterminer le statut
            String statut = determinerStatut(seance);
            
            tableModel.addRow(new Object[]{
                false,
                seance.getId(),
                dateDebut,
                dateFin,
                duree,
                salle,
                membre,
                statut
            });
        }
    }
    
    /**
     * Détermine le statut d'une séance
     */
    private String determinerStatut(Seance seance) {
        if (seance.getDateDebut() == null || seance.getDateFin() == null) {
            return "Incomplète";
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        
        if (seance.getDateFin().isBefore(maintenant)) {
            return "Terminée";
        } else if (seance.getDateDebut().isAfter(maintenant)) {
            return "Programmée";
        } else {
            return "En cours";
        }
    }

    @Override
    public void ajouter() {
        // Récupérer les listes
        List<Salle> salles = salleService.listerTous();
        List<Membre> membres = membreService.listerTous();
        
        if (salles.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucune salle disponible. Veuillez d'abord créer une salle.", 
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
        JPanel formulaire = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField dateDebutField = new JTextField();
        JTextField dateFinField = new JTextField();
        JComboBox<Salle> salleCombo = new JComboBox<>(salles.toArray(new Salle[0]));
        JComboBox<Membre> membreCombo = new JComboBox<>(membres.toArray(new Membre[0]));
        
        // Pré-remplir avec des dates par défaut
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime maintenant = LocalDateTime.now();
        dateDebutField.setText(maintenant.format(formatter));
        dateFinField.setText(maintenant.plusHours(1).format(formatter));
        
        // Personaliser l'affichage des salles
        salleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Salle) {
                    Salle salle = (Salle) value;
                    setText(salle.getLibelle() + (salle.getDescription() != null ? 
                        " (" + salle.getDescription() + ")" : ""));
                }
                return this;
            }
        });
        
        // Personaliser l'affichage des membres
        membreCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Membre) {
                    Membre membre = (Membre) value;
                    if (membre.getClient() != null) {
                        setText(membre.getClient().getPrenom() + " " + membre.getClient().getNom() + 
                               " (ID: " + membre.getId() + ")");
                    } else {
                        setText("Membre ID " + membre.getId());
                    }
                }
                return this;
            }
        });
        
        formulaire.add(new JLabel("Date début (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateDebutField);
        formulaire.add(new JLabel("Date fin (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateFinField);
        formulaire.add(new JLabel("Salle :"));
        formulaire.add(salleCombo);
        formulaire.add(new JLabel("Membre :"));
        formulaire.add(membreCombo);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Programmer une nouvelle séance", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String dateDebutText = dateDebutField.getText().trim();
            String dateFinText = dateFinField.getText().trim();
            Salle salleSelectionnee = (Salle) salleCombo.getSelectedItem();
            Membre membreSelectionne = (Membre) membreCombo.getSelectedItem();
            
            if (!dateDebutText.isEmpty() && !dateFinText.isEmpty() && 
                salleSelectionnee != null && membreSelectionne != null) {
                try {
                    LocalDateTime dateDebut = LocalDateTime.parse(dateDebutText, formatter);
                    LocalDateTime dateFin = LocalDateTime.parse(dateFinText, formatter);
                    
                    // Validation des dates
                    if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                        JOptionPane.showMessageDialog(this, 
                            "La date de fin doit être postérieure à la date de début!", 
                            "Dates invalides", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Vérifier que la séance ne dure pas plus de 12 heures
                    Duration duree = Duration.between(dateDebut, dateFin);
                    if (duree.toHours() > 12) {
                        JOptionPane.showMessageDialog(this, 
                            "Une séance ne peut pas durer plus de 12 heures!", 
                            "Durée excessive", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Vérifier les conflits de salle
                    if (verifierConflitSalle(salleSelectionnee, dateDebut, dateFin, null)) {
                        JOptionPane.showMessageDialog(this, 
                            "Conflit détecté : cette salle est déjà occupée pendant cette période!", 
                            "Conflit de salle", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    Seance nouvelleSeance = new Seance(dateDebut, dateFin, membreSelectionne, salleSelectionnee);
                    seanceService.ajouter(nouvelleSeance);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Séance programmée avec succès!\nDurée: " + 
                        String.format("%dh%02dm", duree.toHours(), duree.toMinutesPart()), 
                        "Programmation réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la programmation: " + e.getMessage() + 
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
        if (seanceSelectionnee != null) {
            // Récupérer les listes
            List<Salle> salles = salleService.listerTous();
            List<Membre> membres = membreService.listerTous();
            
            if (salles.isEmpty() || membres.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Données insuffisantes pour la modification.", 
                    "Erreur", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(4, 2, 10, 10));
            JTextField dateDebutField = new JTextField();
            JTextField dateFinField = new JTextField();
            JComboBox<Salle> salleCombo = new JComboBox<>(salles.toArray(new Salle[0]));
            JComboBox<Membre> membreCombo = new JComboBox<>(membres.toArray(new Membre[0]));
            
            // Formatter et afficher les dates existantes
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (seanceSelectionnee.getDateDebut() != null) {
                dateDebutField.setText(seanceSelectionnee.getDateDebut().format(formatter));
            }
            if (seanceSelectionnee.getDateFin() != null) {
                dateFinField.setText(seanceSelectionnee.getDateFin().format(formatter));
            }
            
            // Personaliser l'affichage des salles
            salleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Salle) {
                        Salle salle = (Salle) value;
                        setText(salle.getLibelle() + (salle.getDescription() != null ? 
                            " (" + salle.getDescription() + ")" : ""));
                    }
                    return this;
                }
            });
            
            // Personaliser l'affichage des membres
            membreCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Membre) {
                        Membre membre = (Membre) value;
                        if (membre.getClient() != null) {
                            setText(membre.getClient().getPrenom() + " " + membre.getClient().getNom() + 
                                   " (ID: " + membre.getId() + ")");
                        } else {
                            setText("Membre ID " + membre.getId());
                        }
                    }
                    return this;
                }
            });
            
            // Sélectionner les valeurs actuelles
            if (seanceSelectionnee.getSalle() != null) {
                salleCombo.setSelectedItem(seanceSelectionnee.getSalle());
            }
            if (seanceSelectionnee.getMembre() != null) {
                membreCombo.setSelectedItem(seanceSelectionnee.getMembre());
            }
            
            formulaire.add(new JLabel("Date début (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateDebutField);
            formulaire.add(new JLabel("Date fin (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateFinField);
            formulaire.add(new JLabel("Salle :"));
            formulaire.add(salleCombo);
            formulaire.add(new JLabel("Membre :"));
            formulaire.add(membreCombo);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier la séance ID: " + seanceSelectionnee.getId(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String dateDebutText = dateDebutField.getText().trim();
                String dateFinText = dateFinField.getText().trim();
                Salle salleSelectionnee = (Salle) salleCombo.getSelectedItem();
                Membre membreSelectionne = (Membre) membreCombo.getSelectedItem();
                
                if (!dateDebutText.isEmpty() && !dateFinText.isEmpty() && 
                    salleSelectionnee != null && membreSelectionne != null) {
                    try {
                        LocalDateTime dateDebut = LocalDateTime.parse(dateDebutText, formatter);
                        LocalDateTime dateFin = LocalDateTime.parse(dateFinText, formatter);
                        
                        // Validation des dates
                        if (dateFin.isBefore(dateDebut) || dateFin.isEqual(dateDebut)) {
                            JOptionPane.showMessageDialog(this, 
                                "La date de fin doit être postérieure à la date de début!", 
                                "Dates invalides", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Vérifier que la séance ne dure pas plus de 12 heures
                        Duration duree = Duration.between(dateDebut, dateFin);
                        if (duree.toHours() > 12) {
                            JOptionPane.showMessageDialog(this, 
                                "Une séance ne peut pas durer plus de 12 heures!", 
                                "Durée excessive", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Vérifier les conflits de salle (exclure la séance actuelle)
                        if (verifierConflitSalle(salleSelectionnee, dateDebut, dateFin, this.seanceSelectionnee)) {
                            JOptionPane.showMessageDialog(this, 
                                "Conflit détecté : cette salle est déjà occupée pendant cette période!", 
                                "Conflit de salle", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Mettre à jour les données de la séance sélectionnée
                        this.seanceSelectionnee.setDateDebut(dateDebut);
                        this.seanceSelectionnee.setDateFin(dateFin);
                        this.seanceSelectionnee.setSalle(salleSelectionnee);
                        this.seanceSelectionnee.setMembre(membreSelectionne);
                        
                        seanceService.modifier(this.seanceSelectionnee);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Séance modifiée avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
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
        if (seanceSelectionnee != null) {
            StringBuilder infoSeance = new StringBuilder();
            infoSeance.append("Séance ID ").append(seanceSelectionnee.getId());
            
            if (seanceSelectionnee.getDateDebut() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                infoSeance.append("\nDate: ").append(seanceSelectionnee.getDateDebut().format(formatter));
            }
            
            if (seanceSelectionnee.getSalle() != null) {
                infoSeance.append("\nSalle: ").append(seanceSelectionnee.getSalle().getLibelle());
            }
            
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer la séance :\n" + infoSeance.toString() + " ?", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    seanceService.supprimer(seanceSelectionnee);
                    JOptionPane.showMessageDialog(this, 
                        "Séance supprimée avec succès!", 
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    seanceSelectionnee = null;
                    
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
        return seanceSelectionnee != null;
    }

    public Seance getSeanceSelectionnee() {
        return seanceSelectionnee;
    }
    
    /**
     * Vérifie s'il y a un conflit de salle pour une période donnée
     */
    private boolean verifierConflitSalle(Salle salle, LocalDateTime dateDebut, LocalDateTime dateFin, Seance seanceAExclure) {
        return seances.stream()
            .filter(seance -> seanceAExclure == null || !seance.getId().equals(seanceAExclure.getId()))
            .filter(seance -> seance.getSalle() != null && seance.getSalle().getId().equals(salle.getId()))
            .filter(seance -> seance.getDateDebut() != null && seance.getDateFin() != null)
            .anyMatch(seance -> 
                // Vérifier le chevauchement des périodes
                dateDebut.isBefore(seance.getDateFin()) && dateFin.isAfter(seance.getDateDebut())
            );
    }
    
    /**
     * Méthode utilitaire pour dupliquer une séance
     */
    public void dupliquerSeance() {
        if (seanceSelectionnee != null) {
            // Demander la nouvelle date de début
            String nouvelleDateStr = JOptionPane.showInputDialog(this, 
                "Entrez la nouvelle date de début (dd/MM/yyyy HH:mm) :", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            if (nouvelleDateStr != null && !nouvelleDateStr.trim().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime nouvelleDateDebut = LocalDateTime.parse(nouvelleDateStr, formatter);
                    
                    // Calculer la durée de la séance originale
                    Duration dureeOriginale = Duration.between(
                        seanceSelectionnee.getDateDebut(), 
                        seanceSelectionnee.getDateFin()
                    );
                    
                    LocalDateTime nouvelleDateFin = nouvelleDateDebut.plus(dureeOriginale);
                    
                    // Vérifier les conflits
                    if (verifierConflitSalle(seanceSelectionnee.getSalle(), nouvelleDateDebut, nouvelleDateFin, null)) {
                        JOptionPane.showMessageDialog(this, 
                            "Conflit détecté : la salle est déjà occupée pendant cette période!", 
                            "Conflit de salle", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    Seance copie = new Seance(nouvelleDateDebut, nouvelleDateFin, 
                                            seanceSelectionnee.getMembre(), 
                                            seanceSelectionnee.getSalle());
                    seanceService.ajouter(copie);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Séance dupliquée avec succès!", 
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
     * Affiche les statistiques des séances
     */
    public void afficherStatistiques() {
        int total = seances.size();
        int terminees = 0;
        int enCours = 0;
        int programmees = 0;
        int incompletes = 0;
        
        Duration dureeTotal = Duration.ZERO;
        int seancesAvecDuree = 0;
        
        for (Seance seance : seances) {
            String statut = determinerStatut(seance);
            switch (statut) {
                case "Terminée" -> terminees++;
                case "En cours" -> enCours++;
                case "Programmée" -> programmees++;
                default -> incompletes++;
            }
            
            if (seance.getDateDebut() != null && seance.getDateFin() != null) {
                dureeTotal = dureeTotal.plus(Duration.between(seance.getDateDebut(), seance.getDateFin()));
                seancesAvecDuree++;
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTIQUES DES SÉANCES ===\n\n");
        stats.append("Total des séances: ").append(total).append("\n");
        stats.append("Séances terminées: ").append(terminees).append("\n");
        stats.append("Séances en cours: ").append(enCours).append("\n");
        stats.append("Séances programmées: ").append(programmees).append("\n");
        stats.append("Séances incomplètes: ").append(incompletes).append("\n\n");
        
        if (seancesAvecDuree > 0) {
            Duration dureeMoyenne = dureeTotal.dividedBy(seancesAvecDuree);
            stats.append("Durée totale: ").append(String.format("%dh%02dm", 
                dureeTotal.toHours(), dureeTotal.toMinutesPart())).append("\n");
            stats.append("Durée moyenne: ").append(String.format("%dh%02dm", 
                dureeMoyenne.toHours(), dureeMoyenne.toMinutesPart())).append("\n");
        }
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Statistiques des séances", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Affiche le planning des séances d'aujourd'hui
     */
    public void afficherPlanningJour() {
        LocalDateTime debutJour = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1).minusMinutes(1);
        
        List<Seance> seancesJour = seances.stream()
            .filter(seance -> seance.getDateDebut() != null)
            .filter(seance -> seance.getDateDebut().isAfter(debutJour) && 
                             seance.getDateDebut().isBefore(finJour))
            .sorted((s1, s2) -> s1.getDateDebut().compareTo(s2.getDateDebut()))
            .toList();
        
        StringBuilder planning = new StringBuilder();
        planning.append("=== PLANNING DU ").append(debutJour.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" ===\n\n");
        
        if (seancesJour.isEmpty()) {
            planning.append("Aucune séance programmée aujourd'hui.");
        } else {
            DateTimeFormatter heureFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (Seance seance : seancesJour) {
                planning.append("• ").append(seance.getDateDebut().format(heureFormatter))
                        .append(" - ").append(seance.getDateFin().format(heureFormatter));
                
                if (seance.getSalle() != null) {
                    planning.append(" | Salle: ").append(seance.getSalle().getLibelle());
                }
                
                if (seance.getMembre() != null && seance.getMembre().getClient() != null) {
                    planning.append(" | ").append(seance.getMembre().getClient().getPrenom())
                            .append(" ").append(seance.getMembre().getClient().getNom());
                }
                
                planning.append(" (").append(determinerStatut(seance)).append(")\n");
            }
        }
        
        JTextArea textArea = new JTextArea(planning.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Planning du jour", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}