/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package salle_gym;

//import entite.Salle;
//import service.SalleService;

//import daojdbc.SalleDao;
//import daojdbc.ClientDao;
//import daojdbc.TicketDao;
//import daojdbc.TypeAbonnementDao;
//import daojdbc.PaiementDao;
//import daojdbc.AbonnementDao;
//import daojdbc.MembreDao;
//import daojdbc.SeanceDao;
import entite.*;
import gui_admin.AdminDashboard;
import gui_admin.controller.*;
import gui_admin.view.equipements.Edit;
import service.EquipementService;
import service.PaiementService;
import service.SalleService;
import service.TicketService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;


/**
 *
 * @author TCHAMIE
 */
public class Main {
    //private static SalleService salleService;
    
    /*static{
        salleService = new SalleService();
    }*/
    
    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        Salle salle1 = new Salle("Salle A", "Salle de musculation");
        Salle salle2 = new Salle("Salle B", "Salle de cardio");
        
        // Ajouter des salles
        salleService.ajouter(salle1);
        salleService.ajouter(salle2);
        
        // Lister toutes les salles
        System.out.println("Liste des salles :");
        for (Salle salle : salleService.lister()) {
            System.out.println(salle.getLibelle() + " - " + salle.getDescription());
        }
        
        // Trouver une salle par son libellé
        Salle foundSalle = salleService.trouver("Salle A");
        if (foundSalle != null) {
            System.out.println("Salle trouvée : " + foundSalle.getLibelle());
        } else {
            System.out.println("Salle non trouvée.");
        }
        
        // Modifier une salle
        salle1.setDescription("Salle de musculation et fitness");
        salleService.modifier(salle1);
        
        // Supprimer une salle
        salleService.supprimer(salle2);
        
        // Lister à nouveau pour voir les changements
        System.out.println("Liste des salles après modification et suppression :");
        for (Salle salle : salleService.lister()) {
            System.out.println(salle.getLibelle() + " - " + salle.getDescription());
        }
    }*/
    
     /*public static void main(String[] args) {
        // DAO
        ClientDao clientDao = new ClientDao();
        TicketDao ticketDao = new TicketDao();
        TypeAbonnementDao typeAbonnementDao = new TypeAbonnementDao();
        MembreDao membreDao = new MembreDao();
        PaiementDao paiementDao = new PaiementDao();
        AbonnementDao abonnementDao = new AbonnementDao();
        SeanceDao seanceDao = new SeanceDao();

        // 1. Création et ajout d'un client
        Client client = new Client();
        client.setNom("Alice");
        client.setPrenom("Dupont");
        client.setEmail("alice.dupont@example.com");
        client.setDateNaissance(LocalDateTime.of(1995, 5, 20, 0, 0));
        clientDao.ajouter(client);
        System.out.println("Client ajouté : " + client.getNom());

        // 2. Ajout d'un ticket pour ce client
        Ticket ticket = new Ticket();
        ticket.setNombreDeSeance(10);
        ticket.setMontant(150);
        ticket.setClient(client);
        ticketDao.ajouter(ticket);
        System.out.println("Ticket ajouté pour client " + client.getNom());

        // 3. Ajout d'un type abonnement
        TypeAbonnement typeAbonnement = new TypeAbonnement();
        typeAbonnement.setCode("ABO1");
        typeAbonnement.setLibelle("Mensuel");
        typeAbonnement.setMontant(100);
        typeAbonnementDao.ajouter(typeAbonnement);
        System.out.println("Type abonnement ajouté : " + typeAbonnement.getLibelle());

        // 4. Ajout d'un membre lié au client
        Membre membre = new Membre();
        membre.setDateInscription(LocalDateTime.now());
        membre.setClient(client);
        membreDao.ajouter(membre);
        System.out.println("Membre ajouté pour client " + client.getNom());

        // 5. Ajout d'un paiement
        Paiement paiement = new Paiement();
        paiement.setDateDePaiement(LocalDateTime.now());
        paiement.setMontant(100);
        // supposons que moyenDePaiement est déjà existant dans la BD et on récupère son code "MP1"
        MoyenDePaiement moyenDePaiement = new MoyenDePaiement();
        moyenDePaiement.setCode("MP1");
        paiement.setMoyenDePaiement(moyenDePaiement);
        paiementDao.ajouter(paiement);
        System.out.println("Paiement ajouté");

        // 6. Ajout d'un abonnement lié au membre, typeAbonnement, et paiement
        Abonnement abonnement = new Abonnement();
        abonnement.setDateDebut(LocalDateTime.now());
        abonnement.setDateFin(LocalDateTime.now().plusMonths(1));
        abonnement.setMembre(membre);
        abonnement.setTypeAbonnement(typeAbonnement);
        abonnement.setPaiement(paiement);
        abonnementDao.ajouter(abonnement);
        System.out.println("Abonnement ajouté");

        // 7. Ajout d'une salle
        Salle salle = new Salle();
        salle.setLibelle("Salle A");
        salle.setDescription("Salle principale");
        // suppose tu as un SalleDao et méthode ajouter
        SalleDao salleDao = new SalleDao();
        salleDao.ajouter(salle);
        System.out.println("Salle ajoutée");

        // 8. Ajout d'une séance liée à la salle et au membre
        Seance seance = new Seance();
        seance.setDateDebut(LocalDateTime.now().plusDays(1));
        seance.setDateFin(LocalDateTime.now().plusDays(1).plusHours(1));
        seance.setSalle(salle);
        seance.setMembre(membre);
        seanceDao.ajouter(seance);
        System.out.println("Séance ajoutée");

        // 9. Listage des clients
        List<Client> clients = clientDao.listerTous();
        System.out.println("Clients en base :");
        for (Client c : clients) {
            System.out.println("- " + c.getId() + ": " + c.getNom() + " " + c.getPrenom());
        }
    }*/

    public static void main(String[] args) {

       /*TypeAbonnement typeAbonnement = new TypeAbonnement("HEBDO", "Hebdomadaire", 50000);
       EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaPU");
       EntityManager em = emf.createEntityManager();

       //demarrer une transaction pour persister l'entité
       em.getTransaction().begin();
       em.persist(typeAbonnement);
       em.getTransaction().commit();
       em.close();

       emf.close();

    };*/

//    Salle salle = new Salle("salle 1","je suis a la salle une");
//    Equipement equipement = new Equipement("Equipement un","Je suis ",salle);
//    equipement.setSalle(salle);
//    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaPU");
//    EntityManager em = emf.createEntityManager();
//
//       em.getTransaction().begin();
//       em.persist(equipement);
//       em.getTransaction().commit();
//       em.close();
//       emf.close();

//        EquipementController equipementController = new EquipementController();
//        equipementController.ajouter();

//        TicketService service = new TicketService();
//         List<Ticket> equipement = service.listerTous();
//         System.out.println(equipement);
//        EquipementController equipementController = new EquipementController();
//        equipementController.modifier(equipement);

//        SalleService service = new SalleService();
//        List<Salle> salles = service.listerTous();
//        service.ajouter(new Salle("Salle Nukafu", "Salle Nukafu"));
//        service.ajouter(new Salle("Salle Agoe", "Salle Agoe"));
//        service.ajouter(new Salle("Salle Sanguera", "Salle Sanguera"));
//
//        list
//        for (Salle salle : salles){
//            System.out.println(salle);
//        }
        // Lancer l'interface graphique dans le thread EDT (Event Dispatch Thread)

        SwingUtilities.invokeLater(() -> {
            AdminDashboard frame = new AdminDashboard();
            frame.setVisible(true);
        });



    };

}
