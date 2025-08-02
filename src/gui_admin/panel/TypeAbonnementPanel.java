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
    
    // Préconfigurations fixes pour les types d'abonnement
    private static final Object[][] PRECONFIGURATIONS = {
        {"BASIC", "Basic", 5000},
        {"STANDARD", "Standard", 15000},
        {"PREMIUM", "Prenium", 25000}
    };

    public TypeAbonnementPanel() {
        this.setLayout(new BorderLayout());
        this.typeAbonnementService = new TypeAbonnementService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "Code", "Libellé", "Montant"};

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
        table.getColumnModel().getColumn(0).setPreferredWidth(70);   // Sélection
        table.getColumnModel().getColumn(1).setPreferredWidth(100);  // Code
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Libellé
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Montant

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
        tableModel.setRowCount(0);
        typesAbonnement = typeAbonnementService.listerTous();
        for (TypeAbonnement type : typesAbonnement) {
            tableModel.addRow(new Object[]{
                false,
                type.getCode(),
                type.getLibelle(),
                type.getMontant() + " FCFA"
            });
        }
    }
    
    /**
     * Détermine la catégorie d'un type d'abonnement
     */
    private String determinerCategorie(TypeAbonnement type) {
        // Pour 3 formules, la catégorie est le libellé
        return type.getLibelle();
    }
    
    /**
     * Calcule le rapport qualité-prix d'un type d'abonnement
     */
    private String calculerRapportQualitePrix(TypeAbonnement type) {
        int montant = type.getMontant();
        if (montant == 5000) return "Économique";
        if (montant == 15000) return "Standard";
        if (montant == 25000) return "Premium";
        return "Inconnu";
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie manuelle
        JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField codeField = new JTextField();
        JTextField libelleField = new JTextField();
        JTextField montantField = new JTextField();

        formulaire.add(new JLabel("Code :"));
        formulaire.add(codeField);
        formulaire.add(new JLabel("Libellé :"));
        formulaire.add(libelleField);
        formulaire.add(new JLabel("Montant (FCFA) :"));
        formulaire.add(montantField);

        // Ajouter des tooltips pour aider l'utilisateur
        codeField.setToolTipText("Code unique (lettres, chiffres, tirets et underscores, max 20 caractères)");
        libelleField.setToolTipText("Nom du type d'abonnement (max 100 caractères)");
        montantField.setToolTipText("Montant en FCFA (nombre entier)");

        int result = JOptionPane.showConfirmDialog(
            this,
            formulaire,
            "Ajouter un nouveau type d'abonnement",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String libelle = libelleField.getText().trim();
            String montantText = montantField.getText().trim();

            // Validation des champs
            if (code.isEmpty() || libelle.isEmpty() || montantText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs sont obligatoires!",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validation du code
            if (!CODE_PATTERN.matcher(code).matches()) {
                JOptionPane.showMessageDialog(this,
                    "Le code doit contenir uniquement des lettres, chiffres, tirets et underscores (max 20 caractères)!",
                    "Code invalide",
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

            // Validation du libellé
            if (libelle.length() > 100) {
                JOptionPane.showMessageDialog(this,
                    "Le libellé ne peut pas dépasser 100 caractères!",
                    "Libellé trop long",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validation et conversion du montant
            try {
                int montant = Integer.parseInt(montantText);

                if (montant <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Le montant doit être supérieur à 0!",
                        "Montant invalide",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Créer le nouveau type d'abonnement
                TypeAbonnement nouveauType = new TypeAbonnement();
                nouveauType.setCode(code.toUpperCase()); // Uniformiser en majuscules
                nouveauType.setLibelle(libelle);
                nouveauType.setMontant(montant);

                // Ajouter en base de données
                typeAbonnementService.ajouter(nouveauType);

                JOptionPane.showMessageDialog(this,
                    "Type d'abonnement ajouté avec succès!",
                    "Ajout réussi",
                    JOptionPane.INFORMATION_MESSAGE);

                // Recharger les données
                loadData();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez saisir un montant valide (nombre entier)!",
                    "Format invalide",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (typeAbonnementSelectionne != null) {
            JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField codeField = new JTextField(typeAbonnementSelectionne.getCode());
            JTextField libelleField = new JTextField(typeAbonnementSelectionne.getLibelle());
            JTextField montantField = new JTextField(String.valueOf(typeAbonnementSelectionne.getMontant()));

            codeField.setEditable(false);
            codeField.setBackground(Color.LIGHT_GRAY);

            formulaire.add(new JLabel("Code (non modifiable) :"));
            formulaire.add(codeField);
            formulaire.add(new JLabel("Libellé :"));
            formulaire.add(libelleField);
            formulaire.add(new JLabel("Montant (FCFA) :"));
            formulaire.add(montantField);

            // Ajouter des tooltips
            libelleField.setToolTipText("Nom du type d'abonnement (max 100 caractères)");
            montantField.setToolTipText("Montant en FCFA (nombre entier positif)");

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

                // Validation des champs
                if (libelle.isEmpty() || montantText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Le libellé and le montant sont obligatoires!",
                        "Champs manquants",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validation du libellé
                if (libelle.length() > 100) {
                    JOptionPane.showMessageDialog(this,
                        "Le libellé ne peut pas dépasser 100 caractères!",
                        "Libellé trop long",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int montant = Integer.parseInt(montantText);

                    if (montant <= 0) {
                        JOptionPane.showMessageDialog(this,
                            "Le montant doit être supérieur à 0!",
                            "Montant invalide",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Mettre à jour les données
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
                        "Veuillez saisir un montant valide (nombre entier)!",
                        "Format invalide",
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
            infoType.append("\nMontant: ").append(typeAbonnementSelectionne.getMontant()).append(" FCFA");
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
            "Créer les 3 types d'abonnement prédéfinis ?\n" +
            "Cette action ajoutera Basic, Standard et Prenium si ils n'existent pas encore.",
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
                boolean codeExiste = typesAbonnement.stream()
                    .anyMatch(t -> t.getCode().equalsIgnoreCase(code));
                if (!codeExiste) {
                    try {
                        TypeAbonnement nouveau = new TypeAbonnement(code, libelle, montant);
                        // La monnaie est toujours FCFA, il n'y a pas de setMonnaie
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
            loadData();
            StringBuilder message = new StringBuilder();
            message.append("=== CR��ATION EN LOT ===\n\n");
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
                    type.getMontant() + " FCFA",
                    determinerCategorie(type)
                ));
            });
        grille.append("\n=== RECOMMANDATIONS ===\n");
        grille.append("• Basic: 5000 FCFA\n");
        grille.append("• Standard: 15000 FCFA\n");
        grille.append("• Premium: 25000 FCFA\n");
        JTextArea textArea = new JTextArea(grille.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 350));
        JOptionPane.showMessageDialog(this,
            scrollPane, 
            "Grille tarifaire", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

