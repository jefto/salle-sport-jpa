/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.HoraireDao;
import entite.Horaire;
import java.util.List;

/**     
 *
 * @author TCHAMIE
 */
public class HoraireService extends GenericService<entite.Horaire, Integer>{
    public HoraireService(){
        super(new HoraireDao());
    }

    private HoraireDao dao = new HoraireDao();

    @Override
    public List<Horaire> listerTous(){
        return dao.listerTous();
    }
}
