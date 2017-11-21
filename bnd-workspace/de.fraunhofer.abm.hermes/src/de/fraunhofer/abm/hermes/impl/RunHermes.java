package de.fraunhofer.abm.hermes.impl;

import java.io.BufferedReader;
import java.io.File;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.builder.api.BuildUtils;

import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;



public class RunHermes extends AbstractHermesStep<String> {

	private static final transient Logger logger = LoggerFactory.getLogger(RunHermes.class);
	private String containerName;
	private String repoDir;
	private String csvName;
	BufferedReader r, e;
	String line;

	public RunHermes(String repoDir/* , ExecutorService executor */) {
		super(repoDir/* , executor */);
		this.repoDir = repoDir;
		this.name = "Run Hermes application through sbt";

	}

	@Override
	public String execute() throws InterruptedException {
		setStatus(STATUS.IN_PROGRESS);

		

		try {
			logger.info("Running Hermes Application");
			csvName = "hermesResults";
			Result result = exec("sh /opt/abm/docker.sh"+" "+containerName+" "+csvName, new File(repoDir));
			output = result.stdout;
			errorOutput = result.stderr;
			setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
		} catch (InterruptedException ie) {
			logger.info("Interrupted build at {}", getName());
			setStatus(STATUS.CANCELLED);
		} catch (Throwable t) {
			logger.error("Couldn't run Hermes Docker: " + containerName, t);
			errorOutput = BuildUtils.createErrorString("Couldn't run Hermes docker container:" + containerName, t);
			setThrowable(t);
		}

		return csvName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}
