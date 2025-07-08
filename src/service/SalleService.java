/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.SalleDao;
import entite.Salle;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class SalleService extends GenericService<entite.Salle, Integer>{
    public SalleService(){
        super(new dao.SalleDao());
    }

    private SalleDao dao = new SalleDao();

    @Override
    public List<Salle> listerTous(){
        return this.dao.listerTous();
    }



}


