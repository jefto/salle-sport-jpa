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
@Table(name="seance")
public class Seance extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_seance")
    private Integer id;

    @Column(name="date_debut")
    private LocalDateTime dateDebut;

    @Column(name="date_fin")
    private LocalDateTime dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    private Membre membre;

    @ManyToOne(fetch = FetchType.LAZY)
    private Salle salle;
    
    public Seance(){
        
    }

    public Seance(Integer id, LocalDateTime dateDebut, LocalDateTime dateFin, Salle salle, Membre membre) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salle = salle;
        this.membre = membre;
    }

    public Seance(LocalDateTime dateDebut, LocalDateTime dateFin, Membre membre, Salle salle) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.membre = membre;
        this.salle = salle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
