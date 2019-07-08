package de.fraunhofer.abm.app.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.ProjectObjectDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.version")
public class VersionController extends AbstractController implements REST {
    private static final transient Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private VersionDao versionDao;

   
    @Reference
    private Authorizer authorizer;

    interface VersionRequest extends RESTRequest {
        VersionDTO _body();
    }

    /**
     * Derives a new version from the given one. The version ID and
     * the IDs of all commit are set to new random UUIDs. The version
     * number is set to the next available version for this collection.
     *
     * @param vr
     * @param derive
     */
    public VersionDTO postVersion(VersionRequest vr) {
    	ArrayList<String> users = new ArrayList<String>();
  	  users.add("RegisteredUser");
  	  users.add("UserAdmin"); 
        authorizer.requireRoles(users);

        VersionDTO version = vr._body();
        if(version.id == null) {
            sendError(vr._response(), HttpServletResponse.SC_BAD_REQUEST, "submitted version is missing an id");
            return null;
        }
        try {
            version = deriveVersion(version);
        } catch(IllegalArgumentException e ) {
            sendError(vr._response(), HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
            return null;
        }
        return version;
    }

    private VersionDTO deriveVersion(VersionDTO version) {
        ensureUserIsOwner(authorizer, collectionDao, version);

        version.comment = "Derived from version " + version.number + ": " + version.comment;
        version.derivedFrom = version.id;
        version.name =   version.name;
        version.id = UUID.randomUUID().toString();
        version.creationDate = new Date();
        version.frozen = false;
        version.privateStatus = true;
        for(ProjectObjectDTO project: version.projects) {
        	project.id = UUID.randomUUID().toString();
        }
        /*for (CommitDTO commit : version.commits) {
            commit.id = UUID.randomUUID().toString();
        }*/
        try {
            version.number = findNextVersionNumberForCollection(version.collectionId);
        } catch(Exception e) {
            throw new IllegalArgumentException("could not find next version number for given collection");
        }
        versionDao.save(version);
        return version;
    }

    public VersionDTO putVersion(VersionRequest vr) throws SQLException {
    	ArrayList<String> users = new ArrayList<String>();
  	    users.add("RegisteredUser");
  	    users.add("UserAdmin"); 
        authorizer.requireRoles(users);
        VersionDTO version = vr._body();

        ensureUserIsOwner(authorizer, collectionDao, version);
        // permissions are checked, now update the version

        // assign an UUID to all new commits
        for (ProjectObjectDTO project : version.projects) {
        	project.id = Optional.ofNullable(project.id).orElse(UUID.randomUUID().toString());
//             if (checkIfProjectExists(project)) {
//            	version.comment = "Project Exists";
//            	return version; 
//             }
        }
        
        
       
        versionDao.update(version);
        return version;
    }

    private boolean checkIfProjectExists(ProjectObjectDTO project) {
    
    	  	if(versionDao.findProjectByVersionId(project.version_id, project.project_id)) {
    		return true;
    	}
    	return false;
    }
    
    public void deleteVersion(RESTRequest rr, String id) {
    	ArrayList<String> users = new ArrayList<String>();
  	    users.add("RegisteredUser");
  	    users.add("UserAdmin"); 
        authorizer.requireRoles(users);
        VersionDTO version = versionDao.findById(id);
        ensureUserIsOwner(authorizer, collectionDao, version);

        // make sure this is not the only version
        CollectionDTO collection = collectionDao.findById(version.collectionId);
        if(collection.versions.size() == 1) {
            sendError(rr._response(), HttpServletResponse.SC_BAD_REQUEST, "Cannot delete the only version");
            return;
        }

        // permissions are checked, now delete the collection
        versionDao.delete(id);
    }

    private int findNextVersionNumberForCollection(String collectionId) {
        int maxVersion = 1;
        CollectionDTO collection = collectionDao.findById(collectionId);
        for (VersionDTO version : collection.versions) {
            maxVersion = Math.max(maxVersion, version.number);
        }
        return ++maxVersion;
    }
    
    public CollectionDTO getVersionDetails(String versionId) {
        CollectionDTO result = null;
        ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        if ( versionId != null ) {
            authorizer.requireRoles(users);
            result = collectionDao.getVersionDetails(versionId);
            if(result == null) {
            	throw new HTTPException(400);
            }
        }
        return result;
    }
    
    public List<VersionDTO> getCollectionVersions(String collectionId) {
    	
    	List<VersionDTO> result = new ArrayList<VersionDTO>();
    	result = versionDao.findByCollectionId(collectionId);
    	
    	return result;
    }

    @Override
    Logger getLogger() {
        return logger;
    }
}