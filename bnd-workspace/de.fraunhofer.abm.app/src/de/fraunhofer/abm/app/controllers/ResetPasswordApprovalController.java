package de.fraunhofer.abm.app.controllers;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Password;
import de.fraunhofer.abm.collection.dao.ResetTokenDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.approvePassword")
public class ResetPasswordApprovalController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private ResetTokenDao resettokenDao;
	@Reference
	private UserAdmin userAdmin;

	interface ApprovalPasswordRequest extends RESTRequest {
		Map<String, String> _body();
	}

	public boolean postApprovePassword(ApprovalPasswordRequest approveReq) {
		Map<String, String> payload = approveReq._body();
		String password = payload.get("password");
		String confirmPassword = payload.get("confirmPassword");
		if (password.equals(confirmPassword)) {
			try {

				// Get this password from the user entered box
				String saltHashPassword = Password.getSaltedHash(password);
				String name = payload.get("username");
				String token = payload.get("token");
				// Check and update the password
				boolean reset = resettokenDao.resetPassword(name, token, saltHashPassword);
				if (reset) {
					User userRole = (User) userAdmin.getRole(name);
					@SuppressWarnings("unchecked")
					Dictionary<String, String> cred = userRole.getCredentials();
					cred.put("password", saltHashPassword);
					System.out.println(userRole.getCredentials().get("password"));
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	Logger getLogger() {
		return logger;
	}
}
