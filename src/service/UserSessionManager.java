package service;

import entite.Client;

public class UserSessionManager {
    
    private static UserSessionManager instance;
    private Client currentUser;
    
    private UserSessionManager() {}
    
    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }
    
    /**
     * Connecte un utilisateur
     * @param client Client connecté
     */
    public void login(Client client) {
        this.currentUser = client;
    }
    
    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     * @return true si connecté, false sinon
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Récupère l'utilisateur connecté
     * @return Client connecté ou null si non connecté
     */
    public Client getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Récupère l'ID de l'utilisateur connecté
     * @return ID du client ou null si non connecté
     */
    public Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    /**
     * Récupère le nom complet de l'utilisateur connecté
     * @return Nom complet ou null si non connecté
     */
    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getPrenom() + " " + currentUser.getNom() : null;
    }
}