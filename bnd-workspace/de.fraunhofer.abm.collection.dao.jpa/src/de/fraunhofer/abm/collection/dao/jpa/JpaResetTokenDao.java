package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
	public void resetPassword(String name, String token, String password) {
		transactionControl.required(() -> {
			TypedQuery<JpaResetToken> query = em.createQuery("SELECT u FROM reset_token u WHERE u.username = :username", JpaResetToken.class);
			query.setParameter("username", name);
			JpaResetToken result = query.getSingleResult();
			if (result.token.equals(token)) {
				TypedQuery<JpaUser> updatequery = em.createQuery(
					      "UPDATE user u SET u.password = :password " +
					      "WHERE u.name =:name",JpaUser.class);
			    query.setParameter("name", name);
				query.setParameter("password", password).executeUpdate();
				
				
			}
			return null;
		});
		
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	

}
