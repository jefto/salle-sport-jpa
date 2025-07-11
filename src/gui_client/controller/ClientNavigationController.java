package gui_client.controller;

import gui_client.panel.*;
import gui_client.ClientDashboard;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClientNavigationController {
    
    private JPanel mainContentPanel;
    private Map<String, Supplier<JPanel>> panelFactory;
    private String currentPage = "Accueil";
    
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
        panelFactory.put("Séances", SeancesPanel::new);
        panelFactory.put("Abonnements", AbonnementClientPanel::new);
        panelFactory.put("Profil", ProfilPanel::new);
        panelFactory.put("Notifications", NotificationsPanel::new);
        panelFactory.put("Inscription", InscriptionPanel::new);
        panelFactory.put("Connexion", ConnexionPanel::new);
    }
    
    public void navigateToPage(String pageName) {
        this.currentPage = pageName;
        
        mainContentPanel.removeAll();
        
        JPanel newPanel = null;
        
        switch (pageName) {
            case "Accueil":
                AccueilClientPanel accueilPanel = new AccueilClientPanel();
                accueilPanel.setNavigationController(this);
                newPanel = accueilPanel;
                break;
            case "Séances":
                newPanel = new SeancesPanel();
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