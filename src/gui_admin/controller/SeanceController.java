package gui_admin.controller;

import entite.Seance;
import gui_admin.view.seances.Edit;
import service.SeanceService;

public class SeanceController extends GenericCrudController{
    public SeanceController(){
        Seance seance = new Seance();
        Edit edit = new Edit(seance);
        super( new SeanceService(), edit);
    }
}
