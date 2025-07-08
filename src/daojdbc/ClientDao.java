/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;
import entite.Client;
import util.Connexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yaod
 */
public class ClientDao {
    
    public void ajouter(Client client) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO client(nom, prenom, date_naissance, email) VALUES ('"
                + client.getNom() + "', '"
                + client.getPrenom() + "', '"
                + client.getDateNaissance() + "', '"
                + client.getEmail() + "');";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void modifier(Client client) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE client SET "
                + "nom = '" + client.getNom() + "', "
                + "prenom = '" + client.getPrenom() + "', "
                + "date_naissance = '" + client.getDateNaissance() + "', "
                + "email = '" + client.getEmail() + "' "
                + "WHERE id = " + client.getId() + ";";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM client WHERE id = " + id + ";";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
    
    public void supprimer(Client client) {
    Connection session = Connexion.getSessionV2();
    String sql = "DELETE FROM client WHERE id = " + client.getId();

    try {
        Statement statement = session.createStatement();
        statement.execute(sql);
    } catch (SQLException e) {
        System.out.println("Erreur lors de la suppression du client : " + e.getMessage());
    }
}

    public Client trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM client WHERE id = " + id + ";";
        Client client = null;
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                client = new Client();
                client.setId(result.getInt("id"));
                client.setNom(result.getString("nom"));
                client.setPrenom(result.getString("prenom"));
                client.setDateNaissance(result.getTimestamp("date_naissance").toLocalDateTime());
                client.setEmail(result.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return client;
    }

    public List<Client> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM client;";
        List<Client> clients = new ArrayList<>();
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Client client = new Client();
                client.setId(result.getInt("id"));
                client.setNom(result.getString("nom"));
                client.setPrenom(result.getString("prenom"));
                client.setDateNaissance(result.getTimestamp("date_naissance").toLocalDateTime());
                client.setEmail(result.getString("email"));
                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return clients;
    }
    
}
