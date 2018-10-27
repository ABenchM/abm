package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
	public void addToken(String name, String token, Long time) {
		transactionControl.required(() -> {
			JpaResetToken jpaToken = new JpaResetToken();
			jpaToken.username = name;
			jpaToken.token = token;
			jpaToken.expired_period = time;
			em.persist(jpaToken);
			return null;

		});
	}
	
	@Override
	public boolean checkExists(String name) {
		return transactionControl.notSupported(() -> {
			TypedQuery<JpaResetToken> query = em.createQuery("SELECT r FROM reset_token r WHERE r.username = :username", JpaResetToken.class);
			query.setParameter("username", name);
			query.setMaxResults(1);
			List<JpaResetToken> result = query.getResultList();
			return (result.size() == 1);
		});
	}
	
	@Override
	public void updateToken(String name,String token,Long time) {
		transactionControl.required(() -> {
			TypedQuery<JpaResetToken> query = em.createQuery("SELECT u FROM reset_token u WHERE u.username = :username", JpaResetToken.class);
			query.setParameter("username", name);
			JpaResetToken user = query.getSingleResult();
			user.token = token;
			user.expired_period = time;
			em.merge(user);
 		    return null;
	
	});
}
		
	@Override
	public void resetPassword(String name, String token, String password) {
		transactionControl.required(() -> {
			TypedQuery<JpaResetToken> query = em.createQuery("SELECT r FROM reset_token r WHERE r.username = :username", JpaResetToken.class);
			query.setParameter("username", name);
			JpaResetToken result = query.getSingleResult();
			long time = System.currentTimeMillis();
			if (time < result.expired_period && result.token.equals(token)) {
				updateUserPassword(name,password);
			}
			return null;
		});
		
	}
	
	@Override
	public void updateUserPassword(String name,String password) {
		transactionControl.required(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", name);
			JpaUser user = query.getSingleResult();
			user.password=password;
			em.merge(user);
 		    return null;
	
	});
}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	

}
