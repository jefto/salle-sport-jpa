package entite;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "equipement")
public class Equipement extends GenericEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salle", nullable = false)
    private Salle salle;


    public Equipement() {
    }

    public Equipement(String libelle, String description, Salle salle) {
        this.libelle = libelle;
        this.description = description;
        this.salle = salle;
    }

    // Getters & Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    // equals & hashCode basés sur id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipement)) return false;
        Equipement that = (Equipement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return libelle;
    }
}
