/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;

import javax.persistence.*;

/**
 *
 * @author TCHAMIE
 */
@Entity
@Table(name="ticket")
public class Ticket extends GenericEntity{

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @Column(name="id_ticket")
    private int id;

    @Column(name="nombre_de_seance")
    private int nombreDeSeance;

    @Column(name="montant")
    private int montant;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    
    public Ticket(){
        
    }

    public Ticket(int id, int nombreDeSeance, int montant, Client client) {
        this.id = id;
        this.nombreDeSeance = nombreDeSeance;
        this.montant = montant;
        this.client = client;
    }

    public Ticket(int nombreDeSeance, int montant, Client client) {
        this.nombreDeSeance = nombreDeSeance;
        this.montant = montant;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNombreDeSeance() {
        return nombreDeSeance;
    }

    public void setNombreDeSeance(int nombreDeSeance) {
        this.nombreDeSeance = nombreDeSeance;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
}
