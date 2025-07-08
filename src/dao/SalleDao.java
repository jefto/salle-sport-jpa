package dao;

import entite.Salle;
import service.GenericService;

import javax.persistence.Query;
import java.util.List;

public class SalleDao extends GenericDao<Salle, Integer> {
    public SalleDao(){
        super();
        classEntity = Salle.class;
        PrimaryKeyName = "id";
    }
}
