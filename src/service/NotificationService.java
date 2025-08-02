package service;

import dao.NotificationDao;
import entite.Notification;

import java.util.List;

public class NotificationService extends GenericService<entite.Notification, Integer>{
    private NotificationDao dao;

    public NotificationService() {
        super(new NotificationDao());
        this.dao = new NotificationDao();
    }

    @Override
    public List<Notification> listerTous() {
        return dao.listerTous();
    }

}
