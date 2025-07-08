/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Membre;
import entite.Client;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.Connexion;

/**
 *
 * @author yaod
 */
public class MembreDao {
    
    public void ajouter(Membre membre) {
        Connection session = Connexion.getSessionV2();

        String sql = "INSERT INTO membre (date_inscription, client_id) VALUES ("
                + "'" + membre.getDateInscription().toString() + "', "
                + membre.getClient().getId()
                + ")";

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du membre : " + e.getMessage());
        }
    }

    public void modifier(Membre membre) {
        Connection session = Connexion.getSessionV2();

        String sql = "UPDATE membre SET "
                + "date_inscription = '" + membre.getDateInscription().toString() + "', "
                + "client_id = " + membre.getClient().getId()
                + " WHERE id = " + membre.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du membre : " + e.getMessage());
        }
    }

    public void supprimer(Membre membre) {
        Connection session = Connexion.getSessionV2();

        String sql = "DELETE FROM membre WHERE id = " + membre.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du membre : " + e.getMessage());
        }
    }

    public Membre trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM membre WHERE id = " + id;
        Membre membre = new Membre();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                membre.setId(rs.getInt("id"));
                membre.setDateInscription(LocalDateTime.parse(rs.getString("date_inscription")));

                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                membre.setClient(client);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du membre : " + e.getMessage());
        }

        return membre;
    }

    public List<Membre> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM membre";
        List<Membre> membres = new ArrayList<>();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Membre membre = new Membre();
                membre.setId(rs.getInt("id"));
                membre.setDateInscription(LocalDateTime.parse(rs.getString("date_inscription")));

                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                membre.setClient(client);

                membres.add(membre);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des membres : " + e.getMessage());
        }

        return membres;
    }
}

