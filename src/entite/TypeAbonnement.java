package entite;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "types_abonnement")
public class TypeAbonnement implements Serializable {

    @Id
    @Column(name = "code", nullable = false , length = 20)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "montant",nullable = false)
    private int montant;

    //@OneToMany(mappedBy = "typeAbonnement")
    //private List<Abonnement> abonnements;

    public TypeAbonnement() {
    }

    public TypeAbonnement(String code, String libelle, int montant) {
        this.code = code;
        this.libelle = libelle;
        this.montant = montant;
    }

    // Getters & Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    @Override
    public String toString() {
        return libelle + " (" + montant + " €)";
    }

    // equals et hashCode basés sur le code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeAbonnement)) return false;
        TypeAbonnement that = (TypeAbonnement) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

