package de.fraunhofer.abm.app.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.RoleConstants;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
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
@Component(name="de.fraunhofer.abm.rest.version")
public class VersionController extends AbstractController implements REST {
    private static final transient Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private VersionDao versionDao;

    @Reference
    private BuildResultDao buildResultDao;

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
    public VersionDTO postVersion(VersionRequest vr, String action) {
        authorizer.requireRole(RoleConstants.REGISTERED_USER);

        VersionDTO version = vr._body();
        if("derive".equals(action)) {
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
        } else if("unfreeze".equals(action)) {
            BuildResultDTO dto = buildResultDao.findByVersion(version.id);
            if(dto != null) {
            	
                // delete build from disk
                File buildDir = new File(dto.dir);
                try {
                    FileUtil.deleteRecursively(buildDir);
                    // delete the build result from db
                    buildResultDao.delete(dto.id);
                } catch (IOException e) {
                    logger.error("Couldn't delete build directory", e);
                    throw new RuntimeException("Couldn't delete build directory");
                }
            }

            // unfreeze version
            version.frozen = false;
            versionDao.update(version);
        } else {
            sendError(vr._response(), HttpServletResponse.SC_BAD_REQUEST, "unknown action " + action);
            return null;
        }
        return version;
    }

    private VersionDTO deriveVersion(VersionDTO version) {
        ensureUserIsOwner(authorizer, collectionDao, version);

        version.comment = "Derived from version " + version.number + ": " + version.comment;
        version.id = UUID.randomUUID().toString();
        version.creationDate = new Date();
        version.frozen = false;
        for (CommitDTO commit : version.commits) {
            commit.id = UUID.randomUUID().toString();
        }
        try {
            version.number = findNextVersionNumberForCollection(version.collectionId);
        } catch(Exception e) {
            throw new IllegalArgumentException("could not find next version number for given collection");
        }
        versionDao.save(version);
        return version;
    }

    public VersionDTO putVersion(VersionRequest vr) {
        authorizer.requireRole(RoleConstants.REGISTERED_USER);
        VersionDTO version = vr._body();

        ensureUserIsOwner(authorizer, collectionDao, version);
        // permissions are checked, now update the version

        // assign an UUID to all new commits
        for (CommitDTO commit : version.commits) {
            commit.id = Optional.ofNullable(commit.id).orElse(UUID.randomUUID().toString());
        }

        versionDao.update(version);
        return version;
    }

    public void deleteVersion(RESTRequest rr, String id) {
        authorizer.requireRole(RoleConstants.REGISTERED_USER);
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

    @Override
    Logger getLogger() {
        return logger;
    }
}
