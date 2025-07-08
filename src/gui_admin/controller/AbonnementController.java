package gui_admin.controller;

import entite.Abonnement;
import gui_admin.view.abonnements.Edit;
import service.AbonnementService;

public class AbonnementController extends GenericCrudController{
    public AbonnementController(){
        Abonnement abonnement = new Abonnement();
        Edit edit = new Edit(abonnement);
        super(new AbonnementService(), edit);
    }
}
