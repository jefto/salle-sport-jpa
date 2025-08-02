package dao;

import entite.Abonnement;
import javax.persistence.TypedQuery;
import java.util.List;
import java.time.LocalDateTime;

public class AbonnementDao extends GenericDao<Abonnement, Integer>{
    public AbonnementDao(){
        super();
        this.classEntity = Abonnement.class;
        this.PrimaryKeyName = "id";
    }

    /**
     * Récupère tous les abonnements d'un membre, triés par date de fin décroissante
     * @param membreId ID du membre
     * @return Liste des abonnements du membre
     */
    public List<Abonnement> getAbonnementsByMembre(Integer membreId) {
        try {
            TypedQuery<Abonnement> query = em.createQuery(
                "SELECT a FROM Abonnement a WHERE a.membre.id = :membreId ORDER BY a.dateFin DESC",
                Abonnement.class);
            query.setParameter("membreId", membreId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Récupère l'abonnement actif d'un membre (s'il en a un)
     * @param membreId ID du membre
     * @return Abonnement actif ou null si aucun
     */
    public Abonnement getAbonnementActif(Integer membreId) {
        try {
            TypedQuery<Abonnement> query = em.createQuery(
                "SELECT a FROM Abonnement a WHERE a.membre.id = :membreId AND a.dateFin >= :now ORDER BY a.dateFin DESC",
                Abonnement.class);
            query.setParameter("membreId", membreId);
            query.setParameter("now", LocalDateTime.now());
            query.setMaxResults(1);
            List<Abonnement> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vérifie s'il y a des abonnements actifs multiples pour un membre (ne devrait pas arriver)
     * @param membreId ID du membre
     * @return Nombre d'abonnements actifs
     */
    public long countAbonnementsActifs(Integer membreId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Abonnement a WHERE a.membre.id = :membreId AND a.dateFin >= :now",
                Long.class);
            query.setParameter("membreId", membreId);
            query.setParameter("now", LocalDateTime.now());
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
