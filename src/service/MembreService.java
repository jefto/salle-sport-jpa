/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.MembreDao;
import entite.Membre;

import java.util.List;

/**
 *
 * @author TCHAMIE
 */

public class MembreService extends GenericService<entite.Membre, Integer> {
    private MembreDao dao;

    public MembreService() {
        super(new MembreDao());
        this.dao = new MembreDao();
    }

    @Override
    public List<Membre> listerTous() {
        return dao.listerTous();
    }

    /**
     * Récupère un membre par l'ID de son client
     * @param clientId ID du client
     * @return Membre associé au client ou null si non trouvé
     */
    public Membre getMembreByClientId(Integer clientId) {
        return dao.getMembreByClientId(clientId);
    }
}
