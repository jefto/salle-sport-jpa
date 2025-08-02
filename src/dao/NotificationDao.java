package dao;

import entite.Membre;
import entite.Notification;

public class NotificationDao extends GenericDao<Notification, Integer>{
    public NotificationDao() {
        super();
        this.classEntity = Notification.class;
        this.PrimaryKeyName = "id";
    }
}
