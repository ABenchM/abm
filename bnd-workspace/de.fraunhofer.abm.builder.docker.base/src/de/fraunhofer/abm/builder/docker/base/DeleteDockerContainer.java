package de.fraunhofer.abm.builder.docker.base;

import java.io.File;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class DeleteDockerContainer extends AbstractDockerStep<Void> {

    private static final transient Logger logger = LoggerFactory.getLogger(DeleteDockerContainer.class);

    private String containerName;
    private File repoDir;

    public DeleteDockerContainer(RepositoryDTO repo, ExecutorService executor, File repoDir) {
        super(repo, executor);
        this.repoDir = repoDir;
        this.name = "Delete Docker container";
    }

    @Override
    public Void execute() {
        setStatus(STATUS.IN_PROGRESS);
        logger.info("Deleting docker container {}", containerName);
        try {
            Result result = exec("docker rm -v -f " + containerName, repoDir.getParentFile().getParentFile());
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch (Throwable t) {
            logger.error("Couldn't delete docker container:" + containerName, t);
            errorOutput = BuildUtils.createErrorString("Couldn't delete docker container:" + containerName, t);
            setThrowable(t);
        }

        return null;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

}
