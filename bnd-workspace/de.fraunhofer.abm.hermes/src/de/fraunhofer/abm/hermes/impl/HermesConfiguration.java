package de.fraunhofer.abm.hermes.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="ABM Hermes", description = "Configuration for the Hermes")
public @interface HermesConfiguration {

    @AttributeDefinition(name="Hermes config", description= "Hermes Config Directory", required=true)
    String hermesConfigDir() default "/opt/abm/";
}
