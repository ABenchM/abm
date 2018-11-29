package de.fraunhofer.abm.app.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.RepositoryDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.ProjectBuildDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.suitebuilder.BuildProcess;
import de.fraunhofer.abm.suitebuilder.SuiteBuilder;
import de.fraunhofer.abm.util.FileUtil;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.builder")
public class BuildController implements REST {

    private static final transient Logger logger = LoggerFactory.getLogger(BuildController.class);

    @Reference
    private Authorizer authorizer;

    @Reference
    private SuiteBuilder builder;

    @Reference
    private CollectionDao collectionDao;

    @Reference
    private VersionDao versionDao;

    @Reference
    private BuildResultDao buildResultDao;

    @Reference
    private RepositoryDao repositoryDao;

    interface VersionRequest extends RESTRequest {
        VersionDTO _body();
    }

    public BuildResultDTO getBuild(String versionId) throws Exception {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);


        // make sure the session user is the owner
        String sessionUser = SecurityContext.getInstance().getUser();
        VersionDTO version = versionDao.findById(versionId);
        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
        String owner = databaseCollection.user;
        if (!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }

        BuildResultDTO dto = buildResultDao.findByVersion(versionId);
        for (ProjectBuildDTO projectBuild : dto.projectBuilds) {
            projectBuild.repository = repositoryDao.findById(projectBuild.repositoryId);
        }
        dto.projectBuilds.sort(new Comparator<ProjectBuildDTO>() {
            @Override
            public int compare(ProjectBuildDTO o1, ProjectBuildDTO o2) {
                return o1.repository.name.compareTo(o2.repository.name);
            }
        });
        return dto;
    }
    
    public boolean getFe(RESTRequest rr) {
    	
    	  Map<String, String[]> params = rr._request().getParameterMap();
    	  boolean isFileExists = false;
    	  VersionDTO version = versionDao.findById(params.get("id")[0]);
          VersionDTO databaseVersion = versionDao.findById(version.id);
          if (databaseVersion.privateStatus) {
              authorizer.requireRole("Admin");
          }
          
          BuildResultDTO dto = buildResultDao.findByVersion(params.get("id")[0]);
          if(dto!=null) {
        	 
        	  if(params.get("type")[0].contentEquals("build")) {
            	  
              	
        		  isFileExists = (new File(dto.dir.toString()+"/archive.zip").exists());
        	  
        	  
          }else if(params.get("type")[0].contentEquals("hermes")) {
        	  
        	
        		  isFileExists = (new File(dto.dir.toString()+"/hermesResults.csv").exists());
        	  
          }
          }
          
    
          return isFileExists;
    }
    
    public List<Map<String, String>> getBuilds(String user){
    	authorizer.requireUser(user);
    	
    	List<BuildResultDTO> builds = buildResultDao.findRunning(user);
    	List<Map<String, String>> results = new ArrayList<>();
    	
    	for(BuildResultDTO dto: builds){
    		Map<String, String> map = new HashMap<String, String>();
            VersionDTO version = versionDao.findById(dto.versionId);
            CollectionDTO collection = collectionDao.findById(version.collectionId);
            
            for (ProjectBuildDTO projectBuild : dto.projectBuilds) {
                projectBuild.repository = repositoryDao.findById(projectBuild.repositoryId);
            }
            dto.projectBuilds.sort(new Comparator<ProjectBuildDTO>() {
                @Override
                public int compare(ProjectBuildDTO o1, ProjectBuildDTO o2) {
                    return o1.repository.name.compareTo(o2.repository.name);
                }
            });
            
            map.put("id", dto.versionId);
            map.put("name", collection.name);
            map.put("versionNum", String.valueOf(version.number));
            map.put("progress", String.valueOf(buildProgress(dto)));
            map.put("buildStatus", dto.status);
            map.put("hidden", "false");
            results.add(map);
    	}
    	
    	return results;
    }

    public BuildResultDTO getBuild(RESTRequest rr) throws Exception {
    	Map<String, String[]> params = rr._request().getParameterMap();
    	
        // make sure the collection is public
        VersionDTO version = versionDao.findById(params.get("id")[0]);
        VersionDTO databaseVersion = versionDao.findById(version.id);
        if (databaseVersion.privateStatus) {
            authorizer.requireRole("Admin");
        }

        BuildResultDTO dto = buildResultDao.findByVersion(params.get("id")[0]);
        for (ProjectBuildDTO projectBuild : dto.projectBuilds) {
            projectBuild.repository = repositoryDao.findById(projectBuild.repositoryId);
        }
        dto.projectBuilds.sort(new Comparator<ProjectBuildDTO>() {
            @Override
            public int compare(ProjectBuildDTO o1, ProjectBuildDTO o2) {
                return o1.repository.name.compareTo(o2.repository.name);
            }
        });
        return dto;
    }

    public String postBuild(VersionRequest cr) throws Exception {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);


        VersionDTO version = cr._body();

        // make sure the session user is the owner
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
        String owner = databaseCollection.user;
        if (!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }
        
        try{
        	BuildResultDTO oldBuild = getBuild(version.id);
            logger.info("Deleting outdated build for version {}", version.id);
            deleteBuild(oldBuild.id);
        } catch(NullPointerException e){
            logger.info("No outdated build found for version {}", version.id);
        }

        logger.info("Start building version {}", version.id);
        BuildProcess build = builder.initialize(version);
        builder.start(build);
        return build.getId();
    }

    public String deleteBuild(String buildResultId) throws Exception {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);


        // make sure the session user is the owner
        BuildResultDTO buildResultDto = buildResultDao.findById(buildResultId);
        VersionDTO version = versionDao.findById(buildResultDto.versionId);
        String sessionUser = SecurityContext.getInstance().getUser();
        CollectionDTO databaseCollection = collectionDao.findById(version.collectionId);
        String owner = databaseCollection.user;
        if (!owner.equals(sessionUser)) {
            authorizer.requireRole("Admin");
        }

        logger.info("Deleting build result {}", buildResultId);
        File buildDir = new File(buildResultDto.dir);
        FileUtil.deleteRecursively(buildDir);
        buildResultDao.delete(buildResultId);

        logger.info("Unfreezing version {}", version.id);
        version.frozen = false;
        versionDao.update(version);
        return databaseCollection.id;
    }
    
    private float buildProgress(BuildResultDTO dto){
    	if(dto.status.equals("WAITING")){return 0;};
    	float i = 0;
    	for(ProjectBuildDTO projectBuild : dto.projectBuilds){
    		for(BuildStepDTO step: projectBuild.buildSteps){
    			if(step.status.equals("IN_PROGRESS")){return i/dto.projectBuilds.size();}
    		}
    		i++;
    	}
    	return 1;
    }
}
