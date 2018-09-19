package de.fraunhofer.abm.app.controllers;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.RepositoryDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.FilterResultsDTO;
import de.fraunhofer.abm.domain.FilterStatusDTO;
import de.fraunhofer.abm.domain.HermesBuildDTO;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.HermesStepDTO;
import de.fraunhofer.abm.domain.ProjectBuildDTO;
import de.fraunhofer.abm.domain.QueriesDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
//import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.hermes.FilterResults;
import de.fraunhofer.abm.hermes.Hermes;
import de.fraunhofer.abm.hermes.HermesFilter;
import de.fraunhofer.abm.hermes.HermesProcess;
import de.fraunhofer.abm.hermes.HermesProjects;
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
	 private RepositoryDao repositoryDao;
	 
	 @Reference
	 private BuildResultDao buildResultDao;
	 
	 @Reference
	 private VersionDao versionDao;
	 
	 @Reference
	 private FilterResults filterResults;
	 
		 
	 
	 interface VersionRequest extends RESTRequest {
	        VersionDTO _body();
	    }
	 
	 //Function to get the Hermes Instance details for the version.
	 public HermesResultDTO getInstance(String versionId) throws Exception{
		 
		 ArrayList<String> users = new ArrayList<String>();
		 users.add("RegisteredUser");
		 users.add("UserAdmin"); 
		 authorizer.requireRoles(users);
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
	 
	  public HermesResultDTO getInstance(RESTRequest rr) throws Exception {
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
	 
	 
	 public File getCsv(String versionId) {
		 
		 BuildResultDTO buildResultDTO = buildResultDao.findByVersion(versionId) ;
		 
		 return new File(buildResultDTO.dir+"/hermesResults.csv");
				 
	 }			
	 
		 
	 //Function to get the list of Hermes instances running for the user.
	 public List<Map<String, String>> getInstances(String user)
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
	 
	 public List<String> getResultHeader(String versionid) throws IOException {
			
		 ArrayList<String> users = new ArrayList<String>();
		 users.add("RegisteredUser");
		 users.add("UserAdmin"); 
		 authorizer.requireRoles(users);
		 List<String> headers = new ArrayList<String>();
				   
	        
	        headers = filterResults.getHeaders(versionid);
	       
	        
		 return headers;
		 
	 }
	 
public List<FilterResultsDTO> getHermesResults(String versionid) throws IOException{
		 
	ArrayList<String> users = new ArrayList<String>();
	users.add("RegisteredUser");
	users.add("UserAdmin"); 
    authorizer.requireRoles(users);
		  List<FilterResultsDTO> dto = new ArrayList<FilterResultsDTO>();
		    HermesResultDTO hermesDto ; 
		    String repoDir;		  
	        hermesDto = hermesResultDao.findByVersion(versionid);
	        repoDir =  hermesDto.dir;
		    		    
		 // Map<String,Map<String,Integer>> results = new HashMap<String,Map<String,Integer>>();
		 Map<String,Map<String,Integer>> results = new HashMap<String,Map<String,Integer>>();
		 
		  results = filterResults.getFilterResults(repoDir,versionid,filterDao.findFilters(versionid));
		   for(Map.Entry<String,Map<String,Integer>> entry:results.entrySet()) {
			   
			   FilterResultsDTO fr = new FilterResultsDTO();
			   fr.projectName = entry.getKey();
			   fr.queries = entry.getValue();
			   dto.add(fr);
			   		   
		   }
		 			 
		 return dto;
	 
	 }
	 
	 
	 
	 
	 interface FilterVersionRequest extends RESTRequest {
	        List<FilterStatusDTO> _body();
	    }
	 
	 //Function to get the list of active Filters for the selected version.
	 public List<FilterStatusDTO> getActiveFilters(String versionid)
	 { 
		 ArrayList<String> users = new ArrayList<String>();
		 users.add("RegisteredUser");
		 users.add("UserAdmin"); 
		 authorizer.requireRoles(users);
		 List<FilterStatusDTO> dto = new ArrayList<FilterStatusDTO>();
		 if(filterDao.findFilters(versionid)!=null)
		// List<FilterStatusDTO> dto = filterDao.findFilters(versionid);
		 if(dto.size() == 0)
		 {
			 HashMap<String,Boolean> activeFilters = new HashMap<String,Boolean>();
			 activeFilters = hermesFilter.getFilters();
			 for(Map.Entry<String, Boolean> entry : activeFilters.entrySet())
			 {
				 FilterStatusDTO fs = new FilterStatusDTO();
				 fs.filtername = entry.getKey();
				 fs.activate = entry.getValue();
				 fs.versionid = versionid;
				 dto.add(fs);
				 
			 }
		 }
		 return dto;
		// else
		/* {
			 HashMap<String,Boolean> activeFilters = new HashMap<String,Boolean>();
			 activeFilters = hermesFilter.getFilters();
			 for(Map.Entry<String, Boolean> entry : activeFilters.entrySet())
			 {
				 FilterStatusDTO fs = new FilterStatusDTO();
				 fs.filtername = entry.getKey();
				 fs.activate = entry.getValue();
				 dto.add(fs);
				 
			 }
			 return dto;
		 }*/
	 }
	 
	 
	 
	 //Function to post the list of filters against version
	 public String postHermes(FilterVersionRequest fv,String versionid) throws Exception {
		 ArrayList<String> users = new ArrayList<String>();
		 users.add("RegisteredUser");
		 users.add("UserAdmin"); 
		 authorizer.requireRoles(users);
	        
	        
	          List<FilterStatusDTO> filters = fv._body();
	          FilterStatusDTO filter = new FilterStatusDTO();
	        VersionDTO version = versionDao.findById(versionid);
	        Iterator<FilterStatusDTO> iterator = filters.iterator();
	        while(iterator.hasNext()){
	        	filter = iterator.next();
	        	filter.id = UUID.randomUUID().toString();
	        	filter.versionid = versionid;
		        hermesFilter.updateFilter(filter.filtername, filter.activate);
		        filterDao.addFilter(filter);
	        }	        
	        
	        String sessionUser = SecurityContext.getInstance().getUser();
	        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
	        String owner = databaseCollection.user;
	        if (!owner.equals(sessionUser)) {
	            authorizer.requireRole("Admin");
	        }
	        
	        
	        try{
	        	HermesResultDTO oldInstance = getInstance(filter.versionid);
	            logger.info("Deleting outdated Instance for version {}", filter.versionid);
	            deleteInstance(oldInstance.id);
	        } catch(NullPointerException e){
	            logger.info("No outdated Hermes Instance found for version {}", filter.versionid);
	        }
	        
	        logger.info("Starting Instance version {}", filter.versionid);
	        
	        String   repoDir;
	        List<RepositoryDTO> repo;
	        BuildResultDTO resultDTO;
	        repo = repositoryDao.findByVersion(versionid);
	        
	        resultDTO = buildResultDao.findByVersion(versionid);
	        repoDir = resultDTO.dir;
	        
	        
	        
	        HermesProcess process = hermes.initialize(version,repoDir,repo);
	        hermes.start(process);
	        
	        
	        
	        return process.getId();
	         
	        
	    }
	
	 
	 
	 //Function to delete the Hermes Instance entry for selected version.
	 public String deleteInstance(String hermesResultId) throws Exception {
		 	ArrayList<String> users = new ArrayList<String>();
	    	users.add("RegisteredUser");
	    	users.add("UserAdmin"); 
	        authorizer.requireRoles(users);

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
	        File hermesDir = new File(hermesResultDto.dir,"hermesResults.csv");
	        FileUtil.deleteRecursively(hermesDir);
	        hermesResultDao.delete(hermesResultId);
            
	        //logger.info("Unfreezing version {}", version.id);
	        //version.frozen = false;
	        
	        version.filtered = false;
	        filterDao.dropFilters(hermesResultDto.versionId);
	        versionDao.update(version);
	        return version.id;
	    }
	    
	 
	//Function to get the list of Filters  
	public HashMap<String,Boolean> getFilters()
	{
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
		
		  return  hermesFilter.getFilters();
	}
	
	
   interface QueryRequest extends RESTRequest {
	        QueriesDTO _body();
	    }
	
	public void postMaxLocation(QueryRequest qr) throws IOException
	{  
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
	    QueriesDTO dto = qr._body();
	    hermesFilter.updateMaxLocations(dto.maxlocations);
		
	}
	
	public void postFanIn(QueryRequest qr) throws IOException
	{ 
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFanInFanOut("fanin","categories",dto.fanin_categories);
	  hermesFilter.updateFanInFanOut("fanin","categorySize", dto.fanin_categorySize);	
	}
	
	public void postFanOut(QueryRequest qr) throws IOException
	{ 
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFanInFanOut("fanout","categories",dto.fanout_categories);
	  hermesFilter.updateFanInFanOut("fanout","categorySize", dto.fanout_categorySize);	
	}
	

	public void postRatio(QueryRequest qr) throws IOException
	{ 
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
	  QueriesDTO dto = qr._body();
	  hermesFilter.updateFanInFanOut("ratio","categories",dto.ratio_categories);
	  hermesFilter.updateFanInFanOut("ratio","categorySize", dto.ratio_categorySize);
	}
	
	//Function to get the MaxLocation
	public int getMaxLocations()
	{
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
		
		return hermesFilter.getMaxLocation();
	}
	
	//Function to get the FanInFanout 
	public Map<String,Integer> getFanInFanOut()
	{
		ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
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
