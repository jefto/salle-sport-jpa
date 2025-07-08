package dao;

import entite.Client;

public class ClientDao extends GenericDao<Client, Integer>{
    public ClientDao(){
        super();
        this.classEntity = Client.class;
        this.PrimaryKeyName = "id";
    }
}
