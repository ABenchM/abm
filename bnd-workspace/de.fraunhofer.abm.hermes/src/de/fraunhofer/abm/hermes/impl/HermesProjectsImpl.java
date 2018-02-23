package de.fraunhofer.abm.hermes.impl;



import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import de.fraunhofer.abm.hermes.HermesProjects;

@Component(name = "de.fraunhofer.abm.hermes.HermesProjects")
public class HermesProjectsImpl implements HermesProjects  {

	private static final transient Logger logger = LoggerFactory.getLogger(HermesProjectsImpl.class);
	private File projectPath ;
	
	public HermesProjectsImpl(File projectPath) {
		
		this.projectPath = projectPath;
		this.projectPath = new File(projectPath.toString(),"hermes.json");
	}
	
	
	
	@Override
	public void addProjects(String id,String cp,String libcp_defaults) throws IOException
	{
	     JsonFactory jf = new JsonFactory();
		 JsonGenerator jg = jf.createGenerator(projectPath,JsonEncoding.UTF8);
		
		
				jg.writeStartObject();
				jg.writeFieldName("org");
				jg.writeStartObject();
				jg.writeFieldName("opalj");
				jg.writeStartObject();
				jg.writeFieldName("hermes");
				jg.writeStartObject();
				jg.writeFieldName("projects");
				jg.writeStartArray();
				jg.writeStartObject();
				jg.writeFieldName("id");
				jg.writeString(id);
				jg.writeFieldName("cp");
				jg.writeString(cp);
				jg.writeFieldName("libcp_defaults");
				jg.writeString(libcp_defaults);
				jg.writeEndObject();
				jg.writeEndArray();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.close();
	
	}
		
	
	@Override
	public void addProjects(HashMap<String,String> projects) throws IOException
	{
		JsonFactory jf = new JsonFactory();
		logger.info("Project Path is {}", projectPath);
		 JsonGenerator jg = jf.createGenerator(projectPath,JsonEncoding.UTF8);
		
		
				jg.writeStartObject();
				jg.writeFieldName("org");
				jg.writeStartObject();
				jg.writeFieldName("opalj");
				jg.writeStartObject();
				jg.writeFieldName("hermes");
				jg.writeStartObject();
				jg.writeFieldName("projects");
				jg.writeStartArray();
				for(Map.Entry<String, String> entry : projects.entrySet()) {
					
					jg.writeStartObject();
					jg.writeFieldName("id");
					jg.writeString(entry.getKey());
					jg.writeFieldName("cp");
					jg.writeString(entry.getValue());
					jg.writeEndObject();
				}
				
				jg.writeEndArray();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.close();
	}
	
}
