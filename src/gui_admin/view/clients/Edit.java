package gui_admin.view.clients;

import entite.Client;
import javax.swing.JFormattedTextField;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;
import gui_util.GenericEdit;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public class Edit extends GenericEdit<Client> {
    private GridBagConstraints gbc = new GridBagConstraints();
    private JTextField nom = new JTextField();
    private JTextField prenom = new JTextField();
    private JTextField email = new JTextField();
    private JFormattedTextField dateNaissance = new JFormattedTextField();

    public Edit(Client client) {
        super(client);
        JLabel nomLabel = new JLabel("Nom :");
        JLabel prenomLabel = new JLabel("Prénom :");
        JLabel emailLabel = new JLabel("Email :");
        JLabel dateNaissanceLabel = new JLabel("Date de naissance :");

        // Configuration du format de date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateNaissance = new JFormattedTextField(dateFormat);
        dateNaissance.setColumns(10);

        // Marges entre composants
        gbc.insets = new Insets(25, 20, 20, 20);
        // Alignement des composants
        gbc.anchor = GridBagConstraints.WEST;

        // Ligne 0 : Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.form.add(nomLabel, gbc);

        gbc.gridx = 1;
        this.form.add(nom, gbc);

        // Ligne 1 : Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.form.add(prenomLabel, gbc);

        gbc.gridx = 1;
        this.form.add(prenom, gbc);

        // Ligne 2 : Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.form.add(emailLabel, gbc);

        gbc.gridx = 1;
        this.form.add(email, gbc);

        // Ligne 3 : Date de naissance
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.form.add(dateNaissanceLabel, gbc);

        gbc.gridx = 1;
        this.form.add(dateNaissance, gbc);

        // Définir la taille préférée des champs
        Dimension fieldSize = new Dimension(200, 25);
        nom.setPreferredSize(fieldSize);
        prenom.setPreferredSize(fieldSize);
        email.setPreferredSize(fieldSize);
        dateNaissance.setPreferredSize(fieldSize);

        // Mise en forme du panneau
        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    public void initEntity() {
        this.entity.setNom(nom.getText());
        this.entity.setPrenom(prenom.getText());
        this.entity.setEmail(email.getText());

        // Conversion de la date
        try {
            if (dateNaissance.getValue() != null) {
                Date date = (Date) dateNaissance.getValue();
                LocalDateTime localDateTime = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                this.entity.setDateNaissance(localDateTime);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez le format JJ/MM/AAAA",
                    "Erreur de date",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void initForm() {
        if (this.entity != null) {
            nom.setText(this.entity.getNom());
            prenom.setText(this.entity.getPrenom());
            email.setText(this.entity.getEmail());

            // Conversion LocalDateTime vers Date pour l'affichage
            if (this.entity.getDateNaissance() != null) {
                Date date = Date.from(this.entity.getDateNaissance()
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
                dateNaissance.setValue(date);
            }
        }
    }

    @Override
    public String getTitre() {
        return entity != null && entity.getId() != null ?
                "Modification d'équipement" : "Ajout d'un Client";

    }

    @Override
    public String getTitreAjout() {
        return "Ajout d'un Client";
    }

    @Override
    public String getTitreModification() {
        return "Modification d'un Client";
    }
}