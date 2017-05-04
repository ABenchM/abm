package de.fraunhofer.abm.builder.docker.base;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class CreateDockerContainer extends AbstractDockerStep<String> {

    private static final transient Logger logger = LoggerFactory.getLogger(CreateDockerContainer.class);

    private String imageName;
    private File repoDir;

    public CreateDockerContainer(RepositoryDTO repo, ExecutorService executor, String imageName, File repoDir) {
        super(repo, executor);
        this.imageName = imageName;
        this.repoDir = repoDir;
        this.name = "Create Docker container";
    }

    @Override
    public String execute() {
        setStatus(STATUS.IN_PROGRESS);
        String containerName = UUID.randomUUID().toString();
        logger.info("Creating docker container {}", containerName);
        try {
            Result result = exec("docker run --name " + containerName + " " + imageName + " /bin/ls", repoDir);
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch (IOException | InterruptedException e) {
            logger.error("Couldn't create docker container " + containerName, e);
            errorOutput = BuildUtils.createErrorString("Couldn't create docker container " + containerName, e);
            setThrowable(e);
        }
        return containerName;
    }

}
