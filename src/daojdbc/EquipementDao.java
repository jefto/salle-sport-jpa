/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Equipement;
import entite.Salle;
import util.Connexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yaod
 */
public class EquipementDao {
    

    public void ajouter(Equipement equipement) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO equipements(code, description, salle_code) VALUES ("
                + "'" + equipement.getLibelle() + "', "
                + "'" + equipement.getDescription() + "', "
                + "'" + equipement.getSalle().getLibelle() + "'"
                + ");";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void modifier(Equipement equipement) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE equipements SET "
                + "description = '" + equipement.getDescription() + "', "
                + "salle_code = '" + equipement.getSalle().getLibelle() + "' "
                + "WHERE code = '" + equipement.getLibelle() + "';";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM equipements WHERE code = '" + id + "';";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public Equipement trouver(int id) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM equipements WHERE code = '" + id + "';";
        Equipement equipement = null;
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                equipement = new Equipement();
                equipement.setLibelle(result.getString("code"));
                equipement.setDescription(result.getString("description"));

                Salle salle = new Salle();
                salle.setLibelle(result.getString("salle_code"));
                equipement.setSalle(salle);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return equipement;
    }

    public List<Equipement> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM equipements;";
        List<Equipement> equipements = new ArrayList<>();
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Equipement equipement = new Equipement();
                equipement.setLibelle(result.getString("code"));
                equipement.setDescription(result.getString("description"));

                Salle salle = new Salle();
                salle.setLibelle(result.getString("salle_code"));
                equipement.setSalle(salle);

                equipements.add(equipement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return equipements;
    }
}
