package de.fraunhofer.abm.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.projectanalyzer.hermes.HermesFilter;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.hermes")
public class HermesController implements REST {

	private static final transient Logger logger = LoggerFactory.getLogger(HermesController.class);
	
	@Reference
    private Authorizer authorizer;
	
	 @Reference	
     private HermesFilter hermesFilter;
	 
	 @Reference
	 private VersionDao versionDao;
	
	public HashMap<String,Boolean> getFilters()
	{
		authorizer.requireRole("RegisteredUser");
		
		  return  hermesFilter.getFilters();
	}
	
	public int getMaxLocations()
	{
		authorizer.requireRole("RegisteredUser");
		return hermesFilter.getMaxLocation();
	}
	
	public HashMap<String,HashMap<String,Integer>> getFiFO()
	{
		authorizer.requireRole("RegisteredUser");
		return hermesFilter.getFiFO();
	}
}
