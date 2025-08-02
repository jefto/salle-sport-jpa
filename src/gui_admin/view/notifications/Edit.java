//package gui_admin.view.notifications;
//
//import entite.Notification;
//import entite.Membre;
//import gui_util.GenericEdit;
//import service.MembreService;
//
//import javax.swing.*;
//import java.awt.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.List;
//
//public class Edit extends GenericEdit<Notification> {
//    private GridBagConstraints gbc = new GridBagConstraints();
//    private JTextField dateEnvoi = new JTextField();
//    private JTextField type = new JTextField();
//    private JTextField destinataire = new JTextField();
//    private JTextArea description = new JTextArea(4, 20);
//    private JCheckBox estLu = new JCheckBox("Notification lue");
//    private JList<Membre> membresList = new JList<>();
//    private JScrollPane membresScrollPane;
//    private JScrollPane descriptionScrollPane;
//
//    // Formatter pour les dates
//    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//
//    public Edit(Notification notification) {
//        super(notification);
//
//        JLabel dateEnvoiLabel = new JLabel("Date d'envoi :");
//        JLabel typeLabel = new JLabel("Type :");
//        JLabel destinataireLabel = new JLabel("Destinataire :");
//        JLabel descriptionLabel = new JLabel("Description :");
//        JLabel statutLabel = new JLabel("Statut :");
//        JLabel membresLabel = new JLabel("Membres concernés :");
//
//        // Marges entre composants
//        gbc.insets = new Insets(10, 20, 10, 20);
//        // Alignement des composants
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Ligne 0 : Date d'envoi
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        this.form.add(dateEnvoiLabel, gbc);
//
//        gbc.gridx = 1;
//        this.form.add(dateEnvoi, gbc);
//
//        // Ligne 1 : Type
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        this.form.add(typeLabel, gbc);
//
//        gbc.gridx = 1;
//        this.form.add(type, gbc);
//
//        // Ligne 2 : Destinataire
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        this.form.add(destinataireLabel, gbc);
//
//        gbc.gridx = 1;
//        this.form.add(destinataire, gbc);
//
//        // Ligne 3 : Description
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.anchor = GridBagConstraints.NORTHWEST;
//        this.form.add(descriptionLabel, gbc);
//
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.BOTH;
//        description.setLineWrap(true);
//        description.setWrapStyleWord(true);
//        descriptionScrollPane = new JScrollPane(description);
//        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        this.form.add(descriptionScrollPane, gbc);
//
//        // Ligne 4 : Statut (Lu/Non lu)
//        gbc.gridx = 0;
//        gbc.gridy = 4;
//        gbc.anchor = GridBagConstraints.WEST;
//        gbc.fill = GridBagConstraints.NONE;
//        this.form.add(statutLabel, gbc);
//
//        gbc.gridx = 1;
//        this.form.add(estLu, gbc);
//
//        // Ligne 5 : Membres concernés
//        gbc.gridx = 0;
//        gbc.gridy = 5;
//        gbc.anchor = GridBagConstraints.NORTHWEST;
//        this.form.add(membresLabel, gbc);
//
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.weighty = 1.0;
//        this.loadMembres();
//        membresList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        membresScrollPane = new JScrollPane(membresList);
//        membresScrollPane.setPreferredSize(new Dimension(250, 100));
//        this.form.add(membresScrollPane, gbc);
//
//        // Définir la taille préférée des champs
//        Dimension fieldSize = new Dimension(250, 25);
//        dateEnvoi.setPreferredSize(fieldSize);
//        type.setPreferredSize(fieldSize);
//        destinataire.setPreferredSize(fieldSize);
//        descriptionScrollPane.setPreferredSize(new Dimension(250, 80));
//
//        // Ajouter des tooltips pour aider l'utilisateur
//        dateEnvoi.setToolTipText("Format: jj/mm/aaaa hh:mm (ex: 25/12/2024 14:30)");
//        type.setToolTipText("Ex: INFO, ALERTE, PROMOTION, RAPPEL");
//        destinataire.setToolTipText("Email ou nom du destinataire");
//        description.setToolTipText("Description détaillée de la notification");
//
//        // Valeurs par défaut
//        if (notification.getDateEnvoi() == null) {
//            dateEnvoi.setText(LocalDateTime.now().format(formatter));
//        }
//
//        // Mise en forme du panneau
//        this.form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//    }
//
//    private void loadMembres() {
//        MembreService service = new MembreService();
//        List<Membre> membres = service.listerTous();
//        DefaultListModel<Membre> listModel = new DefaultListModel<>();
//        for (Membre membre : membres) {
//            listModel.addElement(membre);
//        }
//        membresList.setModel(listModel);
//    }
//
//    @Override
//    public void initEntity() {
//        try {
//            // Date d'envoi
//            if (!dateEnvoi.getText().trim().isEmpty()) {
//                this.entity.setDateEnvoi(LocalDateTime.parse(dateEnvoi.getText().trim(), formatter));
//            }
//
//            // Type
//            this.entity.setType(type.getText().trim());
//
//            // Destinataire
//            this.entity.setDestinataire(destinataire.getText().trim());
//
//            // Description
//            this.entity.setDescription(description.getText().trim());
//
//            // Statut de lecture
//            this.entity.setEstLu(estLu.isSelected());
//
//            // Membre sélectionné (un seul membre maintenant)
//            Membre selectedMembre = membresList.getSelectedValue();
//            this.entity.setMembre(selectedMembre);
//
//        } catch (DateTimeParseException e) {
//            // Gestion d'erreur - notification à l'utilisateur
//            JOptionPane.showMessageDialog(this,
//                "Format de date invalide. Utilisez le format: jj/mm/aaaa hh:mm",
//                "Erreur de saisie",
//                JOptionPane.ERROR_MESSAGE);
//            throw new RuntimeException("Format de date invalide", e);
//        }
//    }
//
//    @Override
//    public void initForm() {
//        // Date d'envoi
//        if (this.entity.getDateEnvoi() != null) {
//            dateEnvoi.setText(this.entity.getDateEnvoi().format(formatter));
//        }
//
//        // Type
//        if (this.entity.getType() != null) {
//            type.setText(this.entity.getType());
//        }
//
//        // Destinataire
//        if (this.entity.getDestinataire() != null) {
//            destinataire.setText(this.entity.getDestinataire());
//        }
//
//        // Description
//        if (this.entity.getDescription() != null) {
//            description.setText(this.entity.getDescription());
//        }
//
//        // Statut de lecture
//        estLu.setSelected(this.entity.getEstLu() != null && this.entity.getEstLu());
//
//        // Sélectionner les membres concernés
//        if (this.entity.getMembre() != null) {
//            DefaultListModel<Membre> model = (DefaultListModel<Membre>) membresList.getModel();
//            for (int i = 0; i < model.getSize(); i++) {
//                Membre membreModel = model.getElementAt(i);
//                if (membreModel.getId().equals(this.entity.getMembre().getId())) {
//                    membresList.setSelectedIndex(i);
//                    break;
//                }
//            }
//        }
//    }
//
//    @Override
//    public String getTitre() {
//        return entity != null && entity.getId() != null ?
//                "Modification de notification" : "Ajout d'une notification";
//    }
//
//    @Override
//    public String getTitreAjout() {
//        return "Ajout d'une notification";
//    }
//
//    @Override
//    public String getTitreModification() {
//        return "Modification de notification";
//    }
//}
