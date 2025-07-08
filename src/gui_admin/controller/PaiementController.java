package gui_admin.controller;

import entite.Paiement;
import gui_admin.view.paiements.Edit;
import service.PaiementService;

public class PaiementController extends GenericCrudController{
    public PaiementController(){
        Paiement paiement = new Paiement();
        Edit edit = new Edit(paiement);
        super(new PaiementService(), edit);
    }
}
