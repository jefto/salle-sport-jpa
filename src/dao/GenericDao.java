package dao;

import dao.util.DaoProxy;
import entite.Salle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class GenericDao <Entity, Key> {
    protected static EntityManager em ;
    protected DaoProxy daoProxy;
    protected Class<Entity> classEntity;
    protected String PrimaryKeyName ;

    static{
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaPU");
        em = emf.createEntityManager();
    }
    public GenericDao(){
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaPU");
//        em = emf.createEntityManager();
        daoProxy = new DaoProxy(em);
    }

    public void ajouter(Entity entite){
        daoProxy.executeConsumer(entityManager -> {
            entityManager.persist(entite);
        });
    }
    public void modifier(Entity entite){
        daoProxy.executeConsumer(entityManager -> {
            entityManager.merge(entite);
        });

    }
    public void supprimer(Entity entite){
        daoProxy.executeConsumer(entityManager -> {
            entityManager.remove(entityManager.merge(entite));
        });

    }
    public Entity trouver (Key id){
        String ClassName = classEntity.getSimpleName();
        String jpql = "SELECT e FROM " + ClassName + " e "
                + "WHERE e." + PrimaryKeyName + " = :key";//SElection tous elmen de type salle
        Query query = em.createQuery(jpql, classEntity);
        query.setParameter("key",id);
        List<Entity> liste = query.getResultList();
        if(liste.isEmpty()){
            //Lever une exeption

        }
        return (Entity) query.getResultList().get(0);
    }
//    public List<Entity> listerTous (){
//        Salle.class.getSimpleName();
//            String ClassName = classEntity.getSimpleName();
//            String jpql = "SELECT e FROM "+ ClassName + " e";//SElection tous elmen de type salle
//            Query query = em.createQuery(jpql, classEntity);
//            return query.getResultList();
//    }

    public List<Entity> listerTous() {
        try {
            String className = classEntity.getSimpleName();
            String jpql = "SELECT e FROM " + className + " e";
            Query query = em.createQuery(jpql, classEntity);
            List<Entity> result = query.getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Retourner une liste vide au lieu de null
        }
    }


}
