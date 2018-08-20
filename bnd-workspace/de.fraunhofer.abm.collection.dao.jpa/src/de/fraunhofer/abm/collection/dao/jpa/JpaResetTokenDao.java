package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.ResetTokenDao;

@Component
public class JpaResetTokenDao extends AbstractJpaDao implements ResetTokenDao {

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
	public void addToken(String name, String token) {
		transactionControl.required(() -> {
			JpaResetToken jpaToken = new JpaResetToken();
			jpaToken.username = name;
			jpaToken.token = token;
			em.persist(jpaToken);
			return null;

		});
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
