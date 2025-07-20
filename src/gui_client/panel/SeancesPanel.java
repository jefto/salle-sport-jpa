package gui_client.panel;

import entite.Seance;
import entite.Membre;
import entite.Client;
import entite.Salle;
import service.UserSessionManager;
import service.SeanceService;
import service.MembreService;
import service.NotificationManager;
import dao.SeanceDao;
import dao.MembreDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SeancesPanel extends JPanel {
    
    private SeanceService seanceService;
    private MembreService membreService;
    private SeanceDao seanceDao;
    private MembreDao membreDao;

    private JTable seancesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public SeancesPanel() {
        // Initialiser les services
        seanceService = new SeanceService();
        membreService = new MembreService();
        seanceDao = new SeanceDao();
        membreDao = new MembreDao();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Vérifier si l'utilisateur est connecté
        if (!UserSessionManager.getInstance().isLoggedIn()) {
            add(createNotLoggedInPanel(), BorderLayout.CENTER);
            return;
        }

        // Créer l'interface principale
        createMainInterface();

        // Charger les données
        loadSeances();
    }

    private JPanel createNotLoggedInPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("Veuillez vous connecter pour voir les séances", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.GRAY);

        panel.add(messageLabel, BorderLayout.CENTER);
        return panel;
    }

    private void createMainInterface() {
        // Titre principal
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titleLabel = new JLabel("Séances Disponibles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(74, 41, 0));

        refreshButton = new JButton("Actualiser");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadSeances());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Table des séances
        createSeancesTable();
    }

    private void createSeancesTable() {
        // Colonnes de la table
        String[] columnNames = {
            "Date/Heure Début",
            "Date/Heure Fin",
            "Salle",
            "Membres Inscrits",
            "Action"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Seule la colonne Action est éditable
            }
        };

        seancesTable = new JTable(tableModel);
        seancesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        seancesTable.setRowHeight(50);
        seancesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        seancesTable.getTableHeader().setBackground(new Color(74, 41, 0));
        seancesTable.getTableHeader().setForeground(Color.WHITE);

        // Renderer personnalisé pour les boutons
        seancesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        seancesTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Ajuster la largeur des colonnes
        seancesTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        seancesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        seancesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        seancesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        seancesTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(seancesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadSeances() {
        try {
            // Utiliser la méthode JPA correcte pour récupérer toutes les séances
            List<Seance> seances = seanceService.listerTous();
            tableModel.setRowCount(0); // Vider la table

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Seance seance : seances) {
                // Récupérer le nombre de membres inscrits à cette séance en utilisant la méthode générique
                List<Membre> membresInscrits = membreDao.listerPar("seance.id", seance.getId());
                int nombreMembres = membresInscrits.size();

                Object[] row = {
                    seance.getDateDebut().format(formatter),
                    seance.getDateFin().format(formatter),
                    seance.getSalle() != null ? seance.getSalle().getLibelle() : "Non définie",
                    nombreMembres + " membre(s)",
                    seance.getId() // L'ID sera utilisé par le bouton
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des séances : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(41, 128, 185));
                setForeground(Color.WHITE);
            } else {
                setBackground(new Color(52, 152, 219));
                setForeground(Color.WHITE);
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int seanceId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            Client currentUser = UserSessionManager.getInstance().getCurrentUser();
            Membre membre = membreDao.trouverPar("client.id", currentUser.getId());

            if (membre == null) {
                label = "Non membre";
                button.setBackground(Color.GRAY);
                button.setEnabled(false);
            } else if (membre.getSeance() != null) {
                label = "Déjà inscrit";
                button.setBackground(Color.ORANGE);
                button.setEnabled(false);
            } else {
                label = "S'inscrire";
                button.setBackground(new Color(46, 204, 113));
                button.setEnabled(true);
                seanceId = (Integer) value; // Récupérer l'ID de la séance
            }

            button.setText(label);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && button.isEnabled()) {
                inscrireASeance(seanceId);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void inscrireASeance(int seanceId) {
        try {
            Client currentUser = UserSessionManager.getInstance().getCurrentUser();

            // Vérifier si le client est membre
            Membre membre = membreDao.trouverPar("client.id", currentUser.getId());
            if (membre == null) {
                JOptionPane.showMessageDialog(this,
                    "Vous devez être membre pour vous inscrire à une séance.\nVeuillez d'abord souscrire à un abonnement.",
                    "Inscription requise",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Vérifier si le membre est déjà inscrit à une séance
            if (membre.getSeance() != null) {
                JOptionPane.showMessageDialog(this,
                    "Vous êtes déjà inscrit à une séance.\nVous ne pouvez être inscrit qu'à une seule séance à la fois.",
                    "Déjà inscrit",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Récupérer la séance
            Seance seance = seanceDao.trouver(seanceId);
            if (seance == null) {
                JOptionPane.showMessageDialog(this,
                    "Séance introuvable.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Inscrire le membre à la séance
            membre.setSeance(seance);
            membreService.modifier(membre);

            // Envoyer une notification
            envoyerNotificationInscription(membre, seance);

            // Actualiser la table
            loadSeances();

            JOptionPane.showMessageDialog(this,
                "Inscription réussie !\nVous avez été inscrit à la séance.\nConsultez vos notifications pour voir votre ticket.",
                "Inscription confirmée",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'inscription : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void envoyerNotificationInscription(Membre membre, Seance seance) {
        // Créer le message de notification
        String message = String.format(
            "Félicitations ! Vous êtes inscrit à la séance du %s de %s à %s en salle %s. Cliquez pour voir votre ticket.",
            seance.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            seance.getDateDebut().format(DateTimeFormatter.ofPattern("HH:mm")),
            seance.getDateFin().format(DateTimeFormatter.ofPattern("HH:mm")),
            seance.getSalle() != null ? seance.getSalle().getLibelle() : "Non définie"
        );

        // Ajouter la notification au système de notifications
        NotificationManager.getInstance().ajouterNotification(
            membre.getClient().getId(),
            "Inscription à une séance",
            message,
            "SEANCE_INSCRIPTION",
            seance.getId()
        );
    }

    /**
     * Méthode pour rafraîchir la page
     */
    public void refresh() {
        loadSeances();
    }
}
