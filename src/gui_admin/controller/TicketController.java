package gui_admin.controller;

import entite.Ticket;
import gui_admin.view.tickets.Edit;
import service.TicketService;

public class TicketController extends GenericCrudController{
    public TicketController(){
        Ticket ticket = new Ticket();
        Edit edit = new Edit(ticket);
        super(new TicketService(), edit);
    }
}
