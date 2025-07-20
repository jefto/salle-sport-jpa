package dao;

import dao.util.DaoProxy;
import entite.Salle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//    public List<Entity> listerTous ({
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

    /**
     * Méthode générique pour exécuter une requête JPQL personnalisée et retourner une entité unique
     * @param jpql La requête JPQL
     * @param parameters Les paramètres de la requête (nom -> valeur)
     * @return L'entité trouvée ou null si aucune trouvée
     */
    public Entity trouverParRequete(String jpql, Map<String, Object> parameters) {
        try {
            TypedQuery<Entity> query = em.createQuery(jpql, classEntity);
            if (parameters != null) {
                for (Map.Entry<String, Object> param : parameters.entrySet()) {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode générique pour exécuter une requête JPQL personnalisée et retourner une liste d'entités
     * @param jpql La requête JPQL
     * @param parameters Les paramètres de la requête (nom -> valeur)
     * @return La liste des entités trouvées
     */
    public List<Entity> listerParRequete(String jpql, Map<String, Object> parameters) {
        try {
            TypedQuery<Entity> query = em.createQuery(jpql, classEntity);
            if (parameters != null) {
                for (Map.Entry<String, Object> param : parameters.entrySet()) {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
            List<Entity> result = query.getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Méthode générique pour trouver une entité par un champ spécifique
     * @param fieldName Le nom du champ
     * @param value La valeur à rechercher
     * @return L'entité trouvée ou null
     */
    public Entity trouverPar(String fieldName, Object value) {
        try {
            String className = classEntity.getSimpleName();
            String jpql = "SELECT e FROM " + className + " e WHERE e." + fieldName + " = :value";
            TypedQuery<Entity> query = em.createQuery(jpql, classEntity);
            query.setParameter("value", value);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode générique pour lister des entités par un champ spécifique
     * @param fieldName Le nom du champ
     * @param value La valeur à rechercher
     * @return La liste des entités trouvées
     */
    public List<Entity> listerPar(String fieldName, Object value) {
        try {
            String className = classEntity.getSimpleName();
            String jpql = "SELECT e FROM " + className + " e WHERE e." + fieldName + " = :value";
            TypedQuery<Entity> query = em.createQuery(jpql, classEntity);
            query.setParameter("value", value);
            List<Entity> result = query.getResultList();
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Getter pour l'EntityManager (utile pour les DAO enfants qui ont besoin de requêtes très spécifiques)
     * @return L'EntityManager
     */
    protected EntityManager getEntityManager() {
        return em;
    }

}
