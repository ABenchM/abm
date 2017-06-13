package de.fraunhofer.abm.app.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.VersionDTO;

public abstract class AbstractController {

    void sendError(HttpServletResponse resp, int code, String mesg) {
        resp.setStatus(code);
        try {
            resp.getWriter().println(mesg);
        } catch (IOException e) {
            getLogger().error("Couldn't send error response", e);
        }
    }

    abstract Logger getLogger();

    protected void ensureUserIsOwner(Authorizer authorizer, CollectionDTO collection) {
        // make sure the session user is the owner
        String sessionUser = SecurityContext.getInstance().getUser();
        String owner = collection.user;
        if(!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }
    }

    protected void ensureUserIsOwner(Authorizer authorizer, CollectionDao collectionDao, VersionDTO version) {
        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
        ensureUserIsOwner(authorizer, databaseCollection);
    }
}
