package gui_admin.controller;

import entite.GenericEntity;
import gui_util.GenericEdit;
import service.GenericService;
import javax.swing.JOptionPane;

public class GenericCrudController  {

    private GenericEntity newEntity;
    protected GenericService service;
    protected GenericEdit edit ;

    public GenericCrudController(){

    }

    public GenericCrudController(GenericEntity entity, GenericService service){
        this.service = service;
        this.newEntity = entity ;
    }

    public GenericCrudController(GenericEntity entity, GenericService service, GenericEdit edit) {
        this(entity, service);
        this.edit = edit;
    }
    public GenericCrudController(GenericService service, GenericEdit edit) {
        this.service = service;
        this.edit = edit;
    }


    public void ajouter() {
        edit.afficher();
        System.out.println(newEntity);
//        edit.setEntity(newEntity);
        edit.getSaveButton().addActionListener(e -> {
            edit.initEntity();
            service.ajouter(edit.getEntity());
            edit.dispose();
        });
    }

    public void modifier(GenericEntity entite) {
        edit.afficher();
        edit.initForm();
        edit.setEntity(entite);
        edit.getSaveButton().addActionListener(e -> {
            edit.initEntity();
            service.modifier(edit.getEntity());
            edit.dispose();
        });
    }
    
    public void supprimer(Object id) {
        // Afficher une boîte de dialogue de confirmation
        int confirmation = JOptionPane.showConfirmDialog(
            null,
            "Êtes-vous sûr de vouloir supprimer l'élément avec l'ID : " + id + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                // D'abord récupérer l'entité par son ID
                GenericEntity entite = (GenericEntity) service.trouver(id);
                if (entite != null) {
                    service.supprimer(entite);
                    JOptionPane.showMessageDialog(
                        null,
                        "Élément supprimé avec succès !",
                        "Suppression réussie",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Aucun élément trouvé avec l'ID : " + id,
                        "Élément introuvable",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Erreur lors de la suppression : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}