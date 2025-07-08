package entite;

import org.w3c.dom.DOMStringList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "salle")
public class Salle extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "description")
    private String description;

//    @Transient
//    @OneToMany(mappedBy = "salle")
//    private List<Seance> seances;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<Equipement> equipements;

//    @Transient
//    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
//    private List<Horaire> horaires;

    public Salle() {
    }

    public Salle(Integer id , String libelle , String description){
        this.id = id ;
        this.libelle = libelle ;
        this.description = description;
    }

    public Salle(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public List<Seance> getSeances() {
//        return seances;
//    }
//
//    public void setSeances(List<Seance> seances) {
//        this.seances = seances;
//    }

//    public List<Equipement> getEquipements() {
//        return equipements;
//    }
//
//    public void setEquipements(List<Equipement> equipements) {
//        this.equipements = equipements;
//    }

//    public List<Horaire> getHoraires() {
//        return horaires;
//    }
//
//    public void setHoraires(List<Horaire> horaires) {
//        this.horaires = horaires;
//    }

    // equals & hashCode sur id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salle)) return false;
        Salle salle = (Salle) o;
        return Objects.equals(id, salle.id);
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
