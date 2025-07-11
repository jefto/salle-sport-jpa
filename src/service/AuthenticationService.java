package service;

public class AuthenticationService {
    private static AuthenticationService instance;
    
    // Identifiants par défaut (à modifier selon vos besoins)
    private static final String ADMIN_USERNAME = "jefto";
    private static final String ADMIN_PASSWORD = "Jefto";
    
    private AuthenticationService() {}
    
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    public boolean authenticateAdmin(String username, String password) {
        // Vérification simple avec les identifiants en dur
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }
    
    public boolean authenticateClient(String username, String password) {
        // Logique d'authentification client
        return false;
    }
}