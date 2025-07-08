/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TCHAMIE
 */
@Entity
@Table(name = "moyens_de_paiement")
public class MoyenDePaiement extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_moyen_de_paiement")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 100 )
    private String libelle;
    
    public MoyenDePaiement(){
        
    }

    public MoyenDePaiement(Integer id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public MoyenDePaiement(String libelle) {
        this.libelle = libelle;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
