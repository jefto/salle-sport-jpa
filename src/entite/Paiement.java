/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entite;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author TCHAMIE
 */
@Entity
@Table(name="paiement")
public class Paiement extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_paiement")
    private Integer id;

    @Column(name="date_de_paiement")
    private LocalDateTime dateDePaiement;

    @Column(name="montant")
    private int montant;

    @ManyToOne(fetch = FetchType.LAZY)
    private MoyenDePaiement moyenDePaiement;
    
    public Paiement() {

    }

    public Paiement(Integer id, LocalDateTime dateDePaiement, int montant, MoyenDePaiement moyenDePaiement) {
        this.id = id;
        this.dateDePaiement = dateDePaiement;
        this.montant = montant;
        this.moyenDePaiement = moyenDePaiement;
    }

    public Paiement(LocalDateTime dateDePaiement, int montant, MoyenDePaiement moyenDePaiement) {
        this.dateDePaiement = dateDePaiement;
        this.montant = montant;
        this.moyenDePaiement = moyenDePaiement;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateDePaiement() {
        return dateDePaiement;
    }

    public void setDateDePaiement(LocalDateTime dateDePaiement) {
        this.dateDePaiement = dateDePaiement;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public MoyenDePaiement getMoyenDePaiement() {
        return moyenDePaiement;
    }

    public void setMoyenDePaiement(MoyenDePaiement moyenDePaiement) {
        this.moyenDePaiement = moyenDePaiement;
    }


    @Override
    public String toString() {
        String datePaiement = "";
        if (dateDePaiement != null) {
            datePaiement = dateDePaiement.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return montant + " € - " + datePaiement + " (ID: " + id + ")";
    }
    
}
