package de.fraunhofer.abm.app.controllers;

import java.util.Map;


import javax.mail.*;  
import javax.mail.internet.*;  
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.app.EmailConfigInterface;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.Password;
import de.fraunhofer.abm.collection.dao.UserDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.username")
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
     * @param ar
     * @return
     * @throws Exception
     */
    public boolean postUsername(AccountRequest ar) throws Exception{
    	//TODO: Add more security here, such as a delay, if possible.
        Map<String, String> params = ar._body();
        String name = params.get("username");
        String password = params.get("password");
    	
    	if(!userDao.checkExists(name)){
    		String sbj = params.get("username") + " Registered on ABM";
    		String msg = "A new user has registered the username '" + params.get("username") + "' on the ABM website.\n" +
    				"\n" +
					"The following information was used to register:\n" +
					"Name: " + params.get("name") + "\n" +
					"Affiliation: " + params.get("affiliation") + "\n" +
					"Email: " + params.get("email") + "\n" +
					"\n" +
					"Please activate this account if this information seems correct.";
    		
    		MimeMessage message = new MimeMessage(config.getSession());  
            message.setFrom(config.getFrom());
            message.addRecipients(Message.RecipientType.TO, config.getTo());  
            message.setSubject(sbj);  
            message.setText(msg);
            Transport.send(message);
            
    		String saltAndHash = Password.getSaltedHash(password);
    		userDao.addUser(name, saltAndHash);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Approval of a user by the admin using token
     * @param token Web token for authentication using url.
     */
    public void getUserApproval(String token) {
    	
    }
    
	@Override
	Logger getLogger() {
		return logger;
	}

}
