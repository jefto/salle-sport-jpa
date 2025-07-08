package gui_admin.controller;

import entite.MoyenDePaiement;
import gui_admin.view.moyens_de_paiement.Edit;
import service.MoyenDePaiementService;

public class MoyenDePaiementController extends GenericCrudController{

    public MoyenDePaiementController(){
        MoyenDePaiement moyenDePaiement = new MoyenDePaiement();
        Edit edit = new Edit(moyenDePaiement);
        super(new MoyenDePaiementService(), edit);
    }
}
