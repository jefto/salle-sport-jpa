/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.DemandeInscriptionDao;
import entite.DemandeInscription;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class DemandeInscriptionService extends GenericService<entite.DemandeInscription, Integer>{
    public DemandeInscriptionService(){
        super(new DemandeInscriptionDao());
    }

    private DemandeInscriptionDao dao = new DemandeInscriptionDao();


    @Override
    public List<DemandeInscription> listerTous() {
        return dao.listerTous();
    }
}
