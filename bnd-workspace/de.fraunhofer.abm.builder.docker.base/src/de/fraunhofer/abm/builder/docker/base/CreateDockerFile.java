package de.fraunhofer.abm.builder.docker.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.AbstractBuildStep;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class CreateDockerFile extends AbstractBuildStep<Void> {

    private static final transient Logger logger = LoggerFactory.getLogger(CreateDockerFile.class);

    private File dockerFile;
    private Bundle sourceBundle;

    public CreateDockerFile(RepositoryDTO repo, File dockerFile, Bundle source) {
        super(repo);
        this.dockerFile = dockerFile;
        this.name = "Create Docker file";
        this.sourceBundle = source;
    }

    @Override
    public Void execute() {
        setStatus(STATUS.IN_PROGRESS);
        logger.info("Creating docker file {}", dockerFile.getAbsolutePath());
        try (
                FileOutputStream out = new FileOutputStream(dockerFile);
                InputStream in = sourceBundle.getResource("Dockerfile").openStream())
        {
            new StreamRedirectThread(in, out).run();
            output = "Dockerfile has been created";
            setStatus(STATUS.SUCCESS);
        } catch (Throwable t) {
            logger.error("Couldn't copy docker file", t);
            errorOutput = BuildUtils.createErrorString("Couldn't copy docker file", t);
            setThrowable(t);
        }
        return null;
    }
}
