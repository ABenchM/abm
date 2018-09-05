package de.fraunhofer.abm.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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
		String usernamesStr = params.get("usernames");
		List<String> usernames = new ArrayList<String>();
		if (usernamesStr.indexOf(",") == -1) {
			usernames.add(usernamesStr);
		}  else {
			usernames.addAll(Arrays.asList(usernamesStr.split(",")));
		}
		//temp code
		if (usernames.size() == 1) {
			deleteCurrentUser(usernames.get(0));
		}
		// code to handle both admin delete and user delete
		//call methods accordingly
	}
	
	
	/**
	 * Delete current user
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public void deleteCurrentUser(String username) throws Exception {
		//Map<String, String> params = ar._body();
		//String username = params.get("username");
        authorizer.requireUser(username);
        logger.debug("Deleting user {}", username);
        if (userDao.checkExists(username)) {
        	// Delete all private collections created by the user
        	
        	// Delete user info from user table
        	List<String> usernames = new ArrayList<String>();
        	usernames.add(username);
        	userDao.deleteUsers(usernames);
        }
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
