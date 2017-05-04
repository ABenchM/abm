package de.fraunhofer.abm.builder.docker.maven;

import java.io.File;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

@Component
public class MavenDockerBuilderFactory implements ProjectBuilderFactory {

    @Override
    public ProjectBuilder createProjectBuilder(RepositoryDTO repo, File repoDir) {
        ProjectBuilder builder = null;

        // first check, if repository properties contain build.system
        for (RepositoryPropertyDTO prop : repo.properties) {
            if(prop.name.equals("build.system")) {
                if(prop.value.equals("maven")) {
                    builder = new MavenDockerBuilder();
                }
            }
        }

        // properties didn't contain build.system
        // let's check, if there is a pom.xml
        File pom = new File(repoDir, "pom.xml");
        if(pom.exists() && pom.isFile()) {
            builder = new MavenDockerBuilder();
        }

        return builder;
    }

}
