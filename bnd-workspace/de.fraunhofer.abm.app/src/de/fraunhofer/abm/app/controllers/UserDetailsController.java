package de.fraunhofer.abm.app.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.domain.UserDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.userList")
public class UserDetailsController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;
	
	@Reference
	private Authorizer authorizer;

	/**
	 * Get All Users by approval status current user
	 * 
	 * @param isApproved
	 * @return
	 * @throws Exception
	 */
	public List<UserDTO> getUserList(RESTRequest rr) throws Exception {
		
        authorizer.requireRole("UserAdmin");
	//authorizer.requireRole("RegisteredUser");
        List<UserDTO> result = Collections.emptyList();
        Map<String, String[]> params = rr._request().getParameterMap();
        int isApproved = params.get("approved")[0].equals("0") ? 0 : 1;
        String adminuser = params.get("username")[0];
        result = userDao.getAllUsers(isApproved, adminuser);
        return result;
    }

	@Override
	Logger getLogger() {
		return logger;
	}

}
