package gui_admin.controller;

import entite.Salle;
import gui_admin.view.salles.Edit;
import service.SalleService;

public class SalleController extends GenericCrudController{

    public SalleController() {
        Salle salle = new Salle();
        Edit edit = new Edit(salle);
        super(new SalleService(), edit);
    }
}
