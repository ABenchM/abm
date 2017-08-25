package de.fraunhofer.abm.app.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.app.controllers.CollectionController.CollectionRequest;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.FilterStatusDTO;
import de.fraunhofer.abm.domain.HermesBuildDTO;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.HermesStepDTO;
import de.fraunhofer.abm.domain.QueriesDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.hermes.Hermes;
import de.fraunhofer.abm.hermes.HermesProcess;
import de.fraunhofer.abm.projectanalyzer.hermes.HermesFilter;
import de.fraunhofer.abm.util.FileUtil;
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
	private Hermes hermes;
	
	@Reference
    private CollectionDao collectionDao;
	
	 @Reference
	 private FilterStatusDao filterDao;
	
	 @Reference	
     private HermesFilter hermesFilter;
	 
	 @Reference
	 private HermesResultDao hermesResultDao;
	 
	 @Reference
	 private VersionDao versionDao;
	 
	 
	 interface VersionRequest extends RESTRequest {
	        VersionDTO _body();
	    }
	 
	 //Function to get the Hermes Instance details for the version.
	 public HermesResultDTO getHermesInstance(String versionId) throws Exception{
		 
		 authorizer.requireRole("RegisteredUser");
		 String sessionUser = SecurityContext.getInstance().getUser();
	        VersionDTO version = versionDao.findById(versionId);
	        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
	        String owner = databaseCollection.user;
	        if (!owner.equals(sessionUser)) {
	            authorizer.requireRole("Admin");
	        }
		 
	        HermesResultDTO dto = hermesResultDao.findByVersion(versionId);
	       
	        
		 return dto;
	 }
	 
	 //Function to get the list of Hermes instances running for the user.
	 public List<Map<String,String>> getHermesInstances(String user)
	 {
		 authorizer.requireUser(user);
		 List<HermesResultDTO> hermesInstances = hermesResultDao.findRunning(user);
		 List<Map<String,String>> results = new ArrayList<>();
		 
		 for (HermesResultDTO dto:hermesInstances)
		 {
			 Map<String,String> map = new HashMap<String,String>();
			 VersionDTO version = versionDao.findById(dto.versionId);
	         CollectionDTO collection = collectionDao.findById(version.collectionId);
	         
	            map.put("id", dto.versionId);
	            map.put("name", collection.name);
	            map.put("versionNum", String.valueOf(version.number));
	            map.put("progress", String.valueOf(hermesProgress(dto)));
	            map.put("hermesStatus", dto.status);
	            results.add(map);
	               
		 }
		 
		 return results;
		 
	 }
	 
	 interface FilterVersionRequest extends RESTRequest {
	        FilterStatusDTO _body();
	    }
	 
	 //Function to get the list of active Filters for the selected version.
	 public FilterStatusDTO getactiveFiltersByVersion(String versionid)
	 { 
		 authorizer.requireRole("RegisteredUser");
		 return filterDao.findFilters(versionid);
	 }
	 
	 //Function to post the list of filters against version
	 public void postFilterVersion(FilterVersionRequest fv) throws IOException {
	        authorizer.requireRole("RegisteredUser");

	        FilterStatusDTO filter = fv._body();
	        filter.id = UUID.randomUUID().toString();
	        hermesFilter.updateFilter(filter.filtername, filter.activate);
	        filterDao.addFilter(filter);
	    }
	
	 //Function to get the List of Hermes Instances as per given parameter.It will be removed if not required.
	 public HermesResultDTO getHermesInstance(RESTRequest rr) throws Exception {
	    	Map<String, String[]> params = rr._request().getParameterMap();
	    	
	        // make sure the collection is public
	        VersionDTO version = versionDao.findById(params.get("id")[0]);
	        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
	        if (databaseCollection.privateStatus) {
	            authorizer.requireRole("Admin");
	        }

	        HermesResultDTO dto = hermesResultDao.findByVersion(params.get("id")[0]);
	            
	        return dto;
	    }
	 
	 //Function to insert the Hermes Instance Entry for selected version
	 public String postHermesInstance(VersionRequest cr) throws Exception {
	        authorizer.requireRole("RegisteredUser");

	        VersionDTO version = cr._body();

	        // make sure the session user is the owner
	        String sessionUser = SecurityContext.getInstance().getUser();
	        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
	        String owner = databaseCollection.user;
	        if (!owner.equals(sessionUser)) {
	            authorizer.requireRole("Admin");
	        }
	        
	        try{
	        	HermesResultDTO oldInstance = getHermesInstance(version.id);
	            logger.info("Deleting outdated Instance for version {}", version.id);
	            deleteHermesInstance(oldInstance.id);
	        } catch(NullPointerException e){
	            logger.info("No outdated Hermes Instance found for version {}", version.id);
	        }

	        logger.info("Starting Instance version {}", version.id);
	        HermesProcess process = hermes.initialize(version);
	         hermes.start(process);
	        return process.getId();
	        
	 }
	 
	 //Function to delete the Hermes Instance entry for selected version.
	 public String deleteHermesInstance(String hermesResultId) throws Exception {
	        authorizer.requireRole("RegisteredUser");

	        // make sure the session user is the owner
	        HermesResultDTO hermesResultDto = hermesResultDao.findById(hermesResultId);
	        VersionDTO version = versionDao.findById(hermesResultDto.versionId);
	        String sessionUser = SecurityContext.getInstance().getUser();
	        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
	        String owner = databaseCollection.user;
	        if (!owner.equals(sessionUser)) {
	            authorizer.requireRole("Admin");
	        }

	        logger.info("Deleting Hermes result {}", hermesResultId);
	        //File hermesDir = new File(hermesResultDto.dir);
	        //FileUtil.deleteRecursively(buildDir);
	        hermesResultDao.delete(hermesResultId);

	        logger.info("Unfreezing version {}", version.id);
	        version.frozen = false;
	        versionDao.update(version);
	        return version.id;
	    }
	    
	 
	//Function to get the list of Filters  
	public HashMap<String,Boolean> getFilters()
	{
		authorizer.requireRole("RegisteredUser");
		
		  return  hermesFilter.getFilters();
	}
	
	
   interface QueryRequest extends RESTRequest {
	        QueriesDTO _body();
	    }
	
	public void postMaxLocation(QueryRequest qr) throws IOException
	{  
		authorizer.requireRole("RegisteredUser");
	    QueriesDTO dto = qr._body();
	    hermesFilter.updateMaxLocations(dto.maxlocations);
		
	}
	
	public void postFanIn(QueryRequest qr) throws IOException
	{ authorizer.requireRole("RegisteredUser");
	  QueriesDTO dto = qr._body();
<<<<<<< HEAD
	  hermesFilter.updateFIFO("fanin","categories",dto.fanin_categories);
	  hermesFilter.updateFIFO("fanin","categorySize", dto.fanin_categorySize);	
	}
	
	public void postFanOut(QueryRequest qr) throws IOException
	{ authorizer.requireRole("RegisteredUser");
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFIFO("fanout","categories",dto.fanout_categories);
	  hermesFilter.updateFIFO("fanout","categorySize", dto.fanout_categorySize);	
	}
	
	public void postFaRatio(QueryRequest qr) throws IOException
	{ authorizer.requireRole("RegisteredUser");
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFIFO("ratio","categories",dto.ratio_categories);
	  hermesFilter.updateFIFO("ratio","categorySize", dto.ratio_categorySize);	
=======
	  hermesFilter.updateFanInFanOut("fanin","categories",dto.fanin_categories);
	  hermesFilter.updateFanInFanOut("fanin","categorySize", dto.fanin_categorySize);	
	}
	
	public void postFanOut(QueryRequest qr) throws IOException
	{ authorizer.requireRole("RegisteredUser");
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFanInFanOut("fanout","categories",dto.fanout_categories);
	  hermesFilter.updateFanInFanOut("fanout","categorySize", dto.fanout_categorySize);	
	}
	
	public void postFaRatio(QueryRequest qr) throws IOException
	{ authorizer.requireRole("RegisteredUser");
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFanInFanOut("ratio","categories",dto.ratio_categories);
	  hermesFilter.updateFanInFanOut("ratio","categorySize", dto.ratio_categorySize);	
>>>>>>> branch 'master' of https://github.com/nguyenLisa/abm.git
	}
	
	//Function to get the MaxLocation
	public int getMaxLocations()
	{
		authorizer.requireRole("RegisteredUser");
		
		return hermesFilter.getMaxLocation();
	}
	
	//Function to get the FanInFanout 
<<<<<<< HEAD
	public Map<String,Integer> getFiFO()
=======
	public Map<String,Integer> getFanInFanOut()
>>>>>>> branch 'master' of https://github.com/nguyenLisa/abm.git
	{
		authorizer.requireRole("RegisteredUser");
		return hermesFilter.getFanInFanOut();
	}
	
	
	
	private float hermesProgress(HermesResultDTO dto){
    	if(dto.status.equals("WAITING")){return 0;};
    	float i = 0;
    	for(HermesBuildDTO hermesBuild : dto.hermesBuilds){
    		for(HermesStepDTO step: hermesBuild.hermesSteps){
    			if(step.status.equals("IN_PROGRESS")){return i/dto.hermesBuilds.size();}
    		}
    		i++;
    	}
    	return 1;
    }
	
}
