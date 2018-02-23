package de.fraunhofer.abm.hermes.impl;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;




public class RunDocker extends AbstractHermesStep<String> {

	private static final transient Logger logger = LoggerFactory.getLogger(RunDocker.class);

	private String imageName;
	private String repoDir;
	private File hermesConfigDir;

	public RunDocker(String repoDir,File hermesConfigDir /* , ExecutorService executor */) {
		super(repoDir/* ,executor */);
		this.repoDir = repoDir;
		this.hermesConfigDir = hermesConfigDir;
		this.name = "Run Docker for Hermes";
		
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Override
	public String execute() throws Exception {
		setStatus(STATUS.IN_PROGRESS);

		String containerName = UUID.randomUUID().toString();
		try {
			logger.debug("Running Hermes docker container:{}", containerName);
			
			Process result = Runtime.getRuntime().exec("docker run -d -v " +repoDir+":/repodir -i --name "+containerName+" "+imageName+" bash",null, hermesConfigDir);
			
			TimeUnit.SECONDS.sleep(3);
			int exitValue = result.waitFor();
			setStatus(exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);

		} catch (InterruptedException ie) {
			logger.info("Interrupted build at {}", getName());
			setStatus(STATUS.CANCELLED);
		} catch (Throwable t) {
			logger.error("Couldn't run Hermes Docker: " + containerName, t);
			errorOutput = BuildUtils.createErrorString("Couldn't run Hermes docker container:" + containerName, t);
			setThrowable(t);
		}

		return containerName;
	}

}
