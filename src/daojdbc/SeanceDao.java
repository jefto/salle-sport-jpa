/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Seance;
import entite.Membre;
import entite.Salle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.Connexion;

/**
 *
 * @author yaod
 */
public class SeanceDao {
    
    // Format SQL standard pour DATETIME
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void ajouter(Seance seance) {
        Connection session = Connexion.getSessionV2();

        String dateDebutStr = seance.getDateDebut().format(formatter);
        String dateFinStr = seance.getDateFin().format(formatter);

        String sql = "INSERT INTO seance (date_debut, date_fin, membre_id, salle_id) VALUES ("
                + "'" + dateDebutStr + "', "
                + "'" + dateFinStr + "', "
                + seance.getMembre().getId() + ", "
                + seance.getSalle().getId()
                + ")";

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la séance : " + e.getMessage());
        }
    }

    public void modifier(Seance seance) {
        Connection session = Connexion.getSessionV2();

        String dateDebutStr = seance.getDateDebut().format(formatter);
        String dateFinStr = seance.getDateFin().format(formatter);

        String sql = "UPDATE seance SET "
                + "date_debut = '" + dateDebutStr + "', "
                + "date_fin = '" + dateFinStr + "', "
                + "membre_id = " + seance.getMembre().getId() + ", "
                + "salle_id = " + seance.getSalle().getId() + " "
                + "WHERE id = " + seance.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la séance : " + e.getMessage());
        }
    }

    public void supprimer(Seance seance) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM seance WHERE id = " + seance.getId();

        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la séance : " + e.getMessage());
        }
    }

    public Seance trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM seance WHERE id = " + id;
        Seance seance = new Seance();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            if (rs.next()) {
                seance.setId(rs.getInt("id"));

                // Parsing string date SQL vers LocalDateTime
                String dateDebut = rs.getString("date_debut");
                String dateFin = rs.getString("date_fin");
                seance.setDateDebut(LocalDateTime.parse(dateDebut, formatter));
                seance.setDateFin(LocalDateTime.parse(dateFin, formatter));

                // Récupérer les objets Membre et Salle associés (seulement avec leur id ici)
                Membre membre = new Membre();
                membre.setId(rs.getInt("membre_id"));
                seance.setMembre(membre);

                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                seance.setSalle(salle);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de la séance : " + e.getMessage());
        }

        return seance;
    }

    public List<Seance> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM seance";
        List<Seance> liste = new ArrayList<>();

        try {
            Statement statement = session.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));

                String dateDebut = rs.getString("date_debut");
                String dateFin = rs.getString("date_fin");
                seance.setDateDebut(LocalDateTime.parse(dateDebut, formatter));
                seance.setDateFin(LocalDateTime.parse(dateFin, formatter));

                Membre membre = new Membre();
                membre.setId(rs.getInt("membre_id"));
                seance.setMembre(membre);

                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                seance.setSalle(salle);

                liste.add(seance);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séances : " + e.getMessage());
        }

        return liste;
    }
    
}
