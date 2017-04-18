package de.fraunhofer.abm.builder.docker.base;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class CreateDockerImage extends AbstractDockerStep<String> {

    private static final transient Logger logger = LoggerFactory.getLogger(CreateDockerImage.class);

    private File repoDir;

    public CreateDockerImage(RepositoryDTO repo, ExecutorService executor, File repoDir) {
        super(repo, executor);
        this.repoDir = repoDir;
        this.name = "Create Docker image";
    }

    @Override
    public String execute() {
        setStatus(STATUS.IN_PROGRESS);
        String imageName = UUID.randomUUID().toString();
        logger.info("Building docker image {}", imageName);
        try {
            Result result = exec("docker build -t " + imageName + " .", repoDir);
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch (Throwable t) {
            logger.error("Couldn't create docker image " + imageName, t);
            errorOutput = BuildUtils.createErrorString("Couldn't create docker image " + imageName, t);
            setThrowable(t);
        }
        return imageName;
    }

}
