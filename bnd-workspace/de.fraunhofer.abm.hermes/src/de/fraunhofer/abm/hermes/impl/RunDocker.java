package de.fraunhofer.abm.hermes.impl;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;
import de.fraunhofer.abm.domain.HermesStepDTO;



public class RunDocker extends AbstractHermesStep<String> {
	
	private static final transient Logger logger = LoggerFactory.getLogger(RunDocker.class);
	
	private String imageName;
	private String repoDir;
	private String dir;
	
	public RunDocker(String repoDir /*, ExecutorService executor*/)
	{
		super(repoDir/*,executor*/);
		this.repoDir = repoDir;
		this.name = "Run Docker for Hermes";
		dir = repoDir.substring(repoDir.lastIndexOf('\\')+1);
	}
	
    
	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}
	
	
	
	@Override
	public String execute() throws Exception
	{ setStatus(STATUS.IN_PROGRESS);
	
		  String containerName = UUID.randomUUID().toString();
	        try {
	            logger.debug("Running Hermes docker container:{}", containerName);
	            /*Result result = exec("docker run -v /c/Ankur/shk/suitebuilder/"+dir+ ":/root/OPAL/repoDir --name "+containerName+" "+imageName+" bash" , new File(repoDir));
	            output = result.stdout;
	            errorOutput = result.stderr;
	            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
	            System.out.println(getStatus() );
	            System.out.println(errorOutput);*/
	        	Runtime.getRuntime().exec("docker run -v /c/Ankur/shk/suitebuilder"+dir+":/root/OPAL/repoDir -i --name "+containerName+" opalj/sbt_scala_javafx  bash");
	        } catch (Throwable t) {
	            logger.error("Couldn't run Hermes Docker: " + containerName, t);
	            errorOutput = BuildUtils.createErrorString("Couldn't run Hermes docker container:" + containerName, t);
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
