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
@Table(name="abonnement")
public class Abonnement extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_abonnement")
    private int id;

    @Column(name="date_debut")
    private LocalDateTime dateDebut;

    @Column(name="date_fin")
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_abonnement")
    private TypeAbonnement typeAbonnement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paiement")
    private Paiement paiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membre")
    private Membre membre;
    
    public Abonnement(){
        
    }

    public Abonnement(LocalDateTime dateDebut, LocalDateTime dateFin, TypeAbonnement typeAbonnement, Paiement paiement, Membre membre, int id) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.typeAbonnement = typeAbonnement;
        this.paiement = paiement;
        this.membre = membre;
        this.id = id;
    }

    public Abonnement(LocalDateTime dateDebut, LocalDateTime dateFin, TypeAbonnement typeAbonnement, Paiement paiement, Membre membre) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.typeAbonnement = typeAbonnement;
        this.paiement = paiement;
        this.membre = membre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public TypeAbonnement getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(TypeAbonnement typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
