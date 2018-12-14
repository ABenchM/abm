package de.fraunhofer.abm.app.controllers;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.UserDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.roleupdate")
public class UpdateRoleController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;

	@Reference
	private UserAdmin userAdmin;
	
	@Reference
	private Authorizer authorizer;
	


	
	interface AccountRequest extends RESTRequest {
		Map<String, String> _body();
	}

	
	
	
	public void postRoleupdate(AccountRequest ar) throws Exception {
		authorizer.requireRole("UserAdmin");
	    Map<String, String> params = ar._body();
		String username = params.get("username");
		String newrole = params.get("rolename");
        try {
        	if (userDao.checkExists(username)) {
        		userDao.updateRole(username, newrole);
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