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
@Table(name="horaire")
public class Horaire extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_horaire")
    private int id;

    @Column(name="date_debut")
    private LocalDateTime debut;

    @Column(name="date_fin")
    private LocalDateTime fin;


    public Horaire() {
    }

    public Horaire(int id, LocalDateTime debut, LocalDateTime fin) {
        this.id = id;
        this.debut = debut;
        this.fin = fin;
    }

    public Horaire(LocalDateTime debut, LocalDateTime fin) {
        this.debut = debut;
        this.fin = fin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
}
