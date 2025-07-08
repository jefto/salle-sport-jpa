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

public class MembreService extends GenericService<entite.Membre, Integer>{

    public MembreService(){
        super(new MembreDao());
    }

    private MembreDao dao = new MembreDao();

   @Override
    public List<Membre> listerTous()
    {
        return dao.listerTous();
    }
}
