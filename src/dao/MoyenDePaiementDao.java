package dao;

import entite.MoyenDePaiement;

public class MoyenDePaiementDao extends GenericDao<MoyenDePaiement, Integer> {
    public MoyenDePaiementDao(){
        super();
        this.classEntity = MoyenDePaiement.class;
        this.PrimaryKeyName = "id";
    }
}
