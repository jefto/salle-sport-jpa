package gui_client.panel;

import entite.Client;
import entite.Seance;
import entite.Membre;
import service.UserSessionManager;
import service.NotificationManager;
import service.SeanceService;
import dao.SeanceDao;
import dao.MembreDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsPanel extends JPanel {
    
    private NotificationManager notificationManager;
    private SeanceService seanceService;
    private SeanceDao seanceDao;
    private MembreDao membreDao;

    private JPanel notificationsListPanel;
    private JScrollPane scrollPane;

    public NotificationsPanel() {
        // Initialiser les services
        notificationManager = NotificationManager.getInstance();
        seanceService = new SeanceService();
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

        // Charger les notifications
        loadNotifications();
    }

    private JPanel createNotLoggedInPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel("Veuillez vous connecter pour voir vos notifications", SwingConstants.CENTER);
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

        JLabel titleLabel = new JLabel("Mes Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(74, 41, 0));

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadNotifications());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Panel pour la liste des notifications
        notificationsListPanel = new JPanel();
        notificationsListPanel.setLayout(new BoxLayout(notificationsListPanel, BoxLayout.Y_AXIS));
        notificationsListPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(notificationsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadNotifications() {
        notificationsListPanel.removeAll();

        Client currentUser = UserSessionManager.getInstance().getCurrentUser();
        List<NotificationManager.Notification> notifications = notificationManager.getNotifications(currentUser.getId());

        if (notifications.isEmpty()) {
            JLabel noNotificationsLabel = new JLabel("Aucune notification", SwingConstants.CENTER);
            noNotificationsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noNotificationsLabel.setForeground(Color.GRAY);
            noNotificationsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationsListPanel.add(Box.createVerticalGlue());
            notificationsListPanel.add(noNotificationsLabel);
            notificationsListPanel.add(Box.createVerticalGlue());
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                NotificationManager.Notification notification = notifications.get(i);
                JPanel notificationPanel = createNotificationPanel(notification, i);
                notificationsListPanel.add(notificationPanel);
                notificationsListPanel.add(Box.createVerticalStrut(10));
            }
        }

        notificationsListPanel.revalidate();
        notificationsListPanel.repaint();
    }

    private JPanel createNotificationPanel(NotificationManager.Notification notification, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(notification.isLue() ? Color.WHITE : new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(notification.isLue() ? Color.LIGHT_GRAY : new Color(52, 152, 219), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        // Contenu de la notification
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(panel.getBackground());

        // Titre et date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(panel.getBackground());

        JLabel titleLabel = new JLabel(notification.getTitre());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(74, 41, 0));

        JLabel dateLabel = new JLabel(notification.getDateCreation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        // Message
        JLabel messageLabel = new JLabel("<html><p style='width: 400px;'>" + notification.getMessage() + "</p></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Bouton d'action si c'est une notification de séance
        if ("SEANCE_INSCRIPTION".equals(notification.getType()) && notification.getRelatedId() != null) {
            JButton voirTicketButton = new JButton("Voir le ticket");
            voirTicketButton.setFont(new Font("Arial", Font.BOLD, 12));
            voirTicketButton.setBackground(new Color(46, 204, 113));
            voirTicketButton.setForeground(Color.WHITE);
            voirTicketButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            voirTicketButton.setFocusPainted(false);
            voirTicketButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            voirTicketButton.addActionListener(e -> {
                ouvrirTicket(notification.getRelatedId());
                marquerCommeLue(index);
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(panel.getBackground());
            buttonPanel.add(voirTicketButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        // Marquer comme lue au clic (pour les autres types de notifications)
        if (!"SEANCE_INSCRIPTION".equals(notification.getType())) {
            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    marquerCommeLue(index);
                }
            });
        }

        return panel;
    }

    private void marquerCommeLue(int index) {
        Client currentUser = UserSessionManager.getInstance().getCurrentUser();
        notificationManager.marquerCommeLue(currentUser.getId(), index);
        loadNotifications(); // Rafraîchir l'affichage
    }

    private void ouvrirTicket(Integer seanceId) {
        try {
            Client currentUser = UserSessionManager.getInstance().getCurrentUser();
            Membre membre = membreDao.trouverPar("client.id", currentUser.getId());
            Seance seance = seanceDao.trouver(seanceId);

            if (membre != null && seance != null) {
                new TicketDialog(membre, seance).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Impossible de récupérer les informations du ticket.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture du ticket : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Méthode pour rafraîchir les notifications
     */
    public void refresh() {
        loadNotifications();
    }
}