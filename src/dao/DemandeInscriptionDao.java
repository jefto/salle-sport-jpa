package dao;

import entite.DemandeInscription;

public class DemandeInscriptionDao extends GenericDao<DemandeInscription, Integer> {
    public DemandeInscriptionDao(){
        super();
        classEntity = DemandeInscription.class;
        PrimaryKeyName = "id";
    }
}
