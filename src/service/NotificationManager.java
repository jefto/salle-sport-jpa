package service;

import java.time.LocalDateTime;
import java.util.*;

public class NotificationManager {

    private static NotificationManager instance;
    private Map<Integer, List<Notification>> notifications; // clientId -> List<Notification>

    private NotificationManager() {
        notifications = new HashMap<>();
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Ajouter une notification pour un client
     */
    public void ajouterNotification(Integer clientId, String titre, String message, String type, Integer relatedId) {
        if (!notifications.containsKey(clientId)) {
            notifications.put(clientId, new ArrayList<>());
        }

        Notification notification = new Notification(
            titre,
            message,
            type,
            LocalDateTime.now(),
            false,
            relatedId
        );

        notifications.get(clientId).add(notification);
    }

    /**
     * Récupérer toutes les notifications d'un client
     */
    public List<Notification> getNotifications(Integer clientId) {
        return notifications.getOrDefault(clientId, new ArrayList<>());
    }

    /**
     * Marquer une notification comme lue
     */
    public void marquerCommeLue(Integer clientId, int notificationIndex) {
        List<Notification> clientNotifications = notifications.get(clientId);
        if (clientNotifications != null && notificationIndex < clientNotifications.size()) {
            clientNotifications.get(notificationIndex).setLue(true);
        }
    }

    /**
     * Obtenir le nombre de notifications non lues
     */
    public int getNombreNotificationsNonLues(Integer clientId) {
        List<Notification> clientNotifications = notifications.get(clientId);
        if (clientNotifications == null) return 0;

        return (int) clientNotifications.stream().filter(n -> !n.isLue()).count();
    }

    /**
     * Classe interne pour représenter une notification
     */
    public static class Notification {
        private String titre;
        private String message;
        private String type; // "SEANCE_INSCRIPTION", "MEMBRE_ACCEPTE", etc.
        private LocalDateTime dateCreation;
        private boolean lue;
        private Integer relatedId; // ID de la séance, demande, etc.

        public Notification(String titre, String message, String type, LocalDateTime dateCreation, boolean lue, Integer relatedId) {
            this.titre = titre;
            this.message = message;
            this.type = type;
            this.dateCreation = dateCreation;
            this.lue = lue;
            this.relatedId = relatedId;
        }

        // Getters et setters
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public LocalDateTime getDateCreation() { return dateCreation; }
        public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

        public boolean isLue() { return lue; }
        public void setLue(boolean lue) { this.lue = lue; }

        public Integer getRelatedId() { return relatedId; }
        public void setRelatedId(Integer relatedId) { this.relatedId = relatedId; }
    }
}
