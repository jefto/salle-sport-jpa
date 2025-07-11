package gui_admin.panel;

import entite.Client;
import service.ClientService;
import gui_util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class ClientPanel extends JPanel implements CrudOperationsInterface {

    private List<Client> clients;
    private Client clientSelectionne;
    private ClientService clientService;
    private DefaultTableModel tableModel;
    private JTable table;
    
    // Pattern pour validation email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public ClientPanel() {
        this.setLayout(new BorderLayout());
        this.clientService = new ClientService();
        
        initializeTable();
        loadData();
    }

    private void initializeTable() {
        // Ajout de la colonne "Mot de passe"
        String[] colonnes = {"Sélection", "ID", "Nom", "Prénom", "Date de naissance", "Email", "Mot de passe"};

        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Nom
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Prénom
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Date de naissance
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Email
        table.getColumnModel().getColumn(6).setPreferredWidth(120);  // Mot de passe
        
        // Styliser l'en-tête
        StyleUtil.styliserTableHeader(table);

        // Ajouter le listener pour les cases à cocher
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                    int row = e.getFirstRow();
                    boolean isChecked = (Boolean) tableModel.getValueAt(row, 0);
                    
                    if (isChecked) {
                        // Décocher les autres cases
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (i != row) {
                                tableModel.setValueAt(false, i, 0);
                            }
                        }
                        clientSelectionne = clients.get(row);
                    } else {
                        clientSelectionne = null;
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        // Vider le modèle
        tableModel.setRowCount(0);
        
        // Charger les données
        clients = clientService.listerTous();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Client client : clients) {
            tableModel.addRow(new Object[]{
                false,
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getDateNaissance() != null ? client.getDateNaissance().format(formatter) : "Non définie",
                client.getEmail(),
                client.getMotDePasse() != null ? client.getMotDePasse() : "Non défini" // Affichage complet du mot de passe
            });
        }
    }

    @Override
    public void ajouter() {
        // Créer un formulaire de saisie avec le champ mot de passe
        JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField dateNaissanceField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField motDePasseField = new JTextField(); // Champ texte normal pour voir le mot de passe
        
        // Pré-remplir la date avec un exemple
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateNaissanceField.setText("01/01/1990 00:00");
        
        formulaire.add(new JLabel("Nom :"));
        formulaire.add(nomField);
        formulaire.add(new JLabel("Prénom :"));
        formulaire.add(prenomField);
        formulaire.add(new JLabel("Date de naissance (dd/MM/yyyy HH:mm) :"));
        formulaire.add(dateNaissanceField);
        formulaire.add(new JLabel("Email :"));
        formulaire.add(emailField);
        formulaire.add(new JLabel("Mot de passe :"));
        formulaire.add(motDePasseField);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            formulaire, 
            "Ajouter un nouveau client", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String dateNaissanceText = dateNaissanceField.getText().trim();
            String email = emailField.getText().trim();
            String motDePasse = motDePasseField.getText().trim();
            
            if (!nom.isEmpty() && !prenom.isEmpty() && !dateNaissanceText.isEmpty() && !email.isEmpty() && !motDePasse.isEmpty()) {
                try {
                    // Validation de l'email
                    if (!EMAIL_PATTERN.matcher(email).matches()) {
                        JOptionPane.showMessageDialog(this, 
                            "Format d'email invalide!", 
                            "Email invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Parser la date
                    LocalDateTime dateNaissance = LocalDateTime.parse(dateNaissanceText, formatter);
                    
                    // Vérifier que la date de naissance n'est pas dans le futur
                    if (dateNaissance.isAfter(LocalDateTime.now())) {
                        JOptionPane.showMessageDialog(this, 
                            "La date de naissance ne peut pas être dans le futur!", 
                            "Date invalide", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    Client nouveauClient = new Client(nom, prenom, dateNaissance, email);
                    nouveauClient.setMotDePasse(motDePasse);
                    clientService.ajouter(nouveauClient);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Client ajouté avec succès!", 
                        "Ajout réussi", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Recharger les données
                    loadData();
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ajout: " + e.getMessage() + 
                        "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/1990 14:30)", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Tous les champs sont obligatoires!", 
                    "Champs manquants", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public void modifier() {
        if (clientSelectionne != null) {
            // Pré-remplir le formulaire avec les données existantes
            JPanel formulaire = new JPanel(new GridLayout(5, 2, 10, 10));
            JTextField nomField = new JTextField(clientSelectionne.getNom());
            JTextField prenomField = new JTextField(clientSelectionne.getPrenom());
            JTextField dateNaissanceField = new JTextField();
            JTextField emailField = new JTextField(clientSelectionne.getEmail());
            JTextField motDePasseField = new JTextField(); // Champ texte normal
            
            // Pré-remplir le mot de passe existant
            if (clientSelectionne.getMotDePasse() != null) {
                motDePasseField.setText(clientSelectionne.getMotDePasse());
            }
            
            // Formatter et afficher la date existante
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (clientSelectionne.getDateNaissance() != null) {
                dateNaissanceField.setText(clientSelectionne.getDateNaissance().format(formatter));
            }
            
            formulaire.add(new JLabel("Nom :"));
            formulaire.add(nomField);
            formulaire.add(new JLabel("Prénom :"));
            formulaire.add(prenomField);
            formulaire.add(new JLabel("Date de naissance (dd/MM/yyyy HH:mm) :"));
            formulaire.add(dateNaissanceField);
            formulaire.add(new JLabel("Email :"));
            formulaire.add(emailField);
            formulaire.add(new JLabel("Mot de passe :"));
            formulaire.add(motDePasseField);
            
            int result = JOptionPane.showConfirmDialog(
                this, 
                formulaire, 
                "Modifier le client: " + clientSelectionne.getNom() + " " + clientSelectionne.getPrenom(), 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String dateNaissanceText = dateNaissanceField.getText().trim();
                String email = emailField.getText().trim();
                String motDePasse = motDePasseField.getText().trim();
                
                if (!nom.isEmpty() && !prenom.isEmpty() && !dateNaissanceText.isEmpty() && !email.isEmpty() && !motDePasse.isEmpty()) {
                    try {
                        // Validation de l'email
                        if (!EMAIL_PATTERN.matcher(email).matches()) {
                            JOptionPane.showMessageDialog(this, 
                                "Format d'email invalide!", 
                                "Email invalide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Parser la date
                        LocalDateTime dateNaissance = LocalDateTime.parse(dateNaissanceText, formatter);
                        
                        // Vérifier que la date de naissance n'est pas dans le futur
                        if (dateNaissance.isAfter(LocalDateTime.now())) {
                            JOptionPane.showMessageDialog(this, 
                                "La date de naissance ne peut pas être dans le futur!", 
                                "Date invalide", 
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        // Mettre à jour les données du client sélectionné
                        clientSelectionne.setNom(nom);
                        clientSelectionne.setPrenom(prenom);
                        clientSelectionne.setDateNaissance(dateNaissance);
                        clientSelectionne.setEmail(email);
                        clientSelectionne.setMotDePasse(motDePasse);
                        
                        clientService.modifier(clientSelectionne);
                        
                        JOptionPane.showMessageDialog(this, 
                            "Client modifié avec succès!", 
                            "Modification réussie", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Recharger les données
                        loadData();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur lors de la modification: " + e.getMessage() + 
                            "\nFormat de date attendu: dd/MM/yyyy HH:mm (ex: 15/01/1990 14:30)", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Tous les champs sont obligatoires!", 
                        "Champs manquants", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    @Override
    public void supprimer() {
        if (clientSelectionne != null) {
            try {
                clientService.supprimer(clientSelectionne);
                JOptionPane.showMessageDialog(this, 
                    "Client supprimé avec succès!", 
                    "Suppression réussie", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Recharger les données
                loadData();
                clientSelectionne = null;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean hasSelection() {
        return clientSelectionne != null;
    }

    public Client getClientSelectionne() {
        return clientSelectionne;
    }
}