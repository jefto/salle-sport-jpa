package dao;

import entite.Membre;
import javax.persistence.TypedQuery;

public class MembreDao extends GenericDao<Membre, Integer> {
    public MembreDao(){
        super();
        this.classEntity = Membre.class;
        this.PrimaryKeyName = "id";
    }

    /**
     * Récupère un membre par l'ID de son client
     * @param clientId ID du client
     * @return Membre associé au client ou null si non trouvé
     */
    public Membre getMembreByClientId(Integer clientId) {
        try {
            TypedQuery<Membre> query = em.createQuery(
                "SELECT m FROM Membre m WHERE m.client.id = :clientId", Membre.class);
            query.setParameter("clientId", clientId);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}