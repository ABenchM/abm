package de.fraunhofer.abm.hermes.impl;

import java.io.BufferedReader;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;

public class ExtractResults extends AbstractHermesStep<Void> {

	private static final transient Logger logger = LoggerFactory.getLogger(ExtractResults.class);

	BufferedReader r, e;
	String line;
	private String containerName;
	private String repoDir;
	private String csvName;
	String[] env = environmentToArray();

	public ExtractResults(String repoDir /* , ExecutorService executor */) {
		super(repoDir/* ,executor */);
		this.repoDir = repoDir;
		this.name = "Extract csv results";
	}

	public void setCsvName(String name) {
		this.csvName = name;
	}

	@Override
	public Void execute() {
		setStatus(STATUS.IN_PROGRESS);
		logger.info("Extracting Hermes results");
		try {

			Result result = exec(
					"docker cp " + containerName + ":/root/OPAL/DEVELOPING_OPAL/tools/" + csvName + ".csv .",
					new File(repoDir));

			output = result.stdout;
			errorOutput = result.stderr;
			setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);

		} catch (Throwable t) {

			logger.error("Couldn't extract results; container:" + containerName, t);
			errorOutput = BuildUtils.createErrorString("Couldn't extract results; container:" + containerName, t);
			setThrowable(t);
		}
		return null;

	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}
