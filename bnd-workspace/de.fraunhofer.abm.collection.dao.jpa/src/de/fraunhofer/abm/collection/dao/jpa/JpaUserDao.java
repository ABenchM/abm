package de.fraunhofer.abm.collection.dao.jpa;

import java.util.ArrayList;
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

import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.domain.UserDTO;

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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserDTO> getAllUsers(int isApproved) {
    	return transactionControl.notSupported(() -> {
    		String queryCompany = "";         		
    		if ( isApproved == 1 ) {
    			queryCompany = "select a.name as username, a.firstname, a.lastname, a.locked, a.email, a.affiliation, a.approved, b.role "
    							+ "from user a, role_members b where a.approved = :isApproved and a.name = b.username";
    		} else {
    			queryCompany = "select a.name as username, a.firstname, a.lastname, a.locked, a.email, a.affiliation, a.approved "
    							+ "from user a where a.approved = :isApproved";
    		}
            Query query = em.createQuery(queryCompany);
            query.setParameter("isApproved", isApproved);
            List<UserDTO> userList = new ArrayList<UserDTO>();
            List<Object[]> resultList = query.getResultList();
            for (Object[] result : resultList) {
            	UserDTO user = new UserDTO();
        		user.username = (String) result[0];
         		user.firstname = (String) result[1];
         		user.lastname = (String) result[2];
         		user.locked = ((int) result[3] == 0) ? false : true;
         		user.email = (String) result[4];
         		user.affiliation = (String) result[5];
         		user.approved = ((int) result[6] == 0) ? false : true;
         		if ( isApproved == 1 ) {
         			user.role = (String) result[7];
         		}
        		userList.add(user);
            }
            return userList;
        });
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
