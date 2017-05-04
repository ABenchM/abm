package de.fraunhofer.abm.suitebuilder.impl;

import java.io.File;
import java.util.List;

import de.fraunhofer.abm.builder.api.AbstractBuildStep;
import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class CreateBuilderStep extends AbstractBuildStep<ProjectBuilder> {

    private File repoDir;
    private List<ProjectBuilderFactory> builderFactories;

    public CreateBuilderStep(RepositoryDTO repository, File repoDir, List<ProjectBuilderFactory> builderFactories) {
        super(repository);
        this.repoDir = repoDir;
        this.builderFactories = builderFactories;
        name = "Create project builder";
    }

    @Override
    public ProjectBuilder execute() {
        setStatus(STATUS.IN_PROGRESS);
        ProjectBuilder builder = null;

        for (ProjectBuilderFactory factory : builderFactories) {
            builder = factory.createProjectBuilder(repository, repoDir);
            if(builder != null) {
                output = "Using " + builder.getClass().getSimpleName() + " to build the project";
                setStatus(STATUS.SUCCESS);
                break;
            }
        }

        if(builder == null) {
            output = "No suitable builder found";
            builder = new NoopBuilder();
        }

        return builder;
    }

}
