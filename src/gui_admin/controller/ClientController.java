package gui_admin.controller;

import entite.Client;
import gui_admin.view.clients.Edit;
import service.ClientService;

public class ClientController extends GenericCrudController{

    public ClientController(){
        Client client = new Client();
        Edit edit = new Edit(client);
        super(new ClientService(), edit);
    }
}
