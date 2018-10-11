package de.fraunhofer.abm.app.controllers;

import java.io.IOException;
import java.util.ArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.deletepubliccollection")
public class DeletePublicCollectionController extends AbstractController implements REST {
    private static final transient Logger logger = LoggerFactory.getLogger(UserAdminController.class);

    @Reference
    private Authorizer authorizer;

    @Reference
    private CollectionDao collectionDao;
    
   
    public boolean deleteDeletepubliccollection(String id) throws IOException{
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("Admin");
    	users.add("UserAdmin");
    	authorizer.requireRoles(users);
    	//id = "3b847907-b97f-481e-a1b4-fea328a9d3ee";
        
        logger.debug("deleting collection with"+id);
    	// delete the collection
    	collectionDao.delete(id);
        return true;
    }

	@Override
	Logger getLogger() {
		// TODO Auto-generated method stub
		return logger;
	}

}