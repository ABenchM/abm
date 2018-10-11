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
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import de.fraunhofer.abm.collection.dao.UserDao;

@Component
public class JpaUserDao extends AbstractJpaDao implements UserDao {

	@Reference
	TransactionControl transactionControl;

	@Reference
	private UserAdmin userAdmin;
	
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
	public void updateUser(String username, String firstname, String lastname, String email, String affiliation, String password) {
		transactionControl.required(() -> {
			JpaUser jpaUser = new JpaUser();
			jpaUser.name = username;
 			jpaUser.firstname = firstname;
 			jpaUser.lastname = lastname;
 			jpaUser.email = email;
 			jpaUser.affiliation = affiliation;
			jpaUser.password = password;
			jpaUser.locked = 0;
			em.merge(jpaUser);
			return null;
		});
	}
	
	private void deleteUserInfo(JpaUser user) {
		System.out.println(user);
		em.remove(user);
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
 			deleteUserInfo(user);
 			return null;
 		});
 	}

	@Override
 	public void deleteUsers(List<String> usernames) {
 		transactionControl.required(() -> {
 			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE u.name IN :names", JpaUser.class);
 			query.setParameter("names", usernames);
 			List<JpaUser> result = query.getResultList();
 			for (JpaUser user : result) {
 				deleteUserInfo(user);
 			}
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
	public String getUsername(String usernameemail) {
		String username = transactionControl.required(() -> {
			TypedQuery<JpaUser> query = em.createQuery("SELECT u FROM user u WHERE  (:email is null or u.email = :email) or (:name is null or u.name=:name)", JpaUser.class);
			query.setParameter("email", usernameemail);
			query.setParameter("name", usernameemail);
			
			JpaUser result = query.getSingleResult();
			if (!result.name.isEmpty()) {
				return result.name;
			}else {
				throw new ApprovalException("email not valid");
			}
		});
		return username;
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
	protected EntityManager getEntityManager() {
		return em;
	}

}