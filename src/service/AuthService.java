package service;

import dao.ClientDao;
import entite.Client;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AuthService {
    
    private static AuthService instance;
    private ClientDao clientDao;
    
    private AuthService() {
        clientDao = new ClientDao();
    }
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Authentifie un client avec email et mot de passe
     */
    public LoginResult authenticate(String email, String password) {
        try {
            // Utiliser soit JDBC soit JPA
            Client client = clientDao.findByEmail(email); // JDBC
            // ou : Client client = clientDao.findByEmailJPA(email); // JPA
            
            if (client == null) {
                return new LoginResult(false, "EMAIL_NOT_FOUND", null);
            }
            
            if (!password.equals(client.getMotDePasse())) {
                return new LoginResult(false, "WRONG_PASSWORD", null);
            }
            
            return new LoginResult(true, "SUCCESS", client);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "DATABASE_ERROR", null);
        }
    }
    
    /**
     * Inscrit un nouveau client
     */
    public LoginResult register(String nom, String prenom, String email, String password, Date dateNaissance) {
        try {
            // Vérifier si l'email existe déjà
            Client existingClient = clientDao.findByEmail(email);
            if (existingClient != null) {
                return new LoginResult(false, "EMAIL_ALREADY_EXISTS", null);
            }
            
            // Convertir Date en LocalDateTime
            LocalDateTime dateNaissanceLD = dateNaissance.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
            
            // Créer le nouveau client
            Client newClient = new Client(nom, prenom, dateNaissanceLD, email, password);
            
            // Sauvegarder (choisir l'approche)
            Client savedClient = clientDao.save(newClient); // JDBC
            // ou : Client savedClient = clientDao.saveWithJPA(newClient); // JPA
            
            return new LoginResult(true, "SUCCESS", savedClient);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "DATABASE_ERROR", null);
        }
    }
    
    /**
     * Classe pour encapsuler le résultat d'une authentification
     */
    public static class LoginResult {
        private boolean success;
        private String errorCode;
        private Client client;
        
        public LoginResult(boolean success, String errorCode, Client client) {
            this.success = success;
            this.errorCode = errorCode;
            this.client = client;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public Client getClient() {
            return client;
        }
    }
}