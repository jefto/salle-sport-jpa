/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;


import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author TCHAMIE
 */
@Entity
@Table(name="demande_inscription")
public class DemandeInscription extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_demande_inscription")
    private int id;

    @Column(name="date_demande")
    private LocalDateTime dateDeDemande;

    @Column(name="date_traitement")
    private LocalDateTime dateDeTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    
    public DemandeInscription(){
        
    }

    public DemandeInscription(int id, LocalDateTime dateDeDemande, LocalDateTime dateDeTraitement, Client client) {
        this.id = id;
        this.dateDeDemande = dateDeDemande;
        this.dateDeTraitement = dateDeTraitement;
        this.client = client;
    }

    public DemandeInscription(LocalDateTime dateDeDemande, LocalDateTime dateDeTraitement, Client client) {
        this.dateDeDemande = dateDeDemande;
        this.dateDeTraitement = dateDeTraitement;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateDeDemande() {
        return dateDeDemande;
    }

    public void setDateDeDemande(LocalDateTime dateDeDemande) {
        this.dateDeDemande = dateDeDemande;
    }

    public LocalDateTime getDateDeTraitement() {
        return dateDeTraitement;
    }

    public void setDateDeTraitement(LocalDateTime dateDeTraitement) {
        this.dateDeTraitement = dateDeTraitement;
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
