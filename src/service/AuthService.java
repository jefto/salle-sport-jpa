package service;

import dao.ClientDao;
import entite.Client;
import java.sql.SQLException;
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
            // Utiliser JPA au lieu de JDBC
            Client client = clientDao.findByEmailJPA(email);

            if (client == null) {
                return new LoginResult(false, "EMAIL_NOT_FOUND", null);
            }

            if (!password.equals(client.getMotDePasse())) {
                return new LoginResult(false, "WRONG_PASSWORD", null);
            }

            return new LoginResult(true, "SUCCESS", client);

        } catch (RuntimeException e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
            e.printStackTrace();

            // Vérifier si l'erreur est liée à la base de données
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                System.err.println("Erreur SQL: " + sqlEx.getMessage() + ", Code: " + sqlEx.getErrorCode());

                // Fournir un message d'erreur plus spécifique selon le code d'erreur SQL
                if (sqlEx.getErrorCode() == 0) {
                    return new LoginResult(false, "DATABASE_CONNECTION_ERROR", null);
                } else if (sqlEx.getErrorCode() == 1045) {
                    return new LoginResult(false, "DATABASE_ACCESS_DENIED", null);
                } else if (sqlEx.getErrorCode() == 1049) {
                    return new LoginResult(false, "DATABASE_NOT_FOUND", null);
                }
            } else if (cause instanceof javax.persistence.PersistenceException) {
                // Gérer les exceptions JPA
                System.err.println("Erreur JPA: " + cause.getMessage());
                return new LoginResult(false, "DATABASE_ERROR", null);
            }

            return new LoginResult(false, "DATABASE_ERROR", null);
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
            return new LoginResult(false, "UNEXPECTED_ERROR", null);
        }
    }

    /**
     * Inscrit un nouveau client
     */
    public LoginResult register(String nom, String prenom, String email, String password, Date dateNaissance) {
        try {
            // Vérifier si l'email existe déjà (avec JPA)
            Client existingClient = clientDao.findByEmailJPA(email);
            if (existingClient != null) {
                return new LoginResult(false, "EMAIL_ALREADY_EXISTS", null);
            }

            // Convertir Date en LocalDateTime
            LocalDateTime dateNaissanceLD = dateNaissance.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            // Créer le nouveau client
            Client newClient = new Client(nom, prenom, dateNaissanceLD, email, password);

            // Sauvegarder avec JPA
            Client savedClient = clientDao.saveWithJPA(newClient);

            return new LoginResult(true, "SUCCESS", savedClient);

        } catch (RuntimeException e) {
            System.err.println("Erreur d'inscription: " + e.getMessage());
            e.printStackTrace();

            // Vérifier si l'erreur est liée à la base de données
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                System.err.println("Erreur SQL: " + sqlEx.getMessage() + ", Code: " + sqlEx.getErrorCode());

                // Fournir un message d'erreur plus spécifique selon le code d'erreur SQL
                if (sqlEx.getErrorCode() == 0) {
                    return new LoginResult(false, "DATABASE_CONNECTION_ERROR", null);
                } else if (sqlEx.getErrorCode() == 1045) {
                    return new LoginResult(false, "DATABASE_ACCESS_DENIED", null);
                } else if (sqlEx.getErrorCode() == 1049) {
                    return new LoginResult(false, "DATABASE_NOT_FOUND", null);
                } else if (sqlEx.getErrorCode() == 1062) {
                    return new LoginResult(false, "EMAIL_ALREADY_EXISTS", null);
                }
            } else if (cause instanceof javax.persistence.PersistenceException) {
                // Gérer les exceptions JPA
                System.err.println("Erreur JPA: " + cause.getMessage());

                // Vérifier si c'est une violation de contrainte d'unicité (email déjà existant)
                if (cause.getMessage().contains("unique") || cause.getMessage().contains("duplicate")) {
                    return new LoginResult(false, "EMAIL_ALREADY_EXISTS", null);
                }

                return new LoginResult(false, "DATABASE_ERROR", null);
            }

            return new LoginResult(false, "DATABASE_ERROR", null);
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de l'inscription: " + e.getMessage());
            e.printStackTrace();
            return new LoginResult(false, "UNEXPECTED_ERROR", null);
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
