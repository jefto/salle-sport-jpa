/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.EquipementDao;
import entite.Equipement;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class EquipementService extends  GenericService<Equipement, Integer>{
    public EquipementService(){
        super(new dao.EquipementDao());
    }

    private EquipementDao dao = new EquipementDao();

    @Override
    public List<Equipement> listerTous() {
        return dao.listerTous();
    }
    
}
