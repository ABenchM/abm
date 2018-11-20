package de.fraunhofer.abm.app.controllers;

import java.sql.Time;
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
import de.fraunhofer.abm.collection.dao.ResetTokenDao;
import de.fraunhofer.abm.collection.dao.UserDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.resetPassword")
public class ResetPasswordController extends AbstractController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

	@Reference
	private UserDao userDao;
	
	@Reference
	private ResetTokenDao resetTokenDao;


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
	
		public boolean postResetpassword(AccountRequest ar) throws Exception {
			String resetpwdEndpoint = "http://localhost:8080/rest/approvePassword";
			Map<String, String> params = ar._body();
			String resetToken = TokenGenerator.generateToken();
			String data = params.get("usernameEmail");
		    String username = userDao.getUsername(data);
				
		    String token = MessageFormat.format("Password reset link: {0}?name={1}&token={2}\n", resetpwdEndpoint, username,
				resetToken);
			String sbj = params.get("usernameEmail") + " Password reset request";
			String msg = "Hello "+ params.get("usernameEmail") +", You have requested for reset password " + " on the ABM website.\n"
						+ "\n" + "Please reset the password for your account by clicking the link below."+"\n"+token;
	
				MimeMessage message = new MimeMessage(config.getSession());
				message.setFrom(config.getFrom());
				message.addRecipients(Message.RecipientType.TO, config.getTo());
				message.setSubject(sbj);
				message.setText(msg);
				Transport.send(message);
				long time = System.currentTimeMillis() + 600000; 
				if (resetTokenDao.checkExists(username)) {
				 resetTokenDao.updateToken(username, resetToken, time);
				} else {
				resetTokenDao.addToken(username, resetToken, time);}
				return true;
	}


	@Override
	Logger getLogger() {
		return logger;
	
}
}