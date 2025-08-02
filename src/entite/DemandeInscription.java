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
    @JoinColumn(name = "id_client")
    private Client client;
    
    // Nouveau champ pour le statut
    @Column(name="statut", length = 20)
    private String statut; // "EN_ATTENTE", "ACCEPTE", "REJETE"

    public DemandeInscription(){
        this.statut = "EN_ATTENTE"; // Statut par défaut
    }

    public DemandeInscription(int id, LocalDateTime dateDeDemande, LocalDateTime dateDeTraitement, Client client) {
        this.id = id;
        this.dateDeDemande = dateDeDemande;
        this.dateDeTraitement = dateDeTraitement;
        this.client = client;
        this.statut = "EN_ATTENTE";
    }

    public DemandeInscription(LocalDateTime dateDeDemande, LocalDateTime dateDeTraitement, Client client) {
        this.dateDeDemande = dateDeDemande;
        this.dateDeTraitement = dateDeTraitement;
        this.client = client;
        this.statut = "EN_ATTENTE";
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
