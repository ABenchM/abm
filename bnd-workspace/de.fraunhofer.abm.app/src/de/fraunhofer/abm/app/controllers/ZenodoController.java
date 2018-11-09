package de.fraunhofer.abm.app.controllers;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;


@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.zenodo")
public class ZenodoController implements REST {
	
	private static final transient Logger logger = LoggerFactory.getLogger(ZenodoController.class);
	
	@Reference
    private CollectionDao collectionDao;
	
	@Reference
	 private BuildResultDao buildResultDao;
	
	@Reference
	 private HermesResultDao hermesResultDao;
	
	 @Reference
	 private VersionDao versionDao;
	 
	
	

}
