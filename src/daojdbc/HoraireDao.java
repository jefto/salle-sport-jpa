/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Horaire;
import entite.Salle;
import util.Connexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author yaod
 */
public class HoraireDao {
    
     public void ajouter(Horaire horaire, Salle salle) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO horaire(debut, fin, salle_code) VALUES ("
                + "'" + horaire.getDebut().toLocalTime() + "', "
                + "'" + horaire.getFin().toLocalTime() + "', "
                + "'" + salle.getLibelle() + "'"
                + ");";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void modifier(Horaire horaire, Salle salle) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE horaire SET "
                + "debut = '" + horaire.getDebut().toLocalTime() + "', "
                + "fin = '" + horaire.getFin().toLocalTime() + "', "
                + "salle_code = '" + salle.getLibelle() + "' "
                + "WHERE id = " + horaire.getId() + ";";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM horaire WHERE id = " + id + ";";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public Horaire trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM horaire WHERE id = " + id + ";";
        Horaire horaire = null;
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                horaire = new Horaire();
                horaire.setId(result.getInt("id"));
                
                // On suppose la date du jour pour composer un LocalDateTime
                LocalDate today = LocalDate.now();
                horaire.setDebut(LocalDateTime.of(today, result.getTime("debut").toLocalTime()));
                horaire.setFin(LocalDateTime.of(today, result.getTime("fin").toLocalTime()));

                // Créer la salle liée
                Salle salle = new Salle();
                salle.setLibelle(result.getString("salle_code"));
                horaire.setSalle(salle); 
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return horaire;
    }

    public List<Horaire> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM horaire;";
        List<Horaire> horaires = new ArrayList<>();
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Horaire horaire = new Horaire();
                horaire.setId(result.getInt("id"));

                LocalDate today = LocalDate.now();
                horaire.setDebut(LocalDateTime.of(today, result.getTime("debut").toLocalTime()));
                horaire.setFin(LocalDateTime.of(today, result.getTime("fin").toLocalTime()));

                Salle salle = new Salle();
                salle.setLibelle(result.getString("salle_code"));
                horaire.setSalle(salle); 

                horaires.add(horaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return horaires;
    }
    
}
