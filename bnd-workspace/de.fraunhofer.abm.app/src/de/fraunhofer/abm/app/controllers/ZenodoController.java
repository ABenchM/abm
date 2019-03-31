package de.fraunhofer.abm.app.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.VersionDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;


@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.zenodo")
public class ZenodoController extends AbstractController implements REST {
	
	private static final transient Logger logger = LoggerFactory.getLogger(ZenodoController.class);
	
	@Reference
    private CollectionDao collectionDao;
	
	@Reference
	 private BuildResultDao buildResultDao;
	
	@Reference
	 private HermesResultDao hermesResultDao;
	
	 @Reference
	 private VersionDao versionDao;
	 
	 
	 @Reference
	 private Authorizer authorizer;
     
	 private static String url = "https://sandbox.zenodo.org/";
	 private static String token = "HWiH1QCdIj81fj0a9vB9knBzfH8puk55NXiEZqkumpILavP2BHgKnjgUEyc9";
	 
	  interface VersionRequest extends RESTRequest {
	        VersionDTO _body();
	    }
	 
	 public String postPublishCollection(VersionRequest vr) throws IOException
	 {
		 
		 ArrayList<String> users = new ArrayList<String>();
	  	  users.add("RegisteredUser");
	  	  users.add("UserAdmin"); 
	       authorizer.requireRoles(users);

	        VersionDTO version = vr._body();
	        if(version.id == null) {
	            sendError(vr._response(), HttpServletResponse.SC_BAD_REQUEST, "submitted version is missing an id");
	            return null;
	        }
		 
		 
		 return version.id;
	 }

	@Override
	Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
