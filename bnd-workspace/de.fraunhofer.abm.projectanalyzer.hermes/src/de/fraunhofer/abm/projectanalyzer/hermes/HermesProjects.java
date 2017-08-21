package de.fraunhofer.abm.projectanalyzer.hermes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.fraunhofer.abm.domain.ProjectDTO;

public class HermesProjects {

	private static final transient Logger logger = LoggerFactory.getLogger(HermesProjects.class);
	
	
	File ProjectPath = new File("C:\\Users\\ankur\\workspace\\Hermes\\hermes.json");
	ObjectMapper mapper = new ObjectMapper();
	ProjectDTO project = new ProjectDTO();
	List<ProjectDTO> pc = new ArrayList<ProjectDTO>();

	List<ProjectDTO> getProjects() throws JsonParseException, JsonMappingException, IOException
	{
		JsonNode node =  mapper.readValue(ProjectPath, JsonNode.class);
		JsonNode orgNode = node.get("org");
		JsonNode opaljNode = orgNode.get("opalj");
		JsonNode hermesNode = opaljNode.get("hermes");
		JsonNode projectsNode = hermesNode.get("projects");
	    
			
		for(int i=0;i<projectsNode.size();i++)
		{   
			project.id = projectsNode.get(i).get("id").textValue();
			project.cp = projectsNode.get(i).get("cp").textValue();
			if(projectsNode.get(i).has("libcp_defaults"))
			{project.libcp_defaults = projectsNode.get(i).get("libcp_defaults").textValue();
			}
			pc.add(project);
			
		}
		
		return pc;
	}
	
	public void addProjects(String id,String cp,Optional<String> libcp_defaults)
	{
		
		
	}
	
	
}
