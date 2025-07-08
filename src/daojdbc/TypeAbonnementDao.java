/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.TypeAbonnement;
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
public class TypeAbonnementDao {

    public void ajouter(TypeAbonnement typeAbonnement) {
        //Connection session =  Connexion.getInstance().getSession();
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO types_abonnement(code,  montant, libelle) "
                + "VALUES("
                + "'" + typeAbonnement.getCode() + "',"
                + typeAbonnement.getMontant()+ ","
                + "'" + typeAbonnement.getLibelle() + "'"
                + ");";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

    }

    public void modifier(TypeAbonnement typeAbonnement) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE types_abonnement SET "
                + "montant = " + typeAbonnement.getMontant() + ", "
                + "libelle = '" + typeAbonnement.getLibelle() + "' "
                + "WHERE code = '" + typeAbonnement.getCode() + "';";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

    }

    public void supprimer(TypeAbonnement typeAbonnement) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM types_abonnement WHERE code = '" + typeAbonnement.getCode() + "';";
        try {
            Statement statement = session.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public TypeAbonnement trouver(String code) {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM types_abonnement WHERE code = '" + code + "';";
        TypeAbonnement typeAbonnement = new TypeAbonnement();
        try {
            Statement statement = session.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                typeAbonnement.setCode(resultSet.getString("code"));
                typeAbonnement.setLibelle(resultSet.getString("libelle"));
                typeAbonnement.setMontant(resultSet.getInt("montant"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return typeAbonnement;
    }

    public List<TypeAbonnement> listerTous() {
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM types_abonnement;";
        List<TypeAbonnement> abonnements = new ArrayList<>();
        try {
            Statement statement = session.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setCode(resultSet.getString("code"));
                typeAbonnement.setLibelle(resultSet.getString("libelle"));
                typeAbonnement.setMontant(resultSet.getInt("montant"));
                abonnements.add(typeAbonnement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return abonnements;
    }

}
