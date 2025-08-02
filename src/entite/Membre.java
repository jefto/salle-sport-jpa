/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
@Entity
@Table(name="membre")
public class Membre extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_membre")
    private Integer id;

    @Column(name="date_inscription")
    private LocalDateTime dateInscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Abonnement> abonnement;

    public Membre(){
    }

    public Membre(Integer id, LocalDateTime dateInscription, Client client, Abonnement abonnement) {
        this.id = id;
        this.dateInscription = dateInscription;
        this.client = client;
    }

    public Membre(LocalDateTime dateInscription, Client client, Abonnement abonnement) {
        this.dateInscription = dateInscription;
        this.client = client;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Abonnement> getAbonnement() {
        return abonnement;
    }

    public void setAbonnement(List<Abonnement> abonnement) {
        this.abonnement = abonnement;
    }

    @Override
    public String toString() {
        if (client != null) {
            return client.getPrenom() + " " + client.getNom() + " (ID: " + id + ")";
        }
        return "Membre ID: " + id;
    }
}
