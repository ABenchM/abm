package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.FilterPinDao;


@Component
public class JpaFilterPinDao extends AbstractJpaDao implements FilterPinDao {

    @Reference
    TransactionControl transactionControl;

    @Reference(name = "provider")
    JPAEntityManagerProvider jpaEntityManagerProvider;

    EntityManager em;

    @Activate
    void start(Map<String, Object> props) {
        try {
            em = jpaEntityManagerProvider.getResource(transactionControl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public List<String> findPins(String user) {
		return transactionControl.notSupported(() -> {
			TypedQuery<String> query = em.createQuery("SELECT p.id FROM filterPin p WHERE p.user = :user", String.class);
            query.setParameter("user", user);
            List<String> idList = query.getResultList();
            return idList;
		});
	}

	@Override
	public void addPin(String user, String id) {
		transactionControl.required(() -> {
			Query newPin = em.createNativeQuery("INSERT INTO filterPin VALUES (:user, :id)");
			newPin.setParameter("user", user);
			newPin.setParameter("id", id);
			newPin.executeUpdate();
            return null;
        });
	}

	@Override
	public void dropPin(String user, String id) {
		transactionControl.required(() -> {
			Query oldPin = em.createNativeQuery("DELETE FROM filterPin WHERE user =:user AND id = :id");
			oldPin.setParameter("user", user);
			oldPin.setParameter("id", id);
			oldPin.setMaxResults(1);
			oldPin.executeUpdate();
            return null;
        });

	}



    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
