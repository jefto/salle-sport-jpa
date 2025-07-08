package dao;

import entite.Equipement;

import javax.swing.text.html.parser.Entity;

public class EquipementDao extends GenericDao <Equipement, Integer >{
    public EquipementDao (){
        super();
        this.classEntity = Equipement.class;
        this.PrimaryKeyName = "id";
    }

}
