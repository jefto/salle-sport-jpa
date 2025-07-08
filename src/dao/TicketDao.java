package dao;

import entite.Ticket;

public class TicketDao extends GenericDao<Ticket, Integer>{
    public TicketDao(){
        super();
        this.classEntity = Ticket.class;
        this.PrimaryKeyName = "id";
    }
}
