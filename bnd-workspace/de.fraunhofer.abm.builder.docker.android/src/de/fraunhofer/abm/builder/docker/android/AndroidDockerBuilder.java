package de.fraunhofer.abm.builder.docker.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.fraunhofer.abm.builder.api.AbstractProjectBuilder;
import de.fraunhofer.abm.builder.api.BuildStep;
import de.fraunhofer.abm.builder.api.BuildStep.STATUS;
import de.fraunhofer.abm.builder.docker.android.steps.ExtractBuildResults;
import de.fraunhofer.abm.builder.docker.android.steps.RunDockerBuild;
import de.fraunhofer.abm.builder.docker.base.CreateDockerFile;
import de.fraunhofer.abm.builder.docker.base.CreateDockerImage;
import de.fraunhofer.abm.builder.docker.base.DeleteDockerContainer;
import de.fraunhofer.abm.builder.docker.base.DeleteDockerImage;

import de.fraunhofer.abm.domain.RepositoryDTO;

public class AndroidDockerBuilder extends AbstractProjectBuilder {

	private static final String NOT_SET = "NOT_SET";
    private ExecutorService executor;

    private BuildStep<Void> createDockerFile;
    private BuildStep<String> createDockerImage;
    //private BuildStep<String> createDockerContainer;
    private RunDockerBuild runDockerBuild;
    private ExtractBuildResults extractBuildResults;
    private DeleteDockerContainer deleteDockerContainer;
    private DeleteDockerImage deleteDockerImage;

    private enum STATE {
        CONTINUE,
        CLEAN_UP
    }
    private STATE state = STATE.CONTINUE;

    @Override
    public void init(RepositoryDTO repo, File repoDir) {
        executor = Executors.newCachedThreadPool();
        Bundle sourceBundle = FrameworkUtil.getBundle(AndroidDockerBuilder.class);
        createDockerFile = addBuildStep(new CreateDockerFile(repo, new File(repoDir, "/Dockerfile"), sourceBundle));
        createDockerImage = addBuildStep(new CreateDockerImage(repo, executor, repoDir));
        runDockerBuild = (RunDockerBuild) addBuildStep(new RunDockerBuild(repo, executor, repoDir));
        extractBuildResults = (ExtractBuildResults) addBuildStep(new ExtractBuildResults(repo, executor, repoDir));
        deleteDockerContainer = (DeleteDockerContainer) addBuildStep(new DeleteDockerContainer(repo, executor, repoDir));
        deleteDockerImage = (DeleteDockerImage) addBuildStep(new DeleteDockerImage(repo, executor, repoDir));
        fireBuildInitialized(repo, buildSteps);
    }

    @Override
    public List<File> build(RepositoryDTO repo, File repoDir) throws Exception {
        List<File> buildArtifacts = new ArrayList<>();

        String imageName = NOT_SET;
        String containerName = NOT_SET;
        try {
            if(state == STATE.CONTINUE) {
                createDockerFile.execute();
                if(createDockerFile.getStatus() != STATUS.SUCCESS) {
                    state = STATE.CLEAN_UP;
                }
            }

            if(state == STATE.CONTINUE) {
                imageName = createDockerImage.execute();
                if(createDockerImage.getStatus() != STATUS.SUCCESS) {
                    state = STATE.CLEAN_UP;
                }
            }

            if(state == STATE.CONTINUE) {
                runDockerBuild.setImageName(imageName);
                containerName = runDockerBuild.execute();
                if(runDockerBuild.getStatus() != STATUS.SUCCESS) {
                    state = STATE.CLEAN_UP;
                }
            }

            if(state == STATE.CONTINUE) {
                extractBuildResults.setContainerName(containerName);
                extractBuildResults.execute();
                if(extractBuildResults.getStatus() != STATUS.SUCCESS) {
                    state = STATE.CLEAN_UP;
                } else {
                    buildArtifacts.add(new File(repoDir, "maven"));
                }
            }
        } finally {
            // clean up
            if(!NOT_SET.equals(containerName)) {
                deleteDockerContainer.setContainerName(containerName);
                deleteDockerContainer.execute();
            }
            if(!NOT_SET.equals(imageName)) {
                deleteDockerImage.setImageName(imageName);
                deleteDockerImage.execute();
            }

            executor.shutdown();
        }

        fireBuildFinished(repo);
        return buildArtifacts;
    }
	
}
