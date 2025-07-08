/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.MoyenDePaiement;
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
public class MoyenDePaiementDao {

    public void ajouter(MoyenDePaiement moyenDePaiement) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO moyens_paiement(code, libelle) VALUES("
                + "'" + moyenDePaiement.getCode() + "',"
                + "'" + moyenDePaiement.getLibelle() + "');";
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur (ajouter moyen paiement) : " + e.getMessage());
        }
    }

    public void modifier(MoyenDePaiement moyenDePaiement) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE moyens_paiement SET "
                + "libelle = '" + moyenDePaiement.getLibelle() + "' "
                + "WHERE code = '" + moyenDePaiement.getCode() + "';";
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur (modifier moyen paiement) : " + e.getMessage());
        }
    }

    public void supprimer(String code) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM moyens_paiement WHERE code = '" + code + "';";
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur (supprimer moyen paiement) : " + e.getMessage());
        }
    }

    public MoyenDePaiement trouver(String code) {
        MoyenDePaiement moyenDePaiement = null;
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM moyens_paiement WHERE code = '" + code + "';";
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                moyenDePaiement = new MoyenDePaiement();
                moyenDePaiement.setId(result.getInt("id"));
                moyenDePaiement.setCode(result.getString("code"));
                moyenDePaiement.setLibelle(result.getString("libelle"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur (trouver moyen paiement) : " + e.getMessage());
        }
        return moyenDePaiement;
    }

    public List<MoyenDePaiement> listerTous() {
        List<MoyenDePaiement> liste = new ArrayList<>();
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM moyens_paiement;";
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                MoyenDePaiement moyen = new MoyenDePaiement();
                moyen.setId(result.getInt("id"));
                moyen.setCode(result.getString("code"));
                moyen.setLibelle(result.getString("libelle"));
                liste.add(moyen);
            }
        } catch (SQLException e) {
            System.out.println("Erreur (lister moyens paiement) : " + e.getMessage());
        }
        return liste;
    }
}
