package de.fraunhofer.abm.app.controllers;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.RoleConstants;
import de.fraunhofer.abm.collection.dao.UserDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.approval")
public class UserApprovalController extends AbstractController implements REST {
	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;
	@Reference
	private UserAdmin userAdmin;

	/**
	 * Approval of a user by the admin using token
	 * 
	 * @param token
	 *            Web token for authentication using url.
	 */
	@SuppressWarnings("unchecked")
	public String getApproval(RESTRequest rr) {
		Map<String, String[]> params = rr._request().getParameterMap();
		String name = getIfValid(params.get("name"));
		String token = getIfValid(params.get("token"));
		String password = userDao.approveToken(name, token);
		logger.debug("Creating user {}", name);
		User user = (User)userAdmin.createRole(name, Role.USER);
		user.getCredentials().put("password", password);
		Group registeredUserGroup = (Group) userAdmin.getRole(RoleConstants.REGISTERED_USER);
		registeredUserGroup.addMember(user);
		return "User has been approved";
	}

	private String getIfValid(String[] data) {
		if (data != null && data.length == 1) {
			return data[0];
		}
		throw new ArrayIndexOutOfBoundsException("Invalid param key");
	}

	@Override
	Logger getLogger() {
		return logger;
	}
}
