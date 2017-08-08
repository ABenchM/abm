package de.fraunhofer.abm.projectanalyzer.hermes;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="ABM Filter Configuration", description = "Configuration for the Path of Hermes filter configuration file")
public @interface Configuration {

	@AttributeDefinition(name="FilterPath", description= "Directory to store Hermes Filter configuration file", required=true)
    String filterpath() default "C:\\Ankur\\shk\\Hermes\\ApplicationConf.json";	
	
	@AttributeDefinition(name="ProjectPath", description= "Directory to store Hermes Projects configuration file", required=true)
    String projectpath() default "C:\\Ankur\\shk\\Hermes\\Hermes.json";	
}
