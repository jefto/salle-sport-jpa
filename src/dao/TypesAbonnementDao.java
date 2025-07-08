/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import entite.Salle;
import entite.TypeAbonnement;

/**
 *
 * @author DELL
 */
public class TypesAbonnementDao extends GenericDao<TypeAbonnement, Integer> {
    public TypesAbonnementDao(){
        super();
        classEntity = TypeAbonnement.class;
        PrimaryKeyName = "code";
    }
}
