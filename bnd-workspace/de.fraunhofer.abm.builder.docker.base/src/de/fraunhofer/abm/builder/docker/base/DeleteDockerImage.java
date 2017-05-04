package de.fraunhofer.abm.builder.docker.base;

import java.io.File;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class DeleteDockerImage extends AbstractDockerStep<Void> {

    private static final transient Logger logger = LoggerFactory.getLogger(DeleteDockerImage.class);

    private String imageName;
    private File repoDir;

    public DeleteDockerImage(RepositoryDTO repo, ExecutorService executor, File repoDir) {
        super(repo, executor);
        this.repoDir = repoDir;
        this.name = "Delete Docker image";
    }

    @Override
    public Void execute() {
        setStatus(STATUS.IN_PROGRESS);
        logger.info("Deleting docker image {}", imageName);
        try {
            Result result = exec("docker rmi " + imageName, repoDir.getParentFile().getParentFile());
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch (Throwable t) {
            logger.error("Couldn't delete docker image:" + imageName, t);
            errorOutput = BuildUtils.createErrorString("Couldn't delete docker image:" + imageName, t);
            setThrowable(t);
        }
        return null;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
