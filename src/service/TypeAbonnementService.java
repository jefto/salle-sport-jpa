/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.TypesAbonnementDao;
import entite.TypeAbonnement;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
public class TypeAbonnementService extends GenericService<entite.TypeAbonnement, Integer>{
    public TypeAbonnementService(){
        super(new dao.TypesAbonnementDao());
    }
    private TypesAbonnementDao dao =  new TypesAbonnementDao();


    @Override
    public List<TypeAbonnement> listerTous(){
        return dao.listerTous();
    }
}
