/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;


import dao.PaiementDao;
import entite.Paiement;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class PaiementService extends GenericService<Paiement, Integer>{
    public PaiementService(){
        super(new PaiementDao());
    }

    private PaiementDao dao = new PaiementDao();

    @Override
    public List<Paiement> listerTous() {
        return dao.listerTous();
    }
    
}
