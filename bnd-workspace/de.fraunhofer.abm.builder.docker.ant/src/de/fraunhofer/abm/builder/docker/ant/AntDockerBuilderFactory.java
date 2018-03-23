package de.fraunhofer.abm.builder.docker.ant;

import java.io.File;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

@Component
public class AntDockerBuilderFactory implements ProjectBuilderFactory {

	@Override
    public ProjectBuilder createProjectBuilder(RepositoryDTO repo, File repoDir) {
        ProjectBuilder builder = null;

        // first check, if repository properties contain build.system
        for (RepositoryPropertyDTO prop : repo.properties) {
            if(prop.name.equals("build.system")) {
                if(prop.value.equals("ant")) {
                    builder = new AntDockerBuilder();
                }
            }
        }

        // properties didn't contain build.system
        
        File build = new File(repoDir, "build.xml");
        File pom = new File(repoDir,"pom.xml");
        if(build.exists() && build.isFile() && !pom.exists()) {
            builder = new AntDockerBuilder();
        }

        return builder;
    }

}
