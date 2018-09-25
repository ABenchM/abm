package de.fraunhofer.abm.app.controllers;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.UserAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.deleteCollection")
public class DeletePublicCollectionController implements REST {
    private static final transient Logger logger = LoggerFactory.getLogger(UserAdminController.class);

    @Reference
    private UserAdmin userAdmin;

    @Reference
    private Authorizer authorizer;

    @Reference
    private CollectionDao collectionDao;
	
	public void postdeleteCollection(String collectionId) {
        authorizer.requireRole("UserAdmin");
        List<CollectionDTO> result = Collections.emptyList();

        result = collectionDao.findPublicId(collectionId);
        logger.debug("Deleting public collection {}",result.get(0).name);
        
        
     // delete the collection
        collectionDao.delete(result.get(0).id);
    }

}