package gui_admin.controller;

import entite.Equipement;
import entite.GenericEntity;
import gui_admin.view.equipements.Edit;
import gui_util.GenericEdit;
import service.EquipementService;
import service.GenericService;

public class EquipementController extends GenericCrudController{

//    public EquipementController(GenericEntity entity, GenericService service){
//        super(entity, service);
//    }
//
//    public EquipementController(GenericEntity entity, GenericService service, GenericEdit edit){
//        super(entity, service, edit);
//    }

    public EquipementController(){
        Equipement equipement = new Equipement();
        Edit edit = new Edit(equipement);
        super(new EquipementService(), edit);
    }
}
