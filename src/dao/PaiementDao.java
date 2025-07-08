package dao;

import entite.Paiement;

public class PaiementDao extends GenericDao<Paiement, Integer>{
    public PaiementDao(){
        super();
        this.classEntity = Paiement.class;
        this.PrimaryKeyName = "id";
    }
}
