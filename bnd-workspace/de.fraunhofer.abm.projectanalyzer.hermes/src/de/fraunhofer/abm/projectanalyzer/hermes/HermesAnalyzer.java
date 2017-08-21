package de.fraunhofer.abm.projectanalyzer.hermes;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.domain.FilterDTO;
import de.fraunhofer.abm.domain.ProjectConfDTO;
import de.fraunhofer.abm.domain.ProjectDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class HermesAnalyzer implements ProjectAnalyzer {

	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesAnalyzer.class);
	
	
	
	
	
	
	private File FilterPath;
	private File ProjectPath;
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("unused")
	private void updateProjectconf(String id , String cp , Optional<String> libcp_defaults) throws JsonParseException, JsonMappingException, IOException
	{
		index=0;
		ObjectMapper mapper = new ObjectMapper();
		ProjectConfDTO pr = mapper.readValue(ProjectPath, ProjectConfDTO.class);
		
		ProjectDTO p = new ProjectDTO();
		p.id= id;
		p.cp = cp;
		if(libcp_defaults.isPresent())
		{
			p.libcp_defaults = libcp_defaults;	
		}
		
		pr.projects.add(p);
		
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.writeValue(ProjectPath, pr);
		
		
        		
	}
	
	
	
	
	

	
	
	
  
    @Activate
    public void activate(Configuration config) {
        initFilterPath(config.filterpath());
        initProjectPath(config.projectpath());
    }
    
    @Deactivate
    public void deactivate() {
    }
    
    private void initFilterPath(String path) {
        FilterPath = new File(path);
        if(!FilterPath.exists()) {
            FilterPath.mkdirs();
        }
        logger.info("Using repo archive directory {}", FilterPath.getAbsolutePath());
    }
    
    private void initProjectPath(String path) {
        ProjectPath = new File(path);
        if(!ProjectPath.exists()) {
            ProjectPath.mkdirs();
        }
        logger.info("Using repo archive directory {}", ProjectPath.getAbsolutePath());
    }


	@Override
	public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
		// TODO Auto-generated method stub
		return null;
	}
	
 
}
