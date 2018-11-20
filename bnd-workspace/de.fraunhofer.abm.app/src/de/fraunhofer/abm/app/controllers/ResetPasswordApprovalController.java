package de.fraunhofer.abm.app.controllers;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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
	
	
	public void getApprovePassword(RESTRequest rr) {
		try {
			//Get this password from the user entered box
			String password ="Ironman";
			String saltHashPassword = Password.getSaltedHash(password);
			Map<String, String[]> params = rr._request().getParameterMap();
			String name = getIfValid(params.get("name"));
			String token = getIfValid(params.get("token"));
			//Check  and update the password
			resettokenDao.resetPassword(name, token, saltHashPassword);
		}
	  catch (Exception e) {
		e.getStackTrace();
	}
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
	

