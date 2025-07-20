package gui_admin.controller;

import gui_admin.panel.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Contrôleur réutilisable pour la navigation entre les panels
 */
public class NavigationController {
    
    private final JPanel mainContent;
    private final Map<String, Supplier<JPanel>> panelFactory;
    private String currentPanel;
    
    /**
     * Constructeur du contrôleur de navigation
     * @param mainContent Le panel principal où afficher les pages
     */
    public NavigationController(JPanel mainContent) {
        this.mainContent = mainContent;
        this.panelFactory = new HashMap<>();
        this.currentPanel = null;
        
        // Initialiser les factories de panels
        initializePanelFactories();
    }
    
    /**
     * Initialise les factories pour créer les panels
     */
    private void initializePanelFactories() {
        panelFactory.put("Accueil", AcceuilPanel::new);
        panelFactory.put("Abonnements", AbonnementPanel::new);
        panelFactory.put("Clients", ClientPanel::new);
        panelFactory.put("Demande Inscription", DemandeInscriptionPanel::new);
        panelFactory.put("Equipments", EquipementPanel::new);
        panelFactory.put("Horaire", HorairePanel::new);
        panelFactory.put("Membres", MembrePanel::new);
        panelFactory.put("Moyen de Paiement", MoyenPaiementPanel::new);
        panelFactory.put("Paiements", PaiementPanel::new);
        panelFactory.put("Salles", SallePanel::new);
        panelFactory.put("Seance", SeancePanel::new);
        panelFactory.put("Tickets", TicketPanel::new);
        panelFactory.put("Types Abonnements", TypeAbonnementPanel::new);
        panelFactory.put("Notifications", NotificationPanel::new);
    }
    
    /**
     * Navigue vers une page spécifique
     * @param pageName Le nom de la page à afficher
     */
    public void navigateToPage(String pageName) {
        if (pageName == null || pageName.trim().isEmpty()) {
            showErrorPage("Nom de page invalide");
            return;
        }
        
        // Éviter de recharger la même page
        if (pageName.equals(currentPanel)) {
            return;
        }
        
        mainContent.removeAll();
        
        try {
            JPanel panel = createPanel(pageName);
            mainContent.add(panel, BorderLayout.CENTER);
            currentPanel = pageName;
            
            // Rafraîchir l'affichage
            refreshDisplay();
            
        } catch (Exception e) {
            showErrorPage("Erreur lors du chargement de la page : " + pageName);
            e.printStackTrace();
        }
    }
    
    /**
     * Crée un panel pour la page demandée
     * @param pageName Le nom de la page
     * @return Le panel créé
     */
    private JPanel createPanel(String pageName) {
        Supplier<JPanel> factory = panelFactory.get(pageName);
        
        if (factory != null) {
            return factory.get();
        } else {
            // Panel par défaut pour les pages inconnues
            return createUnknownPagePanel(pageName);
        }
    }
    
    /**
     * Crée un panel pour les pages inconnues
     * @param pageName Le nom de la page inconnue
     * @return Un panel d'erreur
     */
    private JPanel createUnknownPagePanel(String pageName) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorLabel = new JLabel("Page inconnue : " + pageName, SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        errorLabel.setForeground(Color.RED);
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        return errorPanel;
    }
    
    /**
     * Affiche une page d'erreur
     * @param message Le message d'erreur
     */
    private void showErrorPage(String message) {
        mainContent.removeAll();
        
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(Color.WHITE);
        
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        errorLabel.setForeground(Color.RED);
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        mainContent.add(errorPanel, BorderLayout.CENTER);
        
        refreshDisplay();
    }
    
    /**
     * Rafraîchit l'affichage du panel principal
     */
    private void refreshDisplay() {
        mainContent.revalidate();
        mainContent.repaint();
    }
    
    /**
     * Obtient le nom de la page actuellement affichée
     * @return Le nom de la page actuelle
     */
    public String getCurrentPanel() {
        return currentPanel;
    }
    
    /**
     * Ajoute ou met à jour une factory de panel
     * @param pageName Le nom de la page
     * @param factory La factory pour créer le panel
     */
    public void addPanelFactory(String pageName, Supplier<JPanel> factory) {
        panelFactory.put(pageName, factory);
    }
    
    /**
     * Supprime une factory de panel
     * @param pageName Le nom de la page à supprimer
     */
    public void removePanelFactory(String pageName) {
        panelFactory.remove(pageName);
    }
    
    /**
     * Obtient la liste des pages disponibles
     * @return Un ensemble des noms de pages disponibles
     */
    public String[] getAvailablePages() {
        return panelFactory.keySet().toArray(new String[0]);
    }
}