package gui_admin.controller;

import entite.TypeAbonnement;
import gui_admin.view.types_abonnement.Edit;
import service.TypeAbonnementService;

public class TypesAbonnementController extends GenericCrudController{

    public TypesAbonnementController() {
        TypeAbonnement typeAbonnement = new TypeAbonnement();
        Edit edit = new Edit(typeAbonnement);
        super(new TypeAbonnementService(), edit);
    }
}
