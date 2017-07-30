package de.fraunhofer.abm.projectanalyzer.hermes;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.domain.ConfFilterDTO;
import de.fraunhofer.abm.domain.FilterDTO;
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
	private static Map<String, String> Qnamemap = new HashMap<>();
	
	int index;
	ConfFilterDTO f = new ConfFilterDTO();
	
	private File FilterPath;
	
	
	static{
		Qnamemap.put("org.opalj.hermes.queries.Metrics","Metrics");
		Qnamemap.put("org.opalj.hermes.queries.FieldAccessStatistics","FieldAccessStatistics");
		Qnamemap.put("org.opalj.hermes.queries.TrivialReflectionUsage","TrivialReflectionUsage");
		Qnamemap.put("org.opalj.hermes.queries.BytecodeInstructions","BytecodeInstructions");
		Qnamemap.put("org.opalj.hermes.queries.RecursiveDataStructures","RecursiveDataStructures");
		Qnamemap.put("org.opalj.hermes.queries.MethodsWithoutReturns","MethodsWithoutReturns");
		Qnamemap.put("org.opalj.hermes.queries.DebugInformation","DebugInformation");
		Qnamemap.put("org.opalj.hermes.queries.FanInFanOut","FanInFanOut");
		Qnamemap.put("org.opalj.hermes.queries.GUIAPIUsage","GUIAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.ClassLoaderAPIUsage","ClassLoaderAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.JavaCryptoArchitectureUsage","JavaCryptoArchitectureUsage");
		Qnamemap.put("org.opalj.hermes.queries.MethodTypes","MethodTypes");
		Qnamemap.put("org.opalj.hermes.queries.ReflectionAPIUsage","ReflectionAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.SystemAPIUsage","SystemAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.ThreadAPIUsage","ThreadAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.UnsafeAPIUsage","UnsafeAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.JDBCAPIUsage","JDBCAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.BytecodeInstrumentationAPIUsage","BytecodeInstrumentationAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.SizeOfInheritanceTree","SizeOfInheritanceTree");
		Qnamemap.put("org.opalj.hermes.queries.ClassFileVersion","ClassFileVersion");
	}
	
	
	@SuppressWarnings("unused")
	private void UpdateFilter(String query,boolean value) throws IOException
	{  index = 0;
       ObjectMapper mapper = new ObjectMapper();
       ConfFilterDTO f = mapper.readValue(FilterPath, ConfFilterDTO.class);
       
       	
       for(FilterDTO q:f.registered)
       {
    	   if(q.query==query)
    	   { q.activate = value;
    	       index++;
    	       f.registered.set(index, q);       
    	   }
    	   
       }
       
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.writeValue(FilterPath, f);
       
		
	}
	
	
	@SuppressWarnings("unused")
	private void createFilter(String query, boolean value) throws JsonGenerationException, JsonMappingException, IOException,ParseException
	{

		
	    
	}

    @SuppressWarnings("unused")
	private void setFIFO(String name,int value) throws JsonParseException, JsonMappingException, IOException
    {
    	ObjectMapper mapper = new ObjectMapper();
        ConfFilterDTO f = mapper.readValue(FilterPath, ConfFilterDTO.class);
         	 
        if(f.FanInFanOut.containsKey(name))
        {
        	f.FanInFanOut.put(name, value);
        }
        	
        
        
         mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
 		mapper.setSerializationInclusion(Include.NON_EMPTY);
 		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
 		mapper.writeValue(FilterPath, f);
    	
    	
    	
    }
    
    @Activate
    public void activate(Configuration config) {
        initDirectory(config.filterpath());
    }
    
    @Deactivate
    public void deactivate() {
    }
    
    private void initDirectory(String path) {
        FilterPath = new File(path);
        if(!FilterPath.exists()) {
            FilterPath.mkdirs();
        }
        logger.info("Using repo archive directory {}", FilterPath.getAbsolutePath());
    }

	@Override
	public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
		// TODO Auto-generated method stub
		return null;
	}
	
 
}
