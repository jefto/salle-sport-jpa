package gui_admin.controller;

import entite.DemandeInscription;
import gui_admin.view.demandes_inscription.Edit;
import service.DemandeInscriptionService;

public class DemandeInscriptionController extends GenericCrudController{

    public DemandeInscriptionController(){
        DemandeInscription demandeInscription= new DemandeInscription();
        Edit edit = new Edit(demandeInscription);
        super(new DemandeInscriptionService(), edit);
    }
}
