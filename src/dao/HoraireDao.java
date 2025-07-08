package dao;

import entite.Horaire;

public class HoraireDao extends GenericDao<Horaire, Integer> {
    public HoraireDao(){
        super();
        this.classEntity = Horaire.class;
        this.PrimaryKeyName = "id";
    }
}
