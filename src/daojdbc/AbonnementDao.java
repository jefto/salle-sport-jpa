/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Abonnement;
import entite.Membre;
import entite.TypeAbonnement;
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
public class AbonnementDao {
    
    public void ajouter(Abonnement abonnement) {
        Connection session = Connexion.getSessionV2();

        String sql = "INSERT INTO abonnement (date_debut, date_fin, type_abonnement_id, membre_id) VALUES ("
                + "'" + abonnement.getDateDebut().toString() + "', "
                + "'" + abonnement.getDateFin().toString() + "', "
                + abonnement.getMembre().getId()
                + ")";

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'abonnement : " + e.getMessage());
        }
    }

    public void modifier(Abonnement abonnement) {
        Connection session = Connexion.getSessionV2();

        String sql = "UPDATE abonnement SET "
                + "date_debut = '" + abonnement.getDateDebut().toString() + "', "
                + "date_fin = '" + abonnement.getDateFin().toString() + "', "
                + "membre_id = " + abonnement.getMembre().getId()
                + " WHERE id = " + abonnement.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'abonnement : " + e.getMessage());
        }
    }

    public void supprimer(Abonnement abonnement) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM abonnement WHERE id = " + abonnement.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'abonnement : " + e.getMessage());
        }
    }

    public Abonnement trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM abonnement WHERE id = " + id;
        Abonnement abonnement = new Abonnement();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                abonnement.setId(rs.getInt("id"));
                abonnement.setDateDebut(LocalDateTime.parse(rs.getString("date_debut")));
                abonnement.setDateFin(LocalDateTime.parse(rs.getString("date_fin")));

                TypeAbonnement typeAb = new TypeAbonnement();
                abonnement.setTypeAbonnement(typeAb);

                // Il n'y a PAS de paiement_id dans la table abonnement → on ne met rien ici.

                Membre membre = new Membre();
                membre.setId(rs.getInt("membre_id"));
                abonnement.setMembre(membre);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'abonnement : " + e.getMessage());
        }

        return abonnement;
    }

    public List<Abonnement> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM abonnement";
        List<Abonnement> abonnements = new ArrayList<>();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setDateDebut(LocalDateTime.parse(rs.getString("date_debut")));
                abonnement.setDateFin(LocalDateTime.parse(rs.getString("date_fin")));

                TypeAbonnement typeAb = new TypeAbonnement();
                abonnement.setTypeAbonnement(typeAb);

                // Même chose ici, pas besoin de créer un paiement.

                Membre membre = new Membre();
                membre.setId(rs.getInt("membre_id"));
                abonnement.setMembre(membre);

                abonnements.add(abonnement);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des abonnements : " + e.getMessage());
        }

        return abonnements;
    } 
    
}
