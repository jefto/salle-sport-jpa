/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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

    @OneToMany(mappedBy = "seance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Membre> membres;

    @ManyToOne(fetch = FetchType.LAZY)
    private Salle salle;
    
    public Seance(){

    }

    public Seance(Integer id, LocalDateTime dateDebut, LocalDateTime dateFin, Salle salle) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salle = salle;
        this.membres = new ArrayList<>();
    }

    public Seance(LocalDateTime dateDebut, LocalDateTime dateFin, Salle salle) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salle = salle;
        this.membres = new ArrayList<>();
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

    public List<Membre> getMembres() {
        return membres;
    }

    public void setMembres(List<Membre> membres) {
        this.membres = membres;
    }

    /**
     * Ajouter un membre à la séance
     */
    public void ajouterMembre(Membre membre) {
        if (membre != null && !this.membres.contains(membre)) {
            this.membres.add(membre);
            membre.setSeance(this);
        }
    }

    /**
     * Retirer un membre de la séance
     */
    public void retirerMembre(Membre membre) {
        if (membre != null && this.membres.contains(membre)) {
            this.membres.remove(membre);
            membre.setSeance(null);
        }
    }

    /**
     * Vérifier si un membre est inscrit à cette séance
     */
    public boolean contientMembre(Membre membre) {
        return membre != null && this.membres.contains(membre);
    }

    /**
     * Obtenir le nombre de membres inscrits
     */
    public int getNombreMembres() {
        return this.membres.size();
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
