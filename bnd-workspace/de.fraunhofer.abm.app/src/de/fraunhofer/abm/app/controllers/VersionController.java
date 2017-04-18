package de.fraunhofer.abm.app.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
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
public class VersionController implements REST {
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
        authorizer.requireRole("RegisteredUser");

        VersionDTO version = vr._body();
        if("derive".equals(action)) {
            version = deriveVersion(version);
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
        }
        return version;
    }

    private VersionDTO deriveVersion(VersionDTO version) {
        version.comment = "Derived from version " + version.number + ": " + version.comment;
        version.id = UUID.randomUUID().toString();
        version.creationDate = new Date();
        version.frozen = false;
        for (CommitDTO commit : version.commits) {
            commit.id = UUID.randomUUID().toString();
        }
        version.number = findNextVersionNumberForCollection(version.collectionId);
        versionDao.save(version);
        return version;
    }

    public VersionDTO putVersion(VersionRequest vr) {
        authorizer.requireRole("RegisteredUser");
        VersionDTO version = vr._body();

        // make sure the session user is the owner
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
        String owner = databaseCollection.user;
        if(!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }

        // assign an UUID to all new commits
        for (CommitDTO commit : version.commits) {
            commit.id = Optional.ofNullable(commit.id).orElse(UUID.randomUUID().toString());
        }

        // permissions are checked, now update the version
        versionDao.update(version);
        return version;
    }

    public void deleteVersion(String id) {
        authorizer.requireRole("RegisteredUser");

        // make sure the user is the owner of the commit
        VersionDTO version = versionDao.findById(id);
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO collection = collectionDao.findById(version.collectionId);
        String owner = collection.user;
        if(!sessionUser.equals(owner)) {
            authorizer.requireRole("Admin");
        }

        // make sure this is not the only version
        if(collection.versions.size() == 1) {
            throw new RuntimeException("Cannot delete the only version");
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
}
