package de.fraunhofer.abm.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.collection.dao.ProjectDao;
import de.fraunhofer.abm.domain.ProjectObjectDTO;
import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.delphi")
public class DelphiController implements REST {
	
	private static final transient Logger logger = LoggerFactory.getLogger(DelphiController.class);
    
	static Map<String, String> header = new HashMap<>();
    
    interface ProjectRequest extends RESTRequest {
    	ProjectObjectDTO _body();
    }
    @Reference
    private ProjectDao projectDao;

	private String featuresString;
	
	public String getDelphifeatures() throws Exception {
		String uri = "https://delphi.cs.uni-paderborn.de/api/features";   
		String resp = HttpUtils.get(uri,header,"UTF-8");   
		return resp;
	}
	
	public String postSearchquery(String features) throws Exception {
	    String body = features;
		header = new HashMap<>(); 
	    header.put("Content-type", "application/json"); 
    	String uri = "https://delphi.cs.uni-paderborn.de/api/search";   
		HttpResponse resp = HttpUtils.post(uri, header, body.getBytes("UTF-8"), "UTF-8");   
		JSONObject json = new JSONObject(resp); 
		JSONObject response = new JSONObject(resp);
		String responseArray = response.getString("content");
		return responseArray;
	}
	
	public void postAddprojects(ProjectRequest rr) {
		ProjectObjectDTO projectdto = rr._body();
        ProjectObjectDTO project = new ProjectObjectDTO();
		project.id = UUID.randomUUID().toString();
        project.version_id = projectdto.version_id;
        project.project_id = projectdto.project_id;
        project.source = projectdto.source;
        projectDao.save(project);
	}
	
	public void deleteRemoveproject(String projectId) {
		List<ProjectObjectDTO> result;
		result = projectDao.findproject(projectId);
		for(ProjectObjectDTO project:result)
		{		
			projectDao.delete(project);
		}
	}

}
