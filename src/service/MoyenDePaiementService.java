/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.MoyenDePaiementDao;
import entite.MoyenDePaiement;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class MoyenDePaiementService extends GenericService<entite.MoyenDePaiement, Integer>{
    public MoyenDePaiementService(){
        super(new dao.MoyenDePaiementDao());
    }

    private MoyenDePaiementDao dao = new MoyenDePaiementDao();


    @Override
    public List<MoyenDePaiement> listerTous() {
        return dao.listerTous();
    }

}
