package de.fraunhofer.abm.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.EmailConfigInterface;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.UserDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.userdel")
public class UserDelController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;

	@Reference
	private UserAdmin userAdmin;
	
	@Reference
	private Authorizer authorizer;

	@Reference
	private EmailConfigInterface config;

	interface AccountRequest extends RESTRequest {
		Map<String, String> _body();
	}

	/**
	 * Delete current user
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	/*
	public void postUserdel(String username) throws Exception {
		deleteCurrentUser(username);
		// code to handle both admin delete and user delete
		//call methods accordingly
	}
	*/
	
	public void postUserdel(AccountRequest ar) throws Exception {
		Map<String, String> params = ar._body();
		String username = params.get("username");
		authorizer.requireUser(username);
        logger.debug("Deleting user {}", username);
        try {
        	if (userDao.checkExists(username)) {
            	changeUserPubCollections(username);
    			delUserPvtCollections(username);
    			delCurrentUser(username);
            }
        } catch (Exception e) {
        	logger.info("Exception");
        }
		// code to handle both admin delete and user delete
		//call methods accordingly
	}
	
	public void changeUserPubCollections(String username) throws Exception {
        	// update created by to demo for public collections by this user
        	
    }
	
	public void delUserPvtCollections(String username) throws Exception {
    	// Delete users private collections
    	
	}
	
	@SuppressWarnings("unchecked")
	public void delCurrentUser(String username) throws Exception {
    	// Delete user info from user table
    	List<String> usernames = new ArrayList<String>();
    	usernames.add(username);
    	userDao.deleteUsers(usernames);
    	User user = (User) userAdmin.getRole(username);
		Group registeredUserGroup = (Group) userAdmin.getRole("RegisteredUser");
		registeredUserGroup.removeMember(user);
		userAdmin.removeRole(username);
	}
	
	/**
	 * Delete list of users
	 * 
	 * @param usernames
	 * @return
	 * @throws Exception
	 */
	/*public void deleteUsers(List<String> usernames) throws Exception {
        authorizer.requireRole("UserAdmin");
        logger.debug("Deleting user {}", usernames);
        boolean error = false;
        for (String user : usernames) {
        	if (!userDao.checkExists(user)) {
        		error = true;
        	}
        }
        if (!error) {
        	// Delete all private collections created by the user
        	
        	// Delete user info from user table
        	userDao.deleteUsers(usernames);
        } else {
        	//send user not found error message
        }
    }*/

	@Override
	Logger getLogger() {
		return logger;
	}

}
