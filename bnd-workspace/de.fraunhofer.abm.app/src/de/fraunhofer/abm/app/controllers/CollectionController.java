package de.fraunhofer.abm.app.controllers;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.EmailConfiguration;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.CollectionPinDao;
import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.collection.dao.FilterPinDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.UserDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.FilterStatusDTO;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.VersionDTO;

import de.fraunhofer.abm.util.FileUtil;
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
    private BuildResultDao buildResultDao;

    @Reference
    private HermesResultDao hermesResultDao;

    @Reference
    private Authorizer authorizer;
    
    @Reference
    private FilterStatusDao filterDao;

    public CollectionDTO getCollection(String id) {
        authorizer.requireRole("RegisteredUser");
        return collectionDao.findById(id);
    }

    public List<CollectionDTO> getCollection(RESTRequest rr) {
        List<CollectionDTO> result = Collections.emptyList();
        Map<String, String[]> params = rr._request().getParameterMap();
        if(params.isEmpty()) {
            authorizer.requireRole("RegisteredUser");
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
        	authorizer.requireRole("RegisteredUser");
            if(params.get("user") != null) {
                String requestUser = params.get("user")[0];
                authorizer.requireUser(requestUser);
                result = collectionDao.findByUser(requestUser);
            } else if(params.get("id") != null){
            	String user = SecurityContext.getInstance().getUser();
            	result = collectionDao.findPrivateId(params.get("id")[0], user);
            }
        }
        return result;
    }

    interface CollectionRequest extends RESTRequest {
        CollectionDTO _body();
    }

    public void postCollection(CollectionRequest cr) {
        authorizer.requireRole("RegisteredUser");

        CollectionDTO collection = cr._body();
        collection.id = UUID.randomUUID().toString();
        collection.user = SecurityContext.getInstance().getUser();
        for (VersionDTO version : collection.versions) {
            version.id = Optional.ofNullable(version.id).orElse(UUID.randomUUID().toString());
            for(CommitDTO commit : version.commits) {
                commit.id = Optional.ofNullable(commit.id).orElse(UUID.randomUUID().toString());
            }
        }

        collectionDao.save(collection);
    }

    public void putCollection(CollectionRequest cr) {
        authorizer.requireRole("RegisteredUser");
        CollectionDTO collection = cr._body();

        // make sure the session user is the owner
        CollectionDTO databaseCollection = collectionDao.findById(collection.id);
        ensureUserIsOwner(authorizer, databaseCollection);

        // permissions are checked, now update the collection
        collectionDao.update(collection);
    }

    public void deleteCollection(String id) throws IOException {
        authorizer.requireRole("RegisteredUser");

        // make sure the user is the owner of the collection
        CollectionDTO collection = collectionDao.findById(id);
        ensureUserIsOwner(authorizer, collection);

        // permissions are ok, we can now delete the collection, all its versions and
        // all build results
        // delete all build results of all version of this collection
        for (VersionDTO version : collection.versions) {
            if(version.frozen) {
                BuildResultDTO buildResult = buildResultDao.findByVersion(version.id);
                FileUtil.deleteRecursively(new File(buildResult.dir));
                buildResultDao.delete(buildResult.id);
                
                //functionality to delete Hermes results as well by Ankur Gupta on 23.08.2017
                 HermesResultDTO hermesResult = hermesResultDao.findByVersion(version.id);
                 hermesResultDao.delete(hermesResult.id);
                
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
