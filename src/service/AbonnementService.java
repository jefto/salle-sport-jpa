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
}
