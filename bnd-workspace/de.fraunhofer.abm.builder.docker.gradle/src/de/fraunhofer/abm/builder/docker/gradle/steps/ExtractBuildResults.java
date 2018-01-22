package de.fraunhofer.abm.builder.docker.gradle.steps;

import java.io.File;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class ExtractBuildResults extends AbstractDockerStep<Void> {

	private static final transient Logger logger = LoggerFactory.getLogger(ExtractBuildResults.class);

    private String containerName;
    private File repoDir;

    public ExtractBuildResults(RepositoryDTO repo, ExecutorService executor, File repoDir) {
        super(repo, executor);
        this.repoDir = repoDir;
        this.name = "Extract built artifacts";
    }

    @Override
    public Void execute() {
        setStatus(STATUS.IN_PROGRESS);
        logger.info("Fetching build results");
        try {
            Result result = exec("docker cp " + containerName + ":/tmp/gradle/ .", repoDir);
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch (Throwable t) {
            logger.error("Couldn't extract build results; container:" + containerName, t);
            errorOutput = BuildUtils.createErrorString("Couldn't extract build results; container:" + containerName, t);
            setThrowable(t);
        }
        return null;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
	
	
}
