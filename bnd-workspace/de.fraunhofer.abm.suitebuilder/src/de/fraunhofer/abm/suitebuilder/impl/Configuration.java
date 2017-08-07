package de.fraunhofer.abm.suitebuilder.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="ABM Suite Builder", description = "Configuration for the SuiteBuilder")
public @interface Configuration {

    @AttributeDefinition(name="Thread Pool Core Size", description = "The minimum number of threads allocated to this pool", required=true)
    int coreSize() default 1;

    @AttributeDefinition(name="Thread Pool Max Size", description = "Maximum number of threads allocated to this pool", required=true)
    int maximumPoolSize() default 1;

    @AttributeDefinition(name="Thread Pool Keep Alive", description = "Nr of seconds an idle free thread should survive before being destroyed", required=true)
    long keepAliveTime() default 300;

    @AttributeDefinition(name="Workspace Root", description= "Workspace root directory", required=true)
    String workspaceRoot() default "C:\\Ankur\\shk\\suitebuilder";
}
