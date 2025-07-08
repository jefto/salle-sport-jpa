package dao.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;
import java.util.function.Function;


public class DaoProxy {
    private EntityManager entityManager;

    public DaoProxy(EntityManager em){
        this.entityManager = em ;
    }
    public void  executeConsumer(Consumer<EntityManager> action ){
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        action.accept(entityManager);
        transaction.commit();
}


    public <R> R executeFunction(Function<EntityManager , R> function) {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        R result = function.apply(entityManager);
        transaction.commit();
        return result;
    }
}
