/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author yaod
 */
public class Connexion {

    private Connection session;
    private static Connexion instance;
    private int maxRetries = 3;
    private int retryDelayMs = 1000; // 1 seconde entre les tentatives

    // Paramètres de connexion à la base de données
    private static final String URL = "jdbc:mariadb://localhost:3306/gym";
    private static final String LOGIN = "root";
    private static final String MOT_DE_PASSE = "goruaang12345$$$$$";

    static {
        instance = new Connexion();
    }

    private Connexion() {
        etablirConnexion();
    }

    private void etablirConnexion() {
        int attempts = 0;
        boolean connected = false;

        while (!connected && attempts < maxRetries) {
            attempts++;
            try {
                // Vérifier que le driver est chargé
                Class.forName("org.mariadb.jdbc.Driver");

                // Tentative de connexion
                session = DriverManager.getConnection(URL, LOGIN, MOT_DE_PASSE);

                // Vérifier que la connexion est valide
                if (session != null && session.isValid(2)) {
                    connected = true;
                    System.out.println("Connexion à la base de données établie avec succès (tentative " + attempts + ").");
                } else {
                    System.err.println("La connexion a été établie mais n'est pas valide (tentative " + attempts + ").");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Erreur: Driver JDBC introuvable - " + e.getMessage());
                e.printStackTrace();
                break; // Pas besoin de réessayer si le driver n'est pas trouvé
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données (tentative " + attempts + ") - " + e.getMessage());
                e.printStackTrace();

                // Afficher des informations supplémentaires sur l'erreur SQL
                System.err.println("Code d'erreur SQL: " + e.getErrorCode());
                System.err.println("État SQL: " + e.getSQLState());
            } catch (Exception e) {
                System.err.println("Erreur inattendue lors de la connexion (tentative " + attempts + ") - " + e.getMessage());
                e.printStackTrace();
            }

            // Si pas connecté et il reste des tentatives, attendre avant de réessayer
            if (!connected && attempts < maxRetries) {
                try {
                    System.out.println("Nouvelle tentative de connexion dans " + (retryDelayMs / 1000) + " seconde(s)...");
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!connected) {
            System.err.println("Échec de la connexion à la base de données après " + attempts + " tentative(s).");
        }
    }

    private boolean verifierConnexion() {
        try {
            if (session == null) {
                System.err.println("La connexion est null - aucune connexion n'a été établie");
                return false;
            }

            if (session.isClosed()) {
                System.err.println("La connexion est fermée");
                return false;
            }

            // Vérifier si la connexion est valide avec un timeout de 2 secondes
            boolean isValid = session.isValid(2);

            if (!isValid) {
                System.err.println("La connexion n'est plus valide");
            }

            return isValid;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion - " + e.getMessage());
            System.err.println("Code d'erreur SQL: " + e.getErrorCode());
            System.err.println("État SQL: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }

    public static Connexion getInstance() {
        return instance;
    }

    public Connection getSession() {
        if (!verifierConnexion()) {
            etablirConnexion();
        }
        return session;
    }

    public static Connection getSessionV2() {
        if (instance == null) {
            instance = new Connexion();
        }

        if (!instance.verifierConnexion()) {
            instance.etablirConnexion();
        }

        // Vérifier à nouveau si la connexion est valide après la tentative de reconnexion
        if (!instance.verifierConnexion()) {
            System.err.println("ALERTE: Impossible d'établir une connexion valide à la base de données après plusieurs tentatives.");

            // Vérifier si le service MariaDB est en cours d'exécution
            try {
                Process process = Runtime.getRuntime().exec("sc query MySQL");
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line;
                boolean serviceFound = false;
                boolean serviceRunning = false;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("MySQL") || line.contains("MariaDB")) {
                        serviceFound = true;
                    }
                    if (line.contains("RUNNING")) {
                        serviceRunning = true;
                    }
                }

                if (!serviceFound) {
                    System.err.println("Le service MySQL/MariaDB n'a pas été trouvé. Veuillez vérifier son installation.");
                } else if (!serviceRunning) {
                    System.err.println("Le service MySQL/MariaDB n'est pas en cours d'exécution. Veuillez le démarrer.");
                }
            } catch (Exception e) {
                System.err.println("Impossible de vérifier l'état du service MySQL/MariaDB: " + e.getMessage());
            }
        }

        return instance.session;
    }
}
