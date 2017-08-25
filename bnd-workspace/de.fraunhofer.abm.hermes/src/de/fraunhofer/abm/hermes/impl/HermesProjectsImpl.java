package de.fraunhofer.abm.hermes.impl;



import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import de.fraunhofer.abm.hermes.HermesProjects;


@Component
public class HermesProjectsImpl implements HermesProjects {

	File file = new File("C:\\Users\\ankur\\workspace\\Hermes\\hermes.json");
	
	@Override
	public void addProjects(String id,String cp,String libcp_defaults) throws IOException
	{
	     JsonFactory jf = new JsonFactory();
		 JsonGenerator jg = jf.createGenerator(file,JsonEncoding.UTF8);
		
		
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
	public void addProjects(String id,String cp) throws IOException
	{
		JsonFactory jf = new JsonFactory();
		 JsonGenerator jg = jf.createGenerator(file,JsonEncoding.UTF8);
		
		
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
				jg.writeEndObject();
				jg.writeEndArray();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.writeEndObject();
				jg.close();
	}
	
}
