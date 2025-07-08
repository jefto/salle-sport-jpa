package gui_admin.panel;

import entite.TypeAbonnement;
import service.TypeAbonnementService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class TypeAbonnementPanel extends JPanel implements CrudOperationsInterface {

    private List<TypeAbonnement> typesAbonnement;
    private TypeAbonnement typeAbonnementSelectionne;
    private TypeAbonnementService typeAbonnementService;
    private DefaultTableModel tableModel;
    private JTable table;
    
    // Pattern pour validation du code (lettres, chiffres, tirets et underscores)
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,20}$");
    
    // Préconfigurations de types d'abonnement courantes
    private static final Object[][] PRECONFIGURATIONS = {
        {"BASIC", "Abonnement Basic", 29},
        {"STANDARD", "Abonnement Standard", 49},
        {"PREMIUM", "Abonnement Premium", 79},
        {"VIP", "Abonnement VIP", 129},
        {"ETUDIANT", "Abonnement Étudiant", 19},
        {"SENIOR", "Abonnement Senior", 39},
        {"FAMILLE", "Abonnement Famille", 99},
        {"ANNUEL", "Abonnement Annuel", 299}
    };

    public TypeAbonnementPanel() {
        this.setLayout(new BorderLayout());
        this.typeAbonnementService = new TypeAbonnementService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "Code", "Libellé", "Montant", "Catégorie", "Rapport Q/P"};

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
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
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
                        typeAbonnementSelectionne = typesAbonnement.get(row);
                    } else {
                        typeAbonnementSelectionne = null;
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
        typesAbonnement = typeAbonnementService.listerTous();
        
        for (TypeAbonnement type : typesAbonnement) {
            // Déterminer la catégorie
            String categorie = determinerCategorie(type);
            
            // Calculer le rapport qualité-prix
            String rapportQP = calculerRapportQualitePrix(type);
            
            tableModel.addRow(new Object[]{
                false,
                type.getCode(),
                type.getLibelle(),
                type.getMontant() + "€",
                categorie,
                rapportQP
            });
        }
    }
    
    /**
     * Détermine la catégorie d'un type d'abonnement
     */
    private String determinerCategorie(TypeAbonnement type) {
        String code = type.getCode().toUpperCase();
        String libelle = type.getLibelle().toUpperCase();
        
        if (code.contains("ETUDIANT") || libelle.contains("ÉTUDIANT") || libelle.contains("ETUDIANT")) {
            return "Étudiant";
        } else if (code.contains("SENIOR") || libelle.contains("SENIOR")) {
            return "Senior";
        } else if (code.contains("FAMILLE") || libelle.contains("FAMILLE")) {
            return "Famille";
        } else if (code.contains("VIP") || libelle.contains("VIP")) {
            return "VIP";
        } else if (code.contains("PREMIUM") || libelle.contains("PREMIUM")) {
            return "Premium";
        } else if (code.contains("BASIC") || libelle.contains("BASIC") || libelle.contains("BASE")) {
            return "Basic";
        } else if (code.contains("ANNUEL") || libelle.contains("ANNUEL") || libelle.contains("AN")) {
            return "Annuel";
        } else if (type.getMontant() <= 20) {
            return "Économique";
        } else if (type.getMontant() <= 50) {
            return "Standard";
        } else if (type.getMontant() <= 100) {
            return "Premium";
        } else {
            return "Luxury";
        }
    }
    
    /**
     * Calcule le rapport qualité-prix d'un type d'abonnement
     */
    private String calculerRapportQualitePrix(TypeAbonnement type) {
        int montant = type.getMontant();
        
        if (montant <= 0) {
            return "Gratuit";
        } else if (montant <= 25) {
            return "Excellent";
        } else if (montant <= 50) {
            return "Bon";
        } else if (montant <= 100) {
            return "Correct";
        } else {
            return "Cher";
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire avec choix entre préconfiguration et saisie libre
        JPanel formulaire = new JPanel(new BorderLayout(10, 10));
        
        // Section choix du type
        JPanel choixPanel = new JPanel(new FlowLayout());
        JRadioButton preconfigureBouton = new JRadioButton("Type prédéfini", true);
        JRadioButton personnaliseBouton = new JRadioButton("Type personnalisé");
        ButtonGroup groupe = new ButtonGroup();
        groupe.add(preconfigureBouton);
        groupe.add(personnaliseBouton);
        
        choixPanel.add(new JLabel("Mode : "));
        choixPanel.add(preconfigureBouton);
        choixPanel.add(personnaliseBouton);
        
        // Section saisie
        JPanel saisiePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        // ComboBox pour préconfigurations
        JComboBox<String> preconfigCombo = new JComboBox<>();
        for (Object[] config : PRECONFIGURATIONS) {
            preconfigCombo.addItem(String.format("%s - %s (%d€)", 
                config[0], config[1], config[2]));
        }
        
        // Champs personnalisés
        JTextField codeField = new JTextField();
        JTextField libelleField = new JTextField();
        JTextField montantField = new JTextField();
        
        saisiePanel.add(new JLabel("Types prédéfinis :"));
        saisiePanel.add(preconfigCombo);
        saisiePanel.add(new JLabel("Code (max 20 caractères) :"));
        saisiePanel.add(codeField);
        saisiePanel.add(new JLabel("Libellé :"));
        saisiePanel.add(libelleField);
        saisiePanel.add(new JLabel("Montant (€) :"));
        saisiePanel.add(montantField);
        
        formulaire.add(choixPanel, BorderLayout.NORTH);
        formulaire.add(saisiePanel, BorderLayout.CENTER);
        
        // Gérer l'activation/désactivation des champs
        preconfigureBouton.addActionListener(e -> {
            preconfigCombo.setEnabled(true);
            codeField.setEnabled(false);
            libelleField.setEnabled(false);
            montantField.setEnabled(false);
        });
        
        personnaliseBouton.addActionListener(e -> {
            preconfigCombo.setEnabled(false);
            codeField.setEnabled(true);
            libelleField.setEnabled(true);
            montantField.setEnabled(true);
        });
        
        // État initial
        preconfigCombo.setEnabled(true);
        codeField.setEnabled(false);
        libelleField.setEnabled(false);
        montantField.setEnabled(false);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Créer un nouveau type d'abonnement", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String code;
                String libelle;
                int montant;
                
                if (preconfigureBouton.isSelected()) {
                    // Utiliser la préconfiguration
                    int configIndex = preconfigCombo.getSelectedIndex();
                    Object[] config = PRECONFIGURATIONS[configIndex];
                    code = (String) config[0];
                    libelle = (String) config[1];
                    montant = (Integer) config[2];
                } else {
                    // Utiliser les valeurs personnalisées
                    code = codeField.getText().trim();
                    libelle = libelleField.getText().trim();
                    String montantText = montantField.getText().trim();
                    
                    if (code.isEmpty() || libelle.isEmpty() || montantText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Tous les champs sont obligatoires!", 
                            "Champs manquants", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    montant = Integer.parseInt(montantText);
                }
                
                // Validations
                if (!CODE_PATTERN.matcher(code).matches()) {
                    JOptionPane.showMessageDialog(this, 
                        "Le code doit contenir uniquement des lettres, chiffres, tirets et underscores (max 20 caractères)!", 
                        "Code invalide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (libelle.length() > 100) {
                    JOptionPane.showMessageDialog(this, 
                        "Le libellé ne peut pas dépasser 100 caractères!", 
                        "Libellé trop long", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (montant < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Le montant ne peut pas être négatif!", 
                        "Montant invalide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (montant > 9999) {
                    JOptionPane.showMessageDialog(this, 
                        "Le montant ne peut pas dépasser 9999€!", 
                        "Montant trop élevé", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Vérifier l'unicité du code
                boolean codeExiste = typesAbonnement.stream()
                    .anyMatch(t -> t.getCode().equalsIgnoreCase(code));
                
                if (codeExiste) {
                    JOptionPane.showMessageDialog(this, 
                        "Un type d'abonnement avec ce code existe déjà!", 
                        "Code en doublon", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                TypeAbonnement nouveauType = new TypeAbonnement(code, libelle, montant);
                typeAbonnementService.ajouter(nouveauType);
                
                JOptionPane.showMessageDialog(this, 
                    "Type d'abonnement créé avec succès!\n" +
                    "Code: " + code + "\n" +
                    "Libellé: " + libelle + "\n" +
                    "Montant: " + montant + "€\n" +
                    "Catégorie: " + determinerCategorie(nouveauType), 
                    "Création réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez saisir un montant valide!", 
                    "Format invalide", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la création: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (typeAbonnementSelectionne != null) {
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField codeField = new JTextField(typeAbonnementSelectionne.getCode());
            JTextField libelleField = new JTextField(typeAbonnementSelectionne.getLibelle());
            JTextField montantField = new JTextField(String.valueOf(typeAbonnementSelectionne.getMontant()));
            
            // Le code ne doit pas être modifiable (clé primaire)
            codeField.setEditable(false);
            codeField.setBackground(Color.LIGHT_GRAY);
            
            formulaire.add(new JLabel("Code (non modifiable) :"));
            formulaire.add(codeField);
            formulaire.add(new JLabel("Libellé :"));
            formulaire.add(libelleField);
            formulaire.add(new JLabel("Montant (€) :"));
            formulaire.add(montantField);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier le type d'abonnement: " + typeAbonnementSelectionne.getCode(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String libelle = libelleField.getText().trim();
                String montantText = montantField.getText().trim();
                
                if (!libelle.isEmpty() && !montantText.isEmpty()) {
                    try {
                        int montant = Integer.parseInt(montantText);
                        
                        // Validations
                        if (libelle.length() > 100) {
                            JOptionPane.showMessageDialog(this, 
                                "Le libellé ne peut pas dépasser 100 caractères!", 
                                "Libellé trop long", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        if (montant < 0) {
                            JOptionPane.showMessageDialog(this, 
                                "Le montant ne peut pas être négatif!", 
                                "Montant invalide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        if (montant > 9999) {
                            JOptionPane.showMessageDialog(this, 
                                "Le montant ne peut pas dépasser 9999€!", 
                                "Montant trop élevé", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Mettre à jour les données du type sélectionné
                        typeAbonnementSelectionne.setLibelle(libelle);
                        typeAbonnementSelectionne.setMontant(montant);
                        
                        typeAbonnementService.modifier(typeAbonnementSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Type d'abonnement modifié avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Veuillez saisir un montant valide!", 
                            "Format invalide", 
                            JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Le libellé et le montant sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (typeAbonnementSelectionne != null) {
            StringBuilder infoType = new StringBuilder();
            infoType.append("Type d'abonnement: ").append(typeAbonnementSelectionne.getCode());
            infoType.append("\nLibellé: ").append(typeAbonnementSelectionne.getLibelle());
            infoType.append("\nMontant: ").append(typeAbonnementSelectionne.getMontant()).append("€");
            infoType.append("\nCatégorie: ").append(determinerCategorie(typeAbonnementSelectionne));
            
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer le type d'abonnement :\n" + infoType.toString() + 
                "\n\nATTENTION: Cette action supprimera également tous les abonnements de ce type!", 
                "Confirmation de suppression", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    typeAbonnementService.supprimer(typeAbonnementSelectionne);
                    JOptionPane.showMessageDialog(this, 
                        "Type d'abonnement supprimé avec succès!", 
                        "Suppression réussie", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    typeAbonnementSelectionne = null;
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression: " + e.getMessage() + 
                        "\nVeuillez d'abord supprimer les abonnements utilisant ce type.", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return typeAbonnementSelectionne != null;
    }

    public TypeAbonnement getTypeAbonnementSelectionne() {
        return typeAbonnementSelectionne;
    }
    
    /**
     * Méthode utilitaire pour créer tous les types prédéfinis
     */
    public void creerTypesPredefinis() {
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Créer tous les types d'abonnement prédéfinis ?\n" +
            "Cette action ajoutera 8 types standards si ils n'existent pas encore.", 
            "Création en lot", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            StringBuilder resultats = new StringBuilder();
            int ajouts = 0;
            int ignores = 0;
            
            for (Object[] config : PRECONFIGURATIONS) {
                String code = (String) config[0];
                String libelle = (String) config[1];
                int montant = (Integer) config[2];
                
                // Vérifier si le code existe déjà
                boolean codeExiste = typesAbonnement.stream()
                    .anyMatch(t -> t.getCode().equalsIgnoreCase(code));
                
                if (!codeExiste) {
                    try {
                        TypeAbonnement nouveau = new TypeAbonnement(code, libelle, montant);
                        typeAbonnementService.ajouter(nouveau);
                        ajouts++;
                    } catch (Exception e) {
                        resultats.append("Erreur pour ").append(code).append(": ")
                                 .append(e.getMessage()).append("\n");
                    }
                } else {
                    ignores++;
                }
            }
            
            // Recharger les données
            loadData();
            
            // Afficher le résumé
            StringBuilder message = new StringBuilder();
            message.append("=== CRÉATION EN LOT ===\n\n");
            message.append("Types créés: ").append(ajouts).append("\n");
            message.append("Types ignorés (déjà existants): ").append(ignores).append("\n");
            message.append("Total traité: ").append(PRECONFIGURATIONS.length).append("\n");
            
            if (resultats.length() > 0) {
                message.append("\nErreurs:\n").append(resultats);
            }
            
            JOptionPane.showMessageDialog(this, 
                message.toString(), 
                "Résultats de la création", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Affiche les statistiques des types d'abonnement
     */
    public void afficherStatistiques() {
        if (typesAbonnement.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun type d'abonnement disponible pour l'analyse.", 
                "Pas de données", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Calculs statistiques
        int total = typesAbonnement.size();
        int montantMin = typesAbonnement.stream().mapToInt(TypeAbonnement::getMontant).min().orElse(0);
        int montantMax = typesAbonnement.stream().mapToInt(TypeAbonnement::getMontant).max().orElse(0);
        double montantMoyen = typesAbonnement.stream().mapToInt(TypeAbonnement::getMontant).average().orElse(0);
        
        // Répartition par catégorie
        var categorieStats = typesAbonnement.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                this::determinerCategorie,
                java.util.stream.Collectors.counting()
            ));
        
        // Répartition par rapport qualité-prix
        var rapportStats = typesAbonnement.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                this::calculerRapportQualitePrix,
                java.util.stream.Collectors.counting()
            ));
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTIQUES DES TYPES D'ABONNEMENT ===\n\n");
        stats.append("Total des types: ").append(total).append("\n");
        stats.append("Montant minimum: ").append(montantMin).append("€\n");
        stats.append("Montant maximum: ").append(montantMax).append("€\n");
        stats.append("Montant moyen: ").append(String.format("%.2f€", montantMoyen)).append("\n\n");
        
        stats.append("=== RÉPARTITION PAR CATÉGORIE ===\n");
        categorieStats.entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> stats.append("• ").append(entry.getKey()).append(": ")
                                  .append(entry.getValue()).append(" type(s)\n"));
        
        stats.append("\n=== RÉPARTITION PAR RAPPORT QUALITÉ-PRIX ===\n");
        rapportStats.entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> stats.append("• ").append(entry.getKey()).append(": ")
                                  .append(entry.getValue()).append(" type(s)\n"));
        
        stats.append("\n=== DÉTAIL DES TYPES ===\n");
        typesAbonnement.stream()
            .sorted(java.util.Comparator.comparing(TypeAbonnement::getMontant))
            .forEach(type -> {
                stats.append("• ").append(type.getCode()).append(" - ")
                     .append(type.getLibelle()).append(" (").append(type.getMontant())
                     .append("€) - ").append(determinerCategorie(type)).append("\n");
            });
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Statistiques des types d'abonnement", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Méthode utilitaire pour dupliquer un type d'abonnement
     */
    public void dupliquerType() {
        if (typeAbonnementSelectionne != null) {
            String nouveauCodeInput = JOptionPane.showInputDialog(this, 
                "Entrez le code pour la copie :", 
                typeAbonnementSelectionne.getCode() + "_COPY");

            if (nouveauCodeInput != null && !nouveauCodeInput.trim().isEmpty()) {
                String nouveauCode = nouveauCodeInput.trim();
                // Continuer avec nouveauCode
            
                // Validation du code
                if (!CODE_PATTERN.matcher(nouveauCode).matches()) {
                    JOptionPane.showMessageDialog(this, 
                        "Le code doit contenir uniquement des lettres, chiffres, tirets et underscores (max 20 caractères)!", 
                        "Code invalide", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Vérifier l'unicité
                boolean codeExiste = typesAbonnement.stream()
                    .anyMatch(t -> t.getCode().equalsIgnoreCase(nouveauCode));
                
                if (codeExiste) {
                    JOptionPane.showMessageDialog(this, 
                        "Un type d'abonnement avec ce code existe déjà!", 
                        "Code en doublon", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    TypeAbonnement copie = new TypeAbonnement(
                        nouveauCode, 
                        typeAbonnementSelectionne.getLibelle() + " (Copie)", 
                        typeAbonnementSelectionne.getMontant()
                    );
                    typeAbonnementService.ajouter(copie);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Type d'abonnement dupliqué avec succès!", 
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
     * Affiche une grille tarifaire comparative
     */
    public void afficherGrilleTarifaire() {
        if (typesAbonnement.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun type d'abonnement disponible.", 
                "Pas de données", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder grille = new StringBuilder();
        grille.append("=== GRILLE TARIFAIRE ===\n\n");
        grille.append(String.format("%-20s %-30s %-10s %-15s\n", 
            "CODE", "LIBELLÉ", "PRIX", "CATÉGORIE"));
        grille.append("-".repeat(80)).append("\n");
        
        typesAbonnement.stream()
            .sorted(java.util.Comparator.comparing(TypeAbonnement::getMontant))
            .forEach(type -> {
                grille.append(String.format("%-20s %-30s %-10s %-15s\n",
                    type.getCode(),
                    type.getLibelle().length() > 30 ? 
                        type.getLibelle().substring(0, 27) + "..." : type.getLibelle(),
                    type.getMontant() + "€",
                    determinerCategorie(type)
                ));
            });
        
        grille.append("\n=== RECOMMANDATIONS ===\n");
        grille.append("• Économique: ≤ 20€\n");
        grille.append("• Standard: 21-50€\n");
        grille.append("• Premium: 51-100€\n");
        grille.append("• Luxury: > 100€\n");
        
        JTextArea textArea = new JTextArea(grille.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 350));
        
        JOptionPane.showMessageDialog(this, 
            scrollPane, 
            "Grille tarifaire", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}