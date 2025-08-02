package gui_admin.panel;

import entite.Membre;
import entite.Client;
import service.MembreService;
import service.ClientService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MembrePanel extends JPanel implements CrudOperationsInterface {

    private List<Membre> membres;
    private Membre membreSelectionne;
    private MembreService membreService;
    private ClientService clientService;
    private DefaultTableModel tableModel;
    private JTable table;

    public MembrePanel() {
        this.setLayout(new BorderLayout());
        this.membreService = new MembreService();
        this.clientService = new ClientService();

        initializeTable();
        loadData();
    }

    private void initializeTable() {
        // Suppression de la colonne "Abonnements"
        String[] colonnes = {"Sélection", "ID", "Date inscription", "Nom complet client"};

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
        table.getColumnModel().getColumn(1).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // Date inscription
        table.getColumnModel().getColumn(3).setPreferredWidth(250);  // Nom complet client

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
                        membreSelectionne = membres.get(row);
                    } else {
                        membreSelectionne = null;
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

        try {
            // Charger les données avec gestion d'erreurs
            membres = membreService.listerTous();

            if (membres == null) {
                JOptionPane.showMessageDialog(this,
                    "Erreur : La liste des membres est null",
                    "Erreur de chargement",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (membres.isEmpty()) {
                // Afficher un message informatif si aucun membre n'existe
                System.out.println("INFO: Aucun membre trouvé dans la base de données");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Membre membre : membres) {
                try {
                    String dateInscription = membre.getDateInscription() != null ?
                        membre.getDateInscription().format(formatter) : "Non définie";

                    String nomComplet = "Client non assigné";

                    if (membre.getClient() != null) {
                        Client client = membre.getClient();
                        nomComplet = (client.getNom() != null ? client.getNom() : "") + " " +
                                    (client.getPrenom() != null ? client.getPrenom() : "");
                    }

                    tableModel.addRow(new Object[]{
                        false,
                        membre.getId(),
                        dateInscription,
                        nomComplet
                    });
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement du membre ID " + membre.getId() + ": " + e.getMessage());
                }
            }

            System.out.println("INFO: " + membres.size() + " membre(s) chargé(s) dans le tableau");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des membres : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void ajouter() {
        // Récupérer la liste des clients
        List<Client> clients = clientService.listerTous();

        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucun client disponible. Veuillez d'abord créer un client.",
                "Erreur",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Filtrer les clients qui ne sont pas déjà membres
        List<Client> clientsDisponibles = clients.stream()
            .filter(client -> !estDejaMembe(client))
            .toList();

        if (clientsDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Tous les clients sont déjà membres.",
                "Aucun client disponible",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Créer un formulaire de saisie SEULEMENT pour membre (pas d'abonnement)
        JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField dateInscriptionField = new JTextField();
        JComboBox<Client> clientCombo = new JComboBox<>(clientsDisponibles.toArray(new Client[0]));

        // Pré-remplir avec la date actuelle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateInscriptionField.setText(LocalDateTime.now().format(formatter));

        // Personaliser l'affichage des clients dans la ComboBox
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client) {
                    Client client = (Client) value;
                    setText(client.getPrenom() + " " + client.getNom() );
                }
                return this;
            }
        });

        formulaire.add(new JLabel("Date inscription (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateInscriptionField);
        formulaire.add(new JLabel("Client :"));
        formulaire.add(clientCombo);

        int result = JOptionPane.showConfirmDialog(
            this,
            formulaire,
            "Inscrire un nouveau membre",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String dateInscriptionText = dateInscriptionField.getText().trim();
            Client clientSelectionne = (Client) clientCombo.getSelectedItem();

            if (!dateInscriptionText.isEmpty() && clientSelectionne != null) {
                try {
                    LocalDateTime dateInscription = LocalDateTime.parse(dateInscriptionText, formatter);

                    // Validation : vérifier que la date d'inscription n'est pas dans le futur
                    if (dateInscription.isAfter(LocalDateTime.now())) {
                        JOptionPane.showMessageDialog(this,
                            "La date d'inscription ne peut pas être dans le futur!",
                            "Date invalide",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Vérifier à nouveau que le client n'est pas déjà membre (sécurité)
                    if (estDejaMembe(clientSelectionne)) {
                        JOptionPane.showMessageDialog(this,
                            "Ce client est déjà membre!",
                            "Client déjà inscrit",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Créer SEULEMENT le membre (sans abonnement)
                    Membre nouveauMembre = new Membre();
                    nouveauMembre.setDateInscription(dateInscription);
                    nouveauMembre.setClient(clientSelectionne);

                    // Sauvegarder le membre
                    membreService.ajouter(nouveauMembre);

                    JOptionPane.showMessageDialog(this,
                        "Membre inscrit avec succès!\nNom: " + clientSelectionne.getPrenom() + " " + clientSelectionne.getNom() +
                        "\n\nPour ajouter un abonnement, utilisez la section 'Abonnements'.",
                        "Inscription réussie",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recharger les données
                    loadData();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'inscription: " + e.getMessage() +
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
        if (membreSelectionne != null) {
            // Récupérer la liste des clients
            List<Client> clients = clientService.listerTous();

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun client disponible.",
                    "Erreur",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Filtrer les clients (inclure le client actuel du membre)
            List<Client> clientsDisponibles = clients.stream()
                .filter(client -> !estDejaMembe(client) || client.equals(membreSelectionne.getClient()))
                .toList();

            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField dateInscriptionField = new JTextField();
            JComboBox<Client> clientCombo = new JComboBox<>(clientsDisponibles.toArray(new Client[0]));

            // Formatter et afficher la date existante
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (membreSelectionne.getDateInscription() != null) {
                dateInscriptionField.setText(membreSelectionne.getDateInscription().format(formatter));
            }

            // Personaliser l'affichage des clients dans la ComboBox
            clientCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Client) {
                        Client client = (Client) value;
                        setText(client.getPrenom() + " " + client.getNom() + " (" + client.getEmail() + ")");
                    }
                    return this;
                }
            });

            // Sélectionner le client actuel
            if (membreSelectionne.getClient() != null) {
                clientCombo.setSelectedItem(membreSelectionne.getClient());
            }

            formulaire.add(new JLabel("Date inscription (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateInscriptionField);
            formulaire.add(new JLabel("Client :"));
            formulaire.add(clientCombo);

            String titreClient = membreSelectionne.getClient() != null ?
                membreSelectionne.getClient().getPrenom() + " " + membreSelectionne.getClient().getNom() :
                "ID " + membreSelectionne.getId();

            int result = JOptionPane.showConfirmDialog(
                this,
                formulaire,
                "Modifier le membre: " + titreClient,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String dateInscriptionText = dateInscriptionField.getText().trim();
                Client clientSelectionne = (Client) clientCombo.getSelectedItem();

                if (!dateInscriptionText.isEmpty() && clientSelectionne != null) {
                    try {
                        LocalDateTime dateInscription = LocalDateTime.parse(dateInscriptionText, formatter);

                        // Validation : vérifier que la date d'inscription n'est pas dans le futur
                        if (dateInscription.isAfter(LocalDateTime.now())) {
                            JOptionPane.showMessageDialog(this,
                                "La date d'inscription ne peut pas être dans le futur!",
                                "Date invalide",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Vérifier que le nouveau client n'est pas déjà membre (sauf si c'est le même)
                        if (!clientSelectionne.equals(membreSelectionne.getClient()) && estDejaMembe(clientSelectionne)) {
                            JOptionPane.showMessageDialog(this,
                                "Ce client est déjà membre!",
                                "Client déjà inscrit",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // Mettre à jour les données du membre sélectionné
                        membreSelectionne.setDateInscription(dateInscription);
                        membreSelectionne.setClient(clientSelectionne);

                        membreService.modifier(membreSelectionne);

                        JOptionPane.showMessageDialog(this,
                            "Membre modifié avec succès!",
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
                        "La date d'inscription et le client sont obligatoires!",
                        "Champs manquants",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (membreSelectionne != null) {
            String nomMembre = "Membre ID " + membreSelectionne.getId();
            if (membreSelectionne.getClient() != null) {
                nomMembre = membreSelectionne.getClient().getPrenom() + " " + membreSelectionne.getClient().getNom();
            }

            int confirmation = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le membre :\n" + nomMembre + " ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    membreService.supprimer(membreSelectionne);
                    JOptionPane.showMessageDialog(this,
                        "Membre supprimé avec succès!",
                        "Suppression réussie",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recharger les donn��es
                    loadData();
                    membreSelectionne = null;

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
        return membreSelectionne != null;
    }

    public Membre getMembreSelectionne() {
        return membreSelectionne;
    }

    /**
     * Vérifie si un client est déjà membre
     * @param client Le client à vérifier
     * @return true s'il est déjà membre, false sinon
     */
    private boolean estDejaMembe(Client client) {
        return membres.stream()
            .anyMatch(membre -> membre.getClient() != null && membre.getClient().getId().equals(client.getId()));
    }

    /**
     * Méthode utilitaire pour renouveler l'inscription d'un membre
     */
    public void renouvellerInscription() {
        if (membreSelectionne != null) {
            String message = "Voulez-vous renouveler l'inscription du membre ";
            if (membreSelectionne.getClient() != null) {
                message += membreSelectionne.getClient().getPrenom() + " " + membreSelectionne.getClient().getNom();
            } else {
                message += "ID " + membreSelectionne.getId();
            }
            message += " à la date d'aujourd'hui ?";

            int confirmation = JOptionPane.showConfirmDialog(this,
                message,
                "Renouvellement d'inscription",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    membreSelectionne.setDateInscription(LocalDateTime.now());
                    membreService.modifier(membreSelectionne);

                    JOptionPane.showMessageDialog(this,
                        "Inscription renouvelée avec succès!",
                        "Renouvellement réussi",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recharger les données
                    loadData();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du renouvellement: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Méthode utilitaire pour afficher les détails d'un membre
     */
    public void afficherDetails() {
        if (membreSelectionne != null) {
            StringBuilder details = new StringBuilder();
            details.append("=== DÉTAILS DU MEMBRE ===\n\n");
            details.append("ID Membre: ").append(membreSelectionne.getId()).append("\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
            details.append("Date d'inscription: ");
            if (membreSelectionne.getDateInscription() != null) {
                details.append(membreSelectionne.getDateInscription().format(formatter));
            } else {
                details.append("Non définie");
            }
            details.append("\n\n");

            if (membreSelectionne.getClient() != null) {
                Client client = membreSelectionne.getClient();
                details.append("=== INFORMATIONS CLIENT ===\n\n");
                details.append("Nom complet: ").append(client.getPrenom()).append(" ").append(client.getNom()).append("\n");
                details.append("Email: ").append(client.getEmail() != null ? client.getEmail() : "Non défini").append("\n");
                details.append("Date de naissance: ");
                if (client.getDateNaissance() != null) {
                    details.append(client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    details.append("Non définie");
                }
            } else {
                details.append("=== AUCUN CLIENT ASSOCIÉ ===");
            }

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this,
                scrollPane,
                "Détails du membre",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Obtient les statistiques des membres
     */
    public void afficherStatistiques() {
        int totalMembres = membres.size();
        int membresAvecClient = (int) membres.stream()
            .filter(membre -> membre.getClient() != null)
            .count();
        int membresSansClient = totalMembres - membresAvecClient;

        // Calcul des inscriptions par mois (exemple simplifié)
        LocalDateTime maintenant = LocalDateTime.now();
        int inscriptionsCeMois = (int) membres.stream()
            .filter(membre -> membre.getDateInscription() != null)
            .filter(membre -> membre.getDateInscription().getMonth() == maintenant.getMonth())
            .filter(membre -> membre.getDateInscription().getYear() == maintenant.getYear())
            .count();

        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTIQUES DES MEMBRES ===\n\n");
        stats.append("Total des membres: ").append(totalMembres).append("\n");
        stats.append("Membres avec client associé: ").append(membresAvecClient).append("\n");
        stats.append("Membres sans client: ").append(membresSansClient).append("\n");
        stats.append("Inscriptions ce mois: ").append(inscriptionsCeMois).append("\n");

        if (totalMembres > 0) {
            double pourcentageAvecClient = (double) membresAvecClient / totalMembres * 100;
            stats.append("Taux d'association client: ").append(String.format("%.1f%%", pourcentageAvecClient));
        }

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 200));

        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Statistiques des membres",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Calcule le statut d'un abonnement en fonction des dates
     * @param abonnement L'abonnement à évaluer
     * @return Le statut calculé : "Actif", "Expiré", "À venir", ou "Non défini"
     */
    private String calculerStatutAbonnement(entite.Abonnement abonnement) {
        LocalDateTime maintenant = LocalDateTime.now();

        if (abonnement.getDateDebut() == null || abonnement.getDateFin() == null) {
            return "Non défini";
        }

        if (maintenant.isBefore(abonnement.getDateDebut())) {
            return "À venir";
        } else if (maintenant.isAfter(abonnement.getDateFin())) {
            return "Expiré";
        } else {
            return "Actif";
        }
    }
}
