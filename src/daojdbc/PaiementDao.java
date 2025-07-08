/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daojdbc;

import entite.Paiement;
import entite.MoyenDePaiement;
import entite.Abonnement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.Connexion;

/**
 *
 * @author yaod
 */
public class PaiementDao {
    
    public void ajouter(Paiement paiement) {
        Connection session = Connexion.getSessionV2();
        String sql = "INSERT INTO paiements(date_paiement, montant, moyen_paiement_id, abonnement_id) "
                   + "VALUES("
                   + "'" + Timestamp.valueOf(paiement.getDateDePaiement()) + "', "
                   + paiement.getMontant() + ", "
                   + paiement.getMoyenDePaiement().getId() + ", "
                   + paiement.getAbonement().getId()
                   + ");";
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
    
    public Paiement trouver(int id) {
    Paiement paiement = null;
    Connection session = Connexion.getSessionV2();
    String sql = "SELECT * FROM paiements WHERE id = " + id + ";";
    try {
        Statement statement = session.createStatement();
        ResultSet result = statement.executeQuery(sql);
        if (result.next()) {
            paiement = new Paiement();
            paiement.setId(result.getInt("id"));
            paiement.setDateDePaiement(result.getTimestamp("date_paiement").toLocalDateTime());
            paiement.setMontant(result.getInt("montant"));

            MoyenDePaiement moyen = new MoyenDePaiement();
            moyen.setId(result.getInt("moyen_paiement_id"));
            paiement.setMoyenDePaiement(moyen);

            Abonnement abonnement = new Abonnement();
            abonnement.setId(result.getInt("abonnement_id"));
            paiement.setAbonement(abonnement);
        }
    } catch (SQLException e) {
        System.out.println("Erreur : " + e.getMessage());
    }

    return paiement;
}

    public void modifier(Paiement paiement) {
        Connection session = Connexion.getSessionV2();
        String sql = "UPDATE paiements SET "
                   + "date_paiement = '" + Timestamp.valueOf(paiement.getDateDePaiement()) + "', "
                   + "montant = " + paiement.getMontant() + ", "
                   + "moyen_paiement_id = " + paiement.getMoyenDePaiement().getId() + ", "
                   + "abonnement_id = " + paiement.getAbonement().getId()
                   + " WHERE date_paiement = '" + Timestamp.valueOf(paiement.getDateDePaiement()) + "';"; // si clé primaire
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void supprimer(Paiement paiement) {
        Connection session = Connexion.getSessionV2();
        String sql = "DELETE FROM paiements WHERE date_paiement = '" + Timestamp.valueOf(paiement.getDateDePaiement()) + "';";
        try {
            Statement statement = session.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public List<Paiement> listerTous() {
        List<Paiement> paiements = new ArrayList<>();
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM paiements;";
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Paiement paiement = new Paiement();
                paiement.setDateDePaiement(result.getTimestamp("date_paiement").toLocalDateTime());
                paiement.setMontant(result.getInt("montant"));

                MoyenDePaiement moyen = new MoyenDePaiement();
                moyen.setId(result.getInt("moyen_paiement_id"));
                paiement.setMoyenDePaiement(moyen); // tu peux charger + tard avec MoyenDePaiementDao si tu veux plus de détails

                Abonnement abonnement = new Abonnement();
                abonnement.setId(result.getInt("abonnement_id"));
                paiement.setAbonement(abonnement);

                paiements.add(paiement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return paiements;
    }

    public Paiement trouverParDate(LocalDateTime datePaiement) {
        Paiement paiement = null;
        Connection session = Connexion.getSessionV2();
        String sql = "SELECT * FROM paiements WHERE date_paiement = '" + Timestamp.valueOf(datePaiement) + "';";
        try {
            Statement statement = session.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                paiement = new Paiement();
                paiement.setDateDePaiement(result.getTimestamp("date_paiement").toLocalDateTime());
                paiement.setMontant(result.getInt("montant"));

                MoyenDePaiement moyen = new MoyenDePaiement();
                moyen.setId(result.getInt("moyen_paiement_id"));
                paiement.setMoyenDePaiement(moyen);

                Abonnement abonnement = new Abonnement();
                abonnement.setId(result.getInt("abonnement_id"));
                paiement.setAbonement(abonnement);
            }
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        return paiement;
    }
    
}
