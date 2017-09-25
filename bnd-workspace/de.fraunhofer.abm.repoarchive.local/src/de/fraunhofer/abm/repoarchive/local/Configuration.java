package de.fraunhofer.abm.repoarchive.local;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="ABM Repo Archive", description = "Configuration for the LocalrepoArchive")
public @interface Configuration {

    @AttributeDefinition(name="Directory", description= "Directory to store checkout repos", required=true)
    String directory() default "/var/lib/abm/repo";
}
