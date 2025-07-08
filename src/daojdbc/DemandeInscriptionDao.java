/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Client;
import entite.DemandeInscription;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import util.Connexion;

/**
 *
 * @author yaod
 */
public class DemandeInscriptionDao {

    public void ajouter(DemandeInscription demande) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO demande_inscription(date_de_demande, date_de_traitement, client_id) VALUES ('"
                + Timestamp.valueOf(demande.getDateDeDemande()) + "', "
                + (demande.getDateDeTraitement() != null ? ("'" + Timestamp.valueOf(demande.getDateDeTraitement()) + "'") : "NULL") + ", "
                + demande.getClient().getId()
                + ");";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void modifier(DemandeInscription demande) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE demande_inscription SET "
                + "date_de_demande = '" + Timestamp.valueOf(demande.getDateDeDemande()) + "', "
                + "date_de_traitement = " + (demande.getDateDeTraitement() != null ? ("'" + Timestamp.valueOf(demande.getDateDeTraitement()) + "'") : "NULL") + ", "
                + "client_id = " + demande.getClient().getId() + " "
                + "WHERE id = " + demande.getId() + ";";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
    Connection session = Connexion.getSessionV2();
    String sql = "DELETE FROM demande_inscription WHERE id = " + id + ";";
    try {
        Statement statement = session.createStatement();
        statement.execute(sql);
    } catch (SQLException e) {
        System.out.println("Erreur : " + e.getMessage());
    }
}

    public DemandeInscription trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM demande_inscription WHERE id = " + id + ";";
        DemandeInscription demande = null;
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                demande = new DemandeInscription();
                demande.setId(result.getInt("id"));
                demande.setDateDeDemande(result.getTimestamp("date_de_demande").toLocalDateTime());

                Timestamp traitement = result.getTimestamp("date_de_traitement");
                if (traitement != null) {
                    demande.setDateDeTraitement(traitement.toLocalDateTime());
                }

               Client client = new Client();
                client.setId(result.getInt("client_id"));
                demande.setClient(client);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return demande;
    }

    public List<DemandeInscription> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM demande_inscription;";
        List<DemandeInscription> demandes = new ArrayList<>();
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                DemandeInscription demande = new DemandeInscription();
                demande.setId(result.getInt("id"));
                demande.setDateDeDemande(result.getTimestamp("date_de_demande").toLocalDateTime());

                Timestamp traitement = result.getTimestamp("date_de_traitement");
                if (traitement != null) {
                    demande.setDateDeTraitement(traitement.toLocalDateTime());
                }

                Client client = new Client();
                client.setId(result.getInt("client_id"));
                demande.setClient(client);
                demandes.add(demande);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return demandes;
    }
}