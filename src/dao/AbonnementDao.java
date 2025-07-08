package dao;

import entite.Abonnement;

public class AbonnementDao extends GenericDao<Abonnement, Integer>{
    public AbonnementDao(){
        super();
        this.classEntity = Abonnement.class;
        this.PrimaryKeyName = "id";
    }
}
