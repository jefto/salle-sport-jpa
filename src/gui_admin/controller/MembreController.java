package gui_admin.controller;

import entite.Membre;
import gui_admin.view.membres.Edit;
import service.MembreService;

public class MembreController extends GenericCrudController{
    public MembreController(){
        Membre membre= new Membre();
        Edit edit = new Edit(membre);
        super(new MembreService(), edit);
    }
}
