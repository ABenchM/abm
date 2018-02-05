package de.fraunhofer.abm.hermes.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;


public class StopDocker extends AbstractHermesStep<Void> {

	private static final transient Logger logger = LoggerFactory.getLogger(StopDocker.class);

	private String containerName;
	private String repoDir;
    
	
	public StopDocker(String repoDir/* , ExecutorService executor */) {
		super(repoDir/* , executor */);
		this.repoDir = repoDir;
		this.name = "Stop Docker for Hermes";
	}

	@Override
	public Void execute() {
		setStatus(STATUS.IN_PROGRESS);
		logger.info("Stopping docker container {}", containerName);

		try {

			Result result = exec("docker stop " + containerName, new File(repoDir));
			output = result.stdout;
			errorOutput = result.stderr;
			setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
		} catch (InterruptedException ie) {
			logger.info("Interrupted process at {}", getName());
			setStatus(STATUS.CANCELLED);
		} catch (Throwable t) {
			logger.error("Couldn't stop Hermes Docker: " + containerName, t);
			errorOutput = BuildUtils.createErrorString("Couldn't stop Hermes docker container:" + containerName, t);
			setThrowable(t);
		}

		return null;

	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}
