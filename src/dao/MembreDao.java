package dao;

import entite.Membre;

public class MembreDao extends GenericDao<Membre, Integer> {
    public MembreDao(){
        super();
        this.classEntity = Membre.class;
        this.PrimaryKeyName = "id";
    }
}