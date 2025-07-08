package service;

import dao.GenericDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public abstract class GenericService <Entity, Key>{

    protected GenericDao dao;

    public GenericService (GenericDao dao){
        this.dao = dao;
    }
    public void ajouter(Entity entite){
        dao.ajouter(entite);
    }
    public void modifier(Entity entite){
        dao.modifier(entite);
    }
    public void supprimer(Entity entite){
        dao.supprimer(entite);
    }
    public Entity trouver(Key id){
        return (Entity) this.dao.trouver(id);
    }
    public  List<Entity> listerTous(){
        return null;
    }


}
