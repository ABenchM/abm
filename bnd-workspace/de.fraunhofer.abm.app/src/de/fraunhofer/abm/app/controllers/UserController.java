package de.fraunhofer.abm.app.controllers;

import java.text.MessageFormat;
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
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.username")
public class UserController extends AbstractController implements REST {

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
	 * Account registration
	 * 
	 * @param ar
	 * @return
	 * @throws Exception
	 */
	public boolean postUsername(AccountRequest ar) throws Exception {
		// TODO: Add more security here, such as a delay, if possible.
		String approvalEndpoint = "https://abm.cs.upb.de/rest/approval";
		Map<String, String> params = ar._body();
		String username = params.get("username");
		String firstname = params.get("firstname");
		String lastname = params.get("lastname");
		String password = params.get("password");
		String email = params.get("email");
		String affiliation = params.get("affiliation");
		String approvalToken = TokenGenerator.generateToken();
		String token = MessageFormat.format("Activation Link: {0}?name={1}&token={2}\n", approvalEndpoint, username,
				approvalToken);
    
		if (!userDao.checkExists(username)) {
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
			String saltAndHash = Password.getSaltedHash(password);
			userDao.addUser(name, saltAndHash, approvalToken, email);
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
