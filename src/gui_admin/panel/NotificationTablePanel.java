package gui_admin.panel;

import entite.Notification;
import service.NotificationService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationTablePanel extends JPanel implements CrudOperationsInterface {

    private List<Notification> notifications;
    private Notification notificationSelectionnee;
    private NotificationService notificationService;
    private DefaultTableModel tableModel;
    private JTable table;

    public NotificationTablePanel() {
        this.setLayout(new BorderLayout());
        this.notificationService = new NotificationService();

        initializeTable();
        loadData();
    }

    private void initializeTable() {
        String[] colonnes = {"Sélection", "ID", "Date d'envoi", "Type", "Destinataire", "Description", "Lu"};

        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);   // Sélection
        table.getColumnModel().getColumn(1).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(2).setPreferredWidth(130);  // Date d'envoi
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Destinataire
        table.getColumnModel().getColumn(5).setPreferredWidth(250);  // Description
        table.getColumnModel().getColumn(6).setPreferredWidth(50);   // Lu

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
                        notificationSelectionnee = notifications.get(row);
                    } else {
                        notificationSelectionnee = null;
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
        notifications = notificationService.listerTous();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Notification notification : notifications) {
            tableModel.addRow(new Object[]{
                false,
                notification.getId(),
                notification.getDateEnvoi() != null ? notification.getDateEnvoi().format(formatter) : "Non définie",
                notification.getType() != null ? notification.getType() : "Non défini",
                notification.getDestinataire() != null ? notification.getDestinataire() : "Non défini",
                notification.getDescription() != null ? notification.getDescription() : "Aucune description",
                notification.getEstLu() != null ? notification.getEstLu() : false
            });
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie
        JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField dateEnvoiField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField destinataireField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        JCheckBox estLuCheckBox = new JCheckBox();

        // Pré-remplir la date avec l'heure actuelle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateEnvoiField.setText(LocalDateTime.now().format(formatter));

        formulaire.add(new JLabel("Date d'envoi (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateEnvoiField);
        formulaire.add(new JLabel("Type :"));
        formulaire.add(typeField);
        formulaire.add(new JLabel("Destinataire :"));
        formulaire.add(destinataireField);
        formulaire.add(new JLabel("Description :"));
        formulaire.add(new JScrollPane(descriptionArea));
        formulaire.add(new JLabel("Lu :"));
        formulaire.add(estLuCheckBox);

        int result = JOptionPane.showConfirmDialog(
            this,
            formulaire,
            "Ajouter une nouvelle notification",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String dateEnvoiText = dateEnvoiField.getText().trim();
            String type = typeField.getText().trim();
            String destinataire = destinataireField.getText().trim();
            String description = descriptionArea.getText().trim();
            boolean estLu = estLuCheckBox.isSelected();

            if (!dateEnvoiText.isEmpty() && !type.isEmpty() && !destinataire.isEmpty() && !description.isEmpty()) {
                try {
                    // Parser la date
                    LocalDateTime dateEnvoi = LocalDateTime.parse(dateEnvoiText, formatter);

                    Notification nouvelleNotification = new Notification(dateEnvoi, destinataire, description, type);
                    nouvelleNotification.setEstLu(estLu);
                    notificationService.ajouter(nouvelleNotification);

                    JOptionPane.showMessageDialog(this,
                        "Notification ajoutée avec succès!",
                        "Ajout réussi",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recharger les données
                    loadData();

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
        if (notificationSelectionnee != null) {
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField dateEnvoiField = new JTextField();
            JTextField typeField = new JTextField(notificationSelectionnee.getType() != null ? notificationSelectionnee.getType() : "");
            JTextField destinataireField = new JTextField(notificationSelectionnee.getDestinataire() != null ? notificationSelectionnee.getDestinataire() : "");
            JTextArea descriptionArea = new JTextArea(notificationSelectionnee.getDescription() != null ? notificationSelectionnee.getDescription() : "", 3, 20);
            JCheckBox estLuCheckBox = new JCheckBox();

            // Pré-remplir l'état "lu"
            estLuCheckBox.setSelected(notificationSelectionnee.getEstLu() != null ? notificationSelectionnee.getEstLu() : false);

            // Formatter et afficher la date existante
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (notificationSelectionnee.getDateEnvoi() != null) {
                dateEnvoiField.setText(notificationSelectionnee.getDateEnvoi().format(formatter));
            }

            formulaire.add(new JLabel("Date d'envoi (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateEnvoiField);
            formulaire.add(new JLabel("Type :"));
            formulaire.add(typeField);
            formulaire.add(new JLabel("Destinataire :"));
            formulaire.add(destinataireField);
            formulaire.add(new JLabel("Description :"));
            formulaire.add(new JScrollPane(descriptionArea));
            formulaire.add(new JLabel("Lu :"));
            formulaire.add(estLuCheckBox);

            int result = JOptionPane.showConfirmDialog(
                this,
                formulaire,
                "Modifier la notification: " + notificationSelectionnee.getType(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String dateEnvoiText = dateEnvoiField.getText().trim();
                String type = typeField.getText().trim();
                String destinataire = destinataireField.getText().trim();
                String description = descriptionArea.getText().trim();
                boolean estLu = estLuCheckBox.isSelected();

                if (!dateEnvoiText.isEmpty() && !type.isEmpty() && !destinataire.isEmpty() && !description.isEmpty()) {
                    try {
                        // Parser la date
                        LocalDateTime dateEnvoi = LocalDateTime.parse(dateEnvoiText, formatter);

                        // Mettre à jour les données de la notification sélectionnée
                        notificationSelectionnee.setDateEnvoi(dateEnvoi);
                        notificationSelectionnee.setType(type);
                        notificationSelectionnee.setDestinataire(destinataire);
                        notificationSelectionnee.setDescription(description);
                        notificationSelectionnee.setEstLu(estLu);

                        notificationService.modifier(notificationSelectionnee);

                        JOptionPane.showMessageDialog(this,
                            "Notification modifiée avec succès!",
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
        if (notificationSelectionnee != null) {
            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer cette notification ?\n" +
                "Type: " + notificationSelectionnee.getType() + "\n" +
                "Destinataire: " + notificationSelectionnee.getDestinataire(),
                "Confirmer la suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    notificationService.supprimer(notificationSelectionnee);
                    JOptionPane.showMessageDialog(this,
                        "Notification supprimée avec succès!",
                        "Suppression réussie",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recharger les données
                    loadData();
                    notificationSelectionnee = null;

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
        return notificationSelectionnee != null;
    }

    public Notification getNotificationSelectionnee() {
        return notificationSelectionnee;
    }
}
