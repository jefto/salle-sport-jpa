/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.SeanceDao;
import entite.Seance;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class SeanceService extends GenericService<entite.Seance, Integer>{
    public SeanceService(){
        super(new SeanceDao());
    }

    private SeanceDao dao = new SeanceDao();

    @Override
    public List<Seance> listerTous() {
        return dao.listerTous();
    }
}

