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

import de.fraunhofer.abm.collection.dao.UserDao;

@Component
public class JpaUserDao extends AbstractJpaDao implements UserDao {

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
	public boolean checkExists(String name) {
		return transactionControl.notSupported(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", name);
			query.setMaxResults(1);
			List<JpaUser> result = query.getResultList();
			return (result.size() == 1);
		});
	}

	@Override
	public boolean checkApproved(String name) {
		return transactionControl.notSupported(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", name);
			JpaUser result = query.getSingleResult();
			return (result.approved == 1);
		});
	}

	@Override
	public void addUser(String name, String password, String approvalToken) {
		transactionControl.required(() -> {
			JpaUser jpaUser = new JpaUser();
			jpaUser.name = name;
			jpaUser.password = password;
			jpaUser.approved = 0;
			jpaUser.token = approvalToken;
			em.persist(jpaUser);
			return null;
		});
	}

	@Override
	public String approveToken(String name, String token) {
		String password = transactionControl.required(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", name);
			JpaUser result = query.getSingleResult();
			if (result.approved == 1) {
				throw new ApprovalException("User already approved");
			}
			if (result.token.equals(token)) {
				result.approved = 1;
				em.persist(result);
				return result.password;
			}
			throw new ApprovalException("Invalid token");
		});
		return password;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
