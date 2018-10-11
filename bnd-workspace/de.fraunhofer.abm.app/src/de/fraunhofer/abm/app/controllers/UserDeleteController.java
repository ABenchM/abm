package de.fraunhofer.abm.app.controllers;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.EmailConfigInterface;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.deleteUser")
public class UserDeleteController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;
	
	@Reference
	private CollectionDao collectionDao;
	
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
	public void postDeleteUser(AccountRequest ar) throws Exception {
		Map<String, String> params = ar._body();
		String username = params.get("username");
		authorizer.requireUser(username);
        logger.debug("Deleting user {}", username);
        try {
        	if (userDao.checkExists(username)) {
        		// update created by to demo for public collections by this user
        		collectionDao.updateUserPublicCollections(username);
        		// Delete users private collections
        		collectionDao.deleteUserPrivateCollections(username);
        		// Delete user info from user table
        		userDao.deleteUser(username);
            }
        } catch (Exception e) {
        	logger.info("Exception");
        }
	}
	
	@Override
	Logger getLogger() {
		return logger;
	}

}