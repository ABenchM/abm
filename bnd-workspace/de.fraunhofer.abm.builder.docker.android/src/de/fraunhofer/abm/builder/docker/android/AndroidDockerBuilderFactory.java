package de.fraunhofer.abm.builder.docker.android;

import java.io.File;

import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

public class AndroidDockerBuilderFactory implements ProjectBuilderFactory {

	@Override
    public ProjectBuilder createProjectBuilder(RepositoryDTO repo, File repoDir) {
        ProjectBuilder builder = null;

        // first check, if repository properties contain build.system
        for (RepositoryPropertyDTO prop : repo.properties) {
            if(prop.name.equals("build.system")) {
                if(prop.value.equals("gradle")) {
                    builder = new AndroidDockerBuilder();
                }
            }
        }

        // properties didn't contain build.system
        // let's check, if there is a pom.xml
        //TODO check the files for gradle instead of pom.xml
        File pom = new File(repoDir, "pom.xml");
        if(pom.exists() && pom.isFile()) {
            builder = new AndroidDockerBuilder();
        }

        return builder;
    }
	
}
