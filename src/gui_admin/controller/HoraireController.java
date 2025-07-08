package gui_admin.controller;

import entite.Horaire;
import gui_admin.view.horaires.Edit;
import service.HoraireService;

public class HoraireController extends GenericCrudController{

    public HoraireController(){
        Horaire horaire = new Horaire();
        Edit edit = new Edit(horaire);
        super(new HoraireService(), edit);
    }
}
