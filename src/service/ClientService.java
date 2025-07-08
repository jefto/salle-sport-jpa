/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.ClientDao;
import entite.Client;
import java.util.List;


/**
 *
 * @author TCHAMIE
 */
public class ClientService extends GenericService<entite.Client, Integer>{
    public ClientService(){
        super(new dao.SalleDao());
    }

     private ClientDao dao = new ClientDao();

    @Override
    public List<Client> listerTous() {
        return dao.listerTous();
    }
    
}
