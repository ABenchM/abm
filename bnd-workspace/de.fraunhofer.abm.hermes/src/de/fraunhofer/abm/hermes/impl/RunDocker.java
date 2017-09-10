package de.fraunhofer.abm.hermes.impl;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.builder.api.AbstractHermesStep;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.api.BuildStep.STATUS;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep.Result;
import de.fraunhofer.abm.domain.HermesStepDTO;
import de.fraunhofer.abm.hermes.HermesDocker;


public class RunDocker extends HermesDocker<String> {
	
	private static final transient Logger logger = LoggerFactory.getLogger(RunDocker.class);
	
	private String imageName;
	private File repoDir;
	
	public RunDocker(File repoDir , ExecutorService executor)
	{
		super(repoDir,executor);
		this.repoDir = repoDir;
		this.name = "Run Docker for Hermes";
	}
	
    
	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}
	
	@Override
	public String execute()
	{
		  String containerName = UUID.randomUUID().toString();
	        try {
	            logger.debug("Running Hermes docker container:{}", containerName);
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


	@Override
	public HermesStepDTO toDTO(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
