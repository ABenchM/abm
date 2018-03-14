package de.fraunhofer.abm.builder.docker.gradle;

import java.io.File;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

@Component
public class GradleDockerBuilderFactory implements ProjectBuilderFactory {

	@Override
    public ProjectBuilder createProjectBuilder(RepositoryDTO repo, File repoDir) {
        ProjectBuilder builder = null;
       
        // first check, if repository properties contain build.system
        for (RepositoryPropertyDTO prop : repo.properties) {
            if(prop.name.equals("build.system")) {
                if(prop.value.equals("gradle")) {
                    builder = new GradleDockerBuilder();
                }
            }
        }

        // properties didn't contain build.system
        // let's check, if there is a pom.x	ml
        //TODO check the files for gradle instead of pom.xml
   
        File pom = new File(repoDir, "build.gradle");
        if(pom.exists() && pom.isFile()) {
        	
            builder = new GradleDockerBuilder();
        }
       
        return builder;
    }
	
	
}
