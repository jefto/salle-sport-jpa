/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.TicketDao;
import entite.Ticket;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class TicketService extends GenericService<Ticket, Integer>{
    public TicketService(){
        super(new TicketDao());
    }
    private TicketDao dao = new TicketDao();

    @Override
    public List<Ticket> listerTous() {
        return dao.listerTous();
    }
    
}
