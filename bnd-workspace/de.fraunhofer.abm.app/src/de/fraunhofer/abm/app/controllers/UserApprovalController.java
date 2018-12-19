package de.fraunhofer.abm.app.controllers;

import java.util.Map;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.EmailConfigInterface;
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
	
	@Reference
	private EmailConfigInterface config;
	
	@Reference
	private Authorizer authorizer;
	
	interface AccountRequest extends RESTRequest {
 		Map<String, String> _body();
 	}

	/**
	 * Approval of a user by the admin using token
	 * 
	 * @params name,token
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void getApproval(RESTRequest rr) {
		try {
			Map<String, String[]> params = rr._request().getParameterMap();
			String name = getIfValid(params.get("name"));
			String token = getIfValid(params.get("token"));
			String password = userDao.approveToken(name, token);
			logger.debug("Creating user {}", name);
			User user = (User) userAdmin.createRole(name, Role.USER);
			user.getCredentials().put("password", password);
			Group registeredUserGroup = (Group) userAdmin.getRole("RegisteredUser");
			registeredUserGroup.addMember(user);
			String userEmail = userDao.getEmailId(name);
			sendApproveRejectEmail(name, true, userEmail);
		} catch (Exception e) {
			// return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
		}
	}
	
	@SuppressWarnings("unchecked")
 	public void putApproval(AccountRequest ar) {
 		// user approve or reject by admin
 		try {
 			authorizer.requireRole("UserAdmin");
 			Map<String, String> params = ar._body();
 			String[] userList = params.get("userList").split(","); 
 			for (String username: userList) {
 	 			boolean isApprove = params.get("isApprove").equals("true") ? true : false;
 	 			if ( isApprove ) {
 	 				String token = userDao.getUserToken(username);
 	 				String password = userDao.approveToken(username, token);
 	 				logger.debug("Creating user {}", username);
 	 				User user = (User) userAdmin.createRole(username, Role.USER);
 	 				user.getCredentials().put("password", password);
 	 				Group registeredUserGroup = (Group) userAdmin.getRole("RegisteredUser");
 	 				registeredUserGroup.addMember(user);
 	 				String userEmail = userDao.getEmailId(username);
 	 				sendApproveRejectEmail(username, true, userEmail);
 	 			} else {
 	 				// TODO: implement deleteuser and uncomment below lines
 	 				// String userEmail = userDao.getEmailId(username);
 	 				// userDao.deleteUser(username);
 	 				// sendApproveRejectEmail(username, false, userEmail);
 	 			}
 			}
 		} catch (Exception e) {
 			// return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
 		}
 	}
	
	public void sendApproveRejectEmail(String username, Boolean isApprove, String userEmail) throws Exception {
		InternetAddress[] addressList = InternetAddress.parse(userEmail);
		String sbj= null;
		String msg= null;
		if(isApprove) {
			sbj = "'"+ username + "' successfully registered on ABM";
			 msg = "Dear " + username + ",\n\n"+"You have been successfully registered on ABM.\n";
		}
		else {
			sbj = "Rejected '" + username + "' registration on ABM";
		    msg = "Dear " + username + ",\n\n"+"You have been rejected on ABM.\n\n"
					+ "Please contact the ABM admin for further details.\n";
		}
		MimeMessage message = new MimeMessage(config.getSession());
		message.setFrom(config.getFrom());
		message.addRecipients(Message.RecipientType.TO, addressList);
		message.setSubject(sbj);
		message.setText(msg);
		Transport.send(message);
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
