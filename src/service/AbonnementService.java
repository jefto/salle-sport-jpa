/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.AbonnementDao;
import entite.Abonnement;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class AbonnementService extends GenericService<entite.Abonnement, Integer>{
    public AbonnementService(){
        super(new AbonnementDao());
    }

    private AbonnementDao dao = new AbonnementDao();


    @Override
    public List<Abonnement> listerTous() {
        return dao.listerTous();
    }

    /**
     * Récupère tous les abonnements d'un membre
     * @param membreId ID du membre
     * @return Liste des abonnements du membre
     */
    public List<Abonnement> getAbonnementsByMembre(Integer membreId) {
        return dao.getAbonnementsByMembre(membreId);
    }

    /**
     * Récupère l'abonnement actif d'un membre
     * @param membreId ID du membre
     * @return Abonnement actif ou null si aucun
     */
    public Abonnement getAbonnementActif(Integer membreId) {
        return dao.getAbonnementActif(membreId);
    }

    /**
     * Vérifie le nombre d'abonnements actifs pour un membre
     * @param membreId ID du membre
     * @return Nombre d'abonnements actifs
     */
    public long countAbonnementsActifs(Integer membreId) {
        return dao.countAbonnementsActifs(membreId);
    }
}
