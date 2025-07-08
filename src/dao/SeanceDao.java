package dao;

import entite.Seance;

public class SeanceDao extends GenericDao<Seance, Integer>{
    public SeanceDao(){
        super();
        this.classEntity = Seance.class;
        this.PrimaryKeyName = "id";
    }
}
