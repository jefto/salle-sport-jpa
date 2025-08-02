package gui_client.controller;

import gui_client.panel.*;
import gui_client.ClientDashboard;
import service.UserSessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ClientNavigationController {

    private JPanel mainContentPanel;
    private Map<String, Supplier<JPanel>> panelFactory;
    private String currentPage = "Accueil";
    private List<String> protectedPages = Arrays.asList("Séances", "Abonnements", "Profil", "Notifications");

    public ClientNavigationController(JPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
        initializePanelFactory();
    }

    public String getCurrentPage() {
        return currentPage;
    }

    private void initializePanelFactory() {
        panelFactory = new HashMap<>();

        panelFactory.put("Accueil", AccueilClientPanel::new);
        panelFactory.put("Abonnements", AbonnementClientPanel::new);
        panelFactory.put("Profil", ProfilPanel::new);
        panelFactory.put("Notifications", NotificationsPanel::new);
        panelFactory.put("Inscription", InscriptionPanel::new);
        panelFactory.put("Connexion", ConnexionPanel::new);
        // Ajouter le nouveau panel d'authentification
        panelFactory.put("AuthRequest", () -> new AuthenticationRequestPanel(""));
    }

    public void navigateToPage(String pageName) {
        // Vérifier si la page est protégée et si l'utilisateur est connecté
        if (protectedPages.contains(pageName) && !UserSessionManager.getInstance().isLoggedIn()) {
            // Utilisateur non connecté essayant d'accéder à une page protégée
            // Rediriger vers le panel de demande d'authentification
            this.currentPage = "AuthRequest";

            mainContentPanel.removeAll();

            AuthenticationRequestPanel authRequestPanel = new AuthenticationRequestPanel(pageName);
            authRequestPanel.setNavigationController(this);
            mainContentPanel.add(authRequestPanel, BorderLayout.CENTER);

            mainContentPanel.revalidate();
            mainContentPanel.repaint();
            return;
        }

        this.currentPage = pageName;

        mainContentPanel.removeAll();

        JPanel newPanel = null;

        switch (pageName) {
            case "Accueil":
                AccueilClientPanel accueilPanel = new AccueilClientPanel();
                accueilPanel.setNavigationController(this);
                newPanel = accueilPanel;
                break;
            case "Abonnements":
                newPanel = new AbonnementClientPanel();
                break;
            case "Profil":
                newPanel = new ProfilPanel();
                break;
            case "Notifications":
                newPanel = new NotificationsPanel();
                break;
            case "Inscription":
                InscriptionPanel inscriptionPanel = new InscriptionPanel();
                inscriptionPanel.setNavigationController(this);
                newPanel = inscriptionPanel;
                break;
            case "Connexion":
                ConnexionPanel connexionPanel = new ConnexionPanel();
                connexionPanel.setNavigationController(this);
                newPanel = connexionPanel;
                break;
            default:
                AccueilClientPanel defaultPanel = new AccueilClientPanel();
                defaultPanel.setNavigationController(this);
                newPanel = defaultPanel;
        }

        if (newPanel != null) {
            mainContentPanel.add(newPanel, BorderLayout.CENTER);
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();

        // Rafraîchir la navbar après navigation depuis connexion/inscription
        if (pageName.equals("Accueil")) {
            refreshNavbarIfNeeded();
        }
    }

    private void refreshNavbarIfNeeded() {
        // Trouver le ClientDashboard parent
        Container parent = mainContentPanel.getParent();
        while (parent != null && !(parent instanceof ClientDashboard)) {
            parent = parent.getParent();
        }

        if (parent instanceof ClientDashboard) {
            ((ClientDashboard) parent).refreshNavbar();
        }
    }
}
