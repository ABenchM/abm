package de.fraunhofer.abm.builder.docker.ant.steps;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class RunDockerBuild  extends AbstractDockerStep<String> {

	private static final transient Logger logger = LoggerFactory.getLogger(RunDockerBuild.class);

    private String imageName;
    private File repoDir;

    public RunDockerBuild(RepositoryDTO repo, ExecutorService executor, File repoDir) {
        super(repo, executor);
        this.repoDir = repoDir;
        this.name = "Run docker ant build";
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String execute() {
        setStatus(STATUS.IN_PROGRESS);

        String containerName = UUID.randomUUID().toString();
        try {
            logger.debug("Running docker build container:{}", containerName);
            Result result = exec("docker run -v M2_REPO:/usr/share/maven/ref/repository --name " + containerName + " " + imageName + " mvn verify deploy:deploy -DaltDeploymentRepository=snapshots::default::file:///tmp/maven", repoDir);
            output = result.stdout;
            errorOutput = result.stderr;
            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
        } catch(InterruptedException ie) {
            logger.info("Interrupted build at {}", getName());
            setStatus(STATUS.CANCELLED);
        } catch (Throwable t) {
            logger.error("Couldn't run docker build container:" + containerName, t);
            errorOutput = BuildUtils.createErrorString("Couldn't run docker build container:" + containerName, t);
            setThrowable(t);
        }

        return containerName;
    }
	
}
