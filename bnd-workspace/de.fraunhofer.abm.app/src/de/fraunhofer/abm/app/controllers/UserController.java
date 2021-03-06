package de.fraunhofer.abm.app.controllers;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.EmailConfigInterface;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.Password;
import de.fraunhofer.abm.app.auth.TokenGenerator;
import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.domain.UserDTO;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.FilterPinDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;
import de.fraunhofer.abm.app.auth.Authenticator; 

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.username")
public class UserController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;

	@Reference
	private CollectionDao collectionDao;
	
	@Reference
	private FilterPinDao filterPinDao;
	
	@Reference
	private Authorizer authorizer;
	
	@Reference 
	private Authenticator authenticator; 

	@Reference
	private EmailConfigInterface config;

	interface AccountRequest extends RESTRequest {
		Map<String, String> _body();
	}

	/**
	 * Account registration
	 * 
	 * @param ar
	 * @return
	 * @throws Exception
	 */
	public String postUsername(AccountRequest ar) throws Exception {
		// TODO: Add more security here, such as a delay, if possible.
		String approvalEndpoint = "https://abm.cs.upb.de/rest/approval";
		Map<String, String> params = ar._body();
 		String username = params.get("username");
 		String firstname = params.get("firstname");
 		String lastname = params.get("lastname");
 		String password = params.get("password");
 		String saltHashPassword = Password.getSaltedHash(password);
 		String email = params.get("email");
 		String affiliation = params.get("affiliation");
 		if (!userDao.checkExists(username) &&  !userDao.checkEmailExists(email) ) {
 			String approvalToken = TokenGenerator.generateToken();
 	 		String token = MessageFormat.format("Activation Link: {0}?name={1}&token={2}\n", approvalEndpoint, username, approvalToken);
 			String sbj = username + " Registered on ABM";
 			String msg = "A new user has registered the username '" + username + "' on the ABM website.\n"
 					+ "\n" + "The following information was used to register:\n" + "Name: " + firstname + " " + lastname + "\n"
 					+ "Affiliation: " + affiliation + "\n" + "Email: " + email + "\n"
 					+ token + "Please activate this account if this information seems correct.";

 			MimeMessage message = new MimeMessage(config.getSession());
			message.setFrom(config.getFrom());
			message.addRecipients(Message.RecipientType.TO, config.getTo());
			message.setSubject(sbj);
			message.setText(msg);
			Transport.send(message);
			userDao.addUser(username, firstname, lastname, email, affiliation, saltHashPassword, approvalToken);
			return "Success";
		} else {
			//throw new ApprovalException("User already exists");
			if(userDao.checkExists(username)) return "username exists";
			else if(userDao.checkEmailExists(email)) return "email exists";
			else return "some other error";
		}
	}
	
	/**
	 * User Profile
	 * 
	 * @param ar
	 * @return
	 * @throws Exception
	 */
	public UserDTO getUsername(RESTRequest rr) throws Exception {
        Map<String, String[]> params = rr._request().getParameterMap();
        String user = params.get("username")[0];
 		if (userDao.checkExists(user)) {
 			return userDao.getUserInfo(user);
		}
 		return null;
	}
	
	/**
	 * User Details Update
	 * 
	 * @param ar
	 * @return
	 * @throws Exception
	 */
	public boolean putUsername(AccountRequest ar) throws Exception {
		Map<String, String> params = ar._body();
 		String username = params.get("username");
 		String firstname = params.get("firstname");
 		String lastname = params.get("lastname");
 		String password = params.get("password");
 		String email = params.get("email");
 		String affiliation = params.get("affiliation");
 		if (userDao.checkExists(username)  && password ==null) {
			userDao.updateUser(username, firstname, lastname, email, affiliation);
			//userDao.updateUserPassword(username, saltHashPassword);
			return true;
		} else if(password !=null) {
			String saltHashPassword = Password.getSaltedHash(password);
			userDao.updateUserPassword(username, saltHashPassword);
			return true;
		}
 		else{
			// throw new ApprovalException("User does not exist");
			return false;
		}
	}
	
	public boolean getIsPasswordMatched(RESTRequest rr) throws Exception { 
	    boolean matched = false; 
	    Map<String, String[]> params = rr._request().getParameterMap(); 
	        String user = params.get("username")[0]; 
	        String password = params.get("password")[0]; 
	        matched = authenticator.authenticate(user, password); 
	    return matched; 
	         
	  } 
	
	/**
	 * Delete current user
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public void deleteUsername(String username) throws Exception {
		System.out.println("inside deleteUsername");
		authorizer.requireUser(username);
        logger.debug("Deleting user {}", username);
        try {
        	if (userDao.checkExists(username)) {
        		// delete all pinned collection entry by the user
        		collectionDao.deleteUserPinnedCollections(username);
        		// update created by to demo for public collections by this user
        		collectionDao.updateUserPublicCollections(username);
        		// delete users private collections
        		collectionDao.deleteUserPrivateCollections(username);
        		// delete any entry for the user in filterPin table
        		List<String> pinList = filterPinDao.findPins(username);
        		for (String pinId : pinList) {
        			filterPinDao.dropPin(username, pinId);
        		}
        		// delete any entry for the user in reset_token table
        		userDao.deleteUserResetToken(username);
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
