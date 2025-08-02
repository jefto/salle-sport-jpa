package entite;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notification")
public class Notification extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer id;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "destinataire", length = 100)
    private String destinataire;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "est_lu")
    private Boolean estLu = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membre")
    private Membre membres;

    public Notification() {
        this.estLu = false;
    }

    public Notification(Integer id, LocalDateTime dateEnvoi, String type, String destinataire, String description, Membre membres) {
        this.id = id;
        this.dateEnvoi = dateEnvoi;
        this.type = type;
        this.destinataire = destinataire;
        this.description = description;
        this.membres = membres;
        this.estLu = false;
    }

    public Notification(LocalDateTime dateEnvoi, String destinataire, String description, String type) {
        this.dateEnvoi = dateEnvoi;
        this.destinataire = destinataire;
        this.description = description;
        this.type = type;
        this.estLu = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEstLu() {
        return estLu;
    }

    public void setEstLu(Boolean estLu) {
        this.estLu = estLu;
    }

    public Membre getMembres() {
        return membres;
    }

    public void setMembres(Membre membres) {
        this.membres = membres;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", destinataire='" + destinataire + '\'' +
                ", description='" + description + '\'' +
                ", estLu=" + estLu +
                '}';
    }
}