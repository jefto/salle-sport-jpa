/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Ticket;
import entite.Client;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import util.Connexion;

/**
 *
 * @author yaod
 */
public class TicketDao {
    
    public void ajouter(Ticket ticket) {
        Connection session = Connexion.getSessionV2();

        String sql = "INSERT INTO ticket (nombre_de_seance, montant, client_id) VALUES ("
                + ticket.getNombreDeSeance() + ", "
                + ticket.getMontant() + ", "
                + ticket.getClient().getId()
                + ")";

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du ticket : " + e.getMessage());
        }
    }

    public void modifier(Ticket ticket) {
        Connection session = Connexion.getSessionV2();

        String sql = "UPDATE ticket SET "
                + "nombre_de_seance = " + ticket.getNombreDeSeance() + ", "
                + "montant = " + ticket.getMontant() + ", "
                + "client_id = " + ticket.getClient().getId() + " "
                + "WHERE id = " + ticket.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du ticket : " + e.getMessage());
        }
    }

    public void supprimer(Ticket ticket) {
        Connection session = Connexion.getSessionV2();

        String sql = "DELETE FROM ticket WHERE id = " + ticket.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du ticket : " + e.getMessage());
        }
    }

    public Ticket trouver(int id) {
        Connection session = Connexion.getSessionV2();

        String sql = "SELECT * FROM ticket WHERE id = " + id;
        Ticket ticket = new Ticket();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                ticket.setId(rs.getInt("id"));
                ticket.setNombreDeSeance(rs.getInt("nombre_de_seance"));
                ticket.setMontant(rs.getInt("montant"));

                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                ticket.setClient(client);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du ticket : " + e.getMessage());
        }

        return ticket;
    }

    public List<Ticket> listerTous() {
        Connection session = Connexion.getSessionV2();

        String sql = "SELECT * FROM ticket";
        List<Ticket> tickets = new ArrayList<>();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setNombreDeSeance(rs.getInt("nombre_de_seance"));
                ticket.setMontant(rs.getInt("montant"));

                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                ticket.setClient(client);

                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des tickets : " + e.getMessage());
        }

        return tickets;
    }
    
}
