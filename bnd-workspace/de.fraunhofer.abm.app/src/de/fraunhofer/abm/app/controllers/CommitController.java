package de.fraunhofer.abm.app.controllers;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.CommitDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.commit")
public class CommitController extends AbstractController implements REST {

    private static final transient Logger logger = LoggerFactory.getLogger(CommitController.class);

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private CommitDao commitDao;

    @Reference
    private Authorizer authorizer;

    interface PostRequest extends RESTRequest {
        PostRequestDTO _body();
    }
    public static class PostRequestDTO {
        public String action;
        public String[] ids;
        public PostRequestDTO() {}
    }

    public void postCommit(PostRequest pr) {
        PostRequestDTO req = pr._body();
        String action = req.action;
        if("delete_multi".equals(action)) {
            String[] ids = req.ids;
            if(ids == null || ids.length == 0) {
                sendError(pr._response(), 400, "ids cannot be empty");
                return;
            }

            for (String id : ids) {
                if(isUserOwnerOfCommit(id)) {
                    // permissions are checked, now delete the commit
                    commitDao.delete(id);
                }
            }
        } else {
            sendError(pr._response(), 400, "unknown action " + action);
        }
    }

    interface PutRequest extends RESTRequest {
        CommitDTO _body();
    }

    public void putCommit(PutRequest pr) {
        CommitDTO commit = pr._body();
        if(isUserOwnerOfCommit(commit.id)) {
            // permissions are checked, now update the commit
            commitDao.update(commit);
        }
    }

    public void deleteCommit(String id) {
        if(isUserOwnerOfCommit(id)) {
            // permissions are checked, now delete the commit
            commitDao.delete(id);
        }
    }

    private boolean isUserOwnerOfCommit(String id) {
        authorizer.requireRole("RegisteredUser");
        CollectionDTO collection = collectionDao.findByCommit(id);
        ensureUserIsOwner(authorizer, collection);
        return true;
    }

    @Override
    Logger getLogger() {
        return logger;
    }
}
