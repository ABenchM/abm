package de.fraunhofer.abm.app.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.ProjectObjectDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.collection")
public class CollectionController extends AbstractController implements REST {

    private static final transient Logger logger = LoggerFactory.getLogger(CollectionController.class);

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private Authorizer authorizer;
    
    @Reference
    private FilterStatusDao filterDao;

    public CollectionDTO getCollection(String id) {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
        return collectionDao.findById(id);
    }

    public List<CollectionDTO> getCollection(RESTRequest rr) {
        List<CollectionDTO> result = Collections.emptyList();
        Map<String, String[]> params = rr._request().getParameterMap();
        ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        if(params.isEmpty()) {
            authorizer.requireRoles(users);
            //result = collectionDao.select();
        } else if(params.get("privateStatus") != null) {
        	if(params.get("id") != null){
        		result = collectionDao.findPublicId(params.get("id")[0]);
        	} else if(params.get("keywords") != null){
        		result = collectionDao.findPublic(params.get("keywords")[0]);
        	} else {
        		result = collectionDao.findPublic();
        	}
        } else {
        	authorizer.requireRoles(users);
            if(params.get("user") != null) {
                String requestUser = params.get("user")[0];
                authorizer.requireUser(requestUser);
                result = collectionDao.findByUser(requestUser);
            } else if(params.get("id") != null){
            	String user = SecurityContext.getInstance().getUser();
            	result = collectionDao.findPrivateId(params.get("id")[0], user);
            }
        }
        //to populate the Manage Collections for UserAdmin
        if(params.get("isAdmin")!=null) {
        	result = collectionDao.findCollections();
        }
        return result;
    }
    
    interface AccountRequest extends RESTRequest {
		Map<String, String> _body();
	}
    
    public void putCollectionstatus(AccountRequest ar) throws Exception {
  	   authorizer.requireRole("UserAdmin");
  	   Map<String, String> params = ar._body();
  		String collectionid = params.get("collectionid");
          try {
           collectionDao.activeCollection(collectionid);
          } catch (Exception e) {
          	logger.info("Exception");
          }
  }


    interface CollectionRequest extends RESTRequest {
        CollectionDTO _body();
    }

    public void postCollection(CollectionRequest cr) {
    	
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
    	
        authorizer.requireRoles(users);
       
        CollectionDTO collection = cr._body();
        collection.id = UUID.randomUUID().toString();
        collection.user = SecurityContext.getInstance().getUser();
  
        for (VersionDTO version : collection.versions) {
            version.id = Optional.ofNullable(version.id).orElse(UUID.randomUUID().toString());
            //set name and derivedFrom for initial version ( create new collection & version )
            version.name = "initial version";
            if (version.derivedFrom == null) {
				version.derivedFrom = version.id;
			}
			for(ProjectObjectDTO project : version.projects) {
            	project.id = UUID.randomUUID().toString();
            }
        }
        
        collectionDao.save(collection);
    }

    public void putCollection(CollectionRequest cr) {
    	
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
        CollectionDTO collection = cr._body();

        // make sure the session user is the owner
        CollectionDTO databaseCollection = collectionDao.findById(collection.id);
        ensureUserIsOwner(authorizer, databaseCollection);

        // permissions are checked, now update the collection
        collectionDao.update(collection);
    }

    public void deleteCollection(String id) throws IOException {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);

        // make sure the user is the owner of the collection
        CollectionDTO collection = collectionDao.findById(id);
        ensureUserIsOwner(authorizer, collection);

        // permissions are ok, we can now delete the collection, all its versions and
        // all build results
        // delete all build results of all version of this collection
        for (VersionDTO version : collection.versions) {
            if(version.frozen) {
              
                
                 //functionality to delete filternames against the version of this collection.
                 
                   filterDao.dropFilters(version.id);
                 
            }
        }

        // delete the collection
        collectionDao.delete(id);
    }
     
  

    @Override
    Logger getLogger() {
        return logger;
    }
}