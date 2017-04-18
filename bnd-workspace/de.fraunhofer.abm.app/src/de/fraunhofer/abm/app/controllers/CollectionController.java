package de.fraunhofer.abm.app.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.util.FileUtil;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.collection")
public class CollectionController implements REST {

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private BuildResultDao buildResultDao;

    @Reference
    private Authorizer authorizer;

    public CollectionDTO getCollection(String id) {
        authorizer.requireRole("RegisteredUser");
        return collectionDao.findById(id);
    }

    public List<CollectionDTO> getCollection(RESTRequest rr) {
        authorizer.requireRole("RegisteredUser");

        List<CollectionDTO> result = Collections.emptyList();
        Map<String, String[]> params = rr._request().getParameterMap();
        if(params.isEmpty()) {
            result = collectionDao.select();
        } else {
            if(params.get("user") != null) {
                String requestUser = params.get("user")[0];
                result = collectionDao.findByUser(requestUser);
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
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO databaseCollection = collectionDao.findById(collection.id);
        String owner = databaseCollection.user;
        if(!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }

        // permissions are checked, now update the collection
        collectionDao.update(collection);
    }

    public void deleteCollection(String id) throws IOException {
        authorizer.requireRole("RegisteredUser");

        // make sure the user is the owner of the collection
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO collection = collectionDao.findById(id);
        String owner = collection.user;
        if(!sessionUser.equals(owner)) {
            authorizer.requireRole("Admin");
        }

        // permissions are ok, we can now delete the collection, all its versions and
        // all build results

        // delete all build results of all version of this collection
        for (VersionDTO version : collection.versions) {
            if(version.frozen) {
                BuildResultDTO buildResult = buildResultDao.findByVersion(version.id);
                FileUtil.deleteRecursively(new File(buildResult.dir));
                buildResultDao.delete(buildResult.id);
            }
        }

        // delete the collection
        collectionDao.delete(id);
    }
}
