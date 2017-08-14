package de.fraunhofer.abm.docker.Hermes;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep.Result;
import de.fraunhofer.abm.docker.Hermes.HermesDocker;

public class RunHermes extends HermesDocker{

	private static final transient Logger logger = LoggerFactory.getLogger(RunHermes.class);
	
 	
	
	public void execute() throws InterruptedException
	
	{
		setStatus(STATUS.IN_PROGRESS);
		 try {
	            logger.debug("Running docker build container:{}", getContainerName());

	            Result result = execHermes("docker run -i --rm --name hermes opalj/sbt_scala_javafx bash",getConfDir());
	            output = result.stdout;
	            errorOutput = result.stderr;
	            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
	        } catch(InterruptedException ie) {
	            logger.info("Interrupted process", getContainerName());
	            setStatus(STATUS.CANCELLED);
	        } catch (Throwable t) {
	            logger.error("Couldn't run docker container:" + getContainerName(), t);
	            errorOutput = BuildUtils.createErrorString("Couldn't run docker container:" + getContainerName(), t);
	            setThrowable(t);
	        }
		
	}
	
}
