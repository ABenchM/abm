package de.fraunhofer.abm.hermes.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;
import de.fraunhofer.abm.domain.HermesStepDTO;

public class DeleteHermesContainer extends AbstractHermesStep<Void> {

	private static final transient Logger logger = LoggerFactory.getLogger(DeleteHermesContainer.class);
	private String containerName;
	private String repoDir;
	BufferedReader r,e;
	String line;
	public DeleteHermesContainer(String repoDir) {
		super(repoDir);
		this.name = "Delete Hermes Container";
		// TODO Auto-generated constructor stub
	}

	@Override
	public Void execute() throws InterruptedException {
		setStatus(STATUS.IN_PROGRESS);
	       logger.info("Deleting Hermes container {}", containerName);
		  
	        try {
	        	 
	        	Process deleteContainer = Runtime.getRuntime().exec("docker rm "+containerName);
	        	r = new BufferedReader(new InputStreamReader(deleteContainer.getInputStream()));
				  e = new BufferedReader(new InputStreamReader(deleteContainer.getErrorStream()));
				  while (true) {
				         line = r.readLine();
				         if (line == null) { break; }
				         System.out.println(line);
				     }
					 
				     while (true) {
				         line = e.readLine();
				         if (line == null) { break; }
				         System.out.println(line);
				     }
	          /*  Result result = exec("docker rm " +containerName,new File(repoDir));
	            output = result.stdout;
	            errorOutput = result.stderr;
	            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);*/
	        } catch (Throwable t) {
	            logger.error("Couldn't delete Hermes container: " + containerName, t);
	            errorOutput = BuildUtils.createErrorString("Couldn't delete Hermes container:" + containerName, t);
	            setThrowable(t);
	        }
		return null;
	}

	public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

	
	@Override
	public HermesStepDTO toDTO(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
