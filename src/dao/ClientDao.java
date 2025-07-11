package dao;

import entite.Client;
import util.Connexion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientDao extends GenericDao<Client, Integer> {

    public ClientDao() {
        super();
        this.classEntity = Client.class;
        this.PrimaryKeyName = "id"; // ou "id_client" selon votre mapping
    }

    /**
     * Trouve un client par son email
     * @param email Email du client
     * @return Client trouvé ou null si non trouvé
     */
    public Client findByEmail(String email) {
        String sql = "SELECT * FROM client WHERE email = ?";
        
        try (Connection conn = Connexion.getSessionV2();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
                client.setMotDePasse(rs.getString("mot_de_passe"));
                
                // Conversion Timestamp vers LocalDateTime
                Timestamp timestamp = rs.getTimestamp("date_naissance");
                if (timestamp != null) {
                    client.setDateNaissance(timestamp.toLocalDateTime());
                }
                
                return client;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Sauvegarde un client en base de données
     * @param client Client à sauvegarder
     * @return Client sauvegardé avec l'ID généré
     */
    public Client save(Client client) {
        String sql = "INSERT INTO client (nom, prenom, email, mot_de_passe, date_naissance) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Connexion.getSessionV2();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getPrenom());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getMotDePasse());
            stmt.setTimestamp(5, Timestamp.valueOf(client.getDateNaissance()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du client, aucune ligne affectée.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création du client, aucun ID obtenu.");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return client;
    }

    /**
     * Alternative utilisant JPA pour la sauvegarde
     * @param client Client à sauvegarder
     * @return Client sauvegardé
     */
    public Client saveWithJPA(Client client) {
        try {
            if (client.getId() == null) {
                // Nouveau client
                ajouter(client);
            } else {
                // Client existant
                modifier(client);
            }
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Trouve un client par email en utilisant JPA
     * @param email Email du client
     * @return Client trouvé ou null
     */
    public Client findByEmailJPA(String email) {
        try {
            String jpql = "SELECT c FROM Client c WHERE c.email = :email";
            return em.createQuery(jpql, Client.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Aucun résultat trouvé
        }
    }
}