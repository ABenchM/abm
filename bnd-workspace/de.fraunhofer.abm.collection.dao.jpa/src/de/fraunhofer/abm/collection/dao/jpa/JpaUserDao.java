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
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.domain.UserDTO;

@Component
public class JpaUserDao extends AbstractJpaDao implements UserDao {

	@Reference
	TransactionControl transactionControl;

	@Reference(name = "provider")
	JPAEntityManagerProvider jpaEntityManagerProvider;

	@Reference
	private UserAdmin userAdmin;
	
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
	public boolean checkLocked(String name) {
		return transactionControl.notSupported(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", name);
			JpaUser result = query.getSingleResult();
			return (result.locked == 1);
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
	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token) {
		transactionControl.required(() -> {
			JpaUser jpaUser = new JpaUser();
			jpaUser.name = username;
 			jpaUser.firstname = firstname;
 			jpaUser.lastname = lastname;
 			jpaUser.email = email;
 			jpaUser.affiliation = affiliation;
			jpaUser.password = password;
			jpaUser.approved = 0;
			jpaUser.locked = 0;
 			jpaUser.token = token;
			em.persist(jpaUser);
			return null;
		});
	}

	@Override
	public void updateUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token) {
		transactionControl.required(() -> {
			JpaUser jpaUser = new JpaUser();
			jpaUser.name = username;
 			jpaUser.firstname = firstname;
 			jpaUser.lastname = lastname;
 			jpaUser.email = email;
 			jpaUser.affiliation = affiliation;
			jpaUser.password = password;
			jpaUser.locked = 0;
			jpaUser.token = token;
			em.merge(jpaUser);
			return null;
		});
	}
	
	private void deleteUserInfo(JpaUser user) {
		em.remove(user);
	}
	
	private void deleteUserRoleInfo(JpaUser user) {
 		User userRole = (User) userAdmin.getRole(user.name);
 		Group registeredUserGroup = (Group) userAdmin.getRole("RegisteredUser");
 		registeredUserGroup.removeMember(userRole);
 		userAdmin.removeRole(user.name);
 	}
	
	@Override
 	public void deleteUser(String username) {
 		transactionControl.required(() -> {
 			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", username);
			JpaUser user = query.getSingleResult();
			if (user.approved == 1) {
  				deleteUserRoleInfo(user);
  			}
			deleteUserInfo(user);
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
	public UserDTO getUserInfo(String username) {
		return transactionControl.notSupported(() -> {
			String queryStr = "select a.name as username, a.firstname, a.lastname, a.locked, a.email, a.affiliation, a.approved, b.role "
					+ "from user a, role_members b where a.name = :name and a.name = b.username";
			Query query = em.createQuery(queryStr);
            query.setParameter("name", username);
            UserDTO user = new UserDTO();
			List<UserDTO> userList = new ArrayList<UserDTO>();
            @SuppressWarnings("unchecked")
			List<Object[]> resultList = query.getResultList();
            for (Object[] result : resultList) {
        		user.username = (String) result[0];
         		user.firstname = (String) result[1];
         		user.lastname = (String) result[2];
         		user.locked = ((int) result[3] == 0) ? false : true;
         		user.email = (String) result[4];
         		user.affiliation = (String) result[5];
         		user.approved = ((int) result[6] == 0) ? false : true;
       			user.role = (String) result[7];
        		userList.add(user);
            }
			if (user.username.equals(username)) {
				return user;
			} else {
				throw new ApprovalException("Invalid User");
			}
		});
	}
	
	public void lockunlockUser(String username,String isLock) {
		transactionControl.required(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", username);
			JpaUser result = query.getSingleResult();
			if(isLock.equals("true")) {
				result.locked=1;
			}else {
				result.locked=0;
			}
			em.merge(result);
			return null;
			
			
		});
		
}
	
	@Override
	public void updateRole(String username,String role) {
		transactionControl.required(() -> {
			Query updateRole = em.createNativeQuery("UPDATE role_members r SET r.member_parent= :value1 where r.member_member = :value2").setParameter("value1", role).setParameter("value2", username);
			updateRole.executeUpdate();
			return null;
		});
	}
	
	@Override
	public String getEmailId(String username) {
		String emailId = transactionControl.required(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name = :name", JpaUser.class);
			query.setParameter("name", username);
			JpaUser result = query.getSingleResult();
			if (!result.email.isEmpty()) {
				return result.email;
			}else {
				throw new ApprovalException("email not valid");
			}
		});
		return emailId;
	}
	
	@Override
 	public String getUserToken(String user) {
 		String token = transactionControl.required(() -> {
 			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name=:name", JpaUser.class);
 			query.setParameter("name", user);
 			
 			JpaUser result = query.getSingleResult();
 			if (!result.name.isEmpty()) {
 				return result.token;
 			}else {
 				throw new ApprovalException("user not registered");
 			}
 		});
 		return token;
 	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
