package de.fraunhofer.abm.suitebuilder.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.fraunhofer.abm.builder.api.AbstractBuildStep;
import de.fraunhofer.abm.builder.api.AbstractProjectBuilder;
import de.fraunhofer.abm.builder.api.BuildStep;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.suitebuilder.NoBuilderFoundException;

public class NoopBuilder extends AbstractProjectBuilder {

    @Override
    public void init(RepositoryDTO repo, File repoDir) {
        BuildStep<Boolean> step = new InitializationStep(repo);
        buildSteps.add(0, step);
        step.addBuildStepListener(this);
        fireBuildInitialized(repo, buildSteps);
    }

    @Override
    public List<File> build(RepositoryDTO repo, File repoDir) throws Exception {
        buildSteps.get(0).execute();
        buildSteps.remove(1);
        return Collections.emptyList();
    }

    private class InitializationStep extends AbstractBuildStep<Boolean> {
        public InitializationStep(RepositoryDTO repository) {
            super(repository);
            name = "Initialize building process";
        }

        @Override
        public Boolean execute() {
            setStatus(STATUS.FAILED);
            output = "No builder for repo " + repository.id + " found";
            setThrowable(new NoBuilderFoundException("No builder for repo " + repository.id + " found"));
            return false;
        }
    }
}
