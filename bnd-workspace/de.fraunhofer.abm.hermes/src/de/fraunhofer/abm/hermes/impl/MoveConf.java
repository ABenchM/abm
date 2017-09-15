package de.fraunhofer.abm.hermes.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;

import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;

import de.fraunhofer.abm.domain.HermesStepDTO;

public class MoveConf extends AbstractHermesStep<Boolean> {

	private static final transient Logger logger = LoggerFactory.getLogger(MoveConf.class);
	
	private String containerName;
	private String repoDir;
	private String fileName;
	private String workSpace;
	
	public MoveConf(String repoDir){
		super(repoDir);
		this.repoDir = repoDir;
		this.name = "Transfer Config files to Docker";
		this.fileName = fileName;
	}
	
	public void setContainerName(String containerName) {
	    this.containerName = containerName;
	}
	
	
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setWorkSpace(String workSpace) {
		this.workSpace = workSpace;
	}

	@Override
	public HermesStepDTO toDTO(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Boolean execute()
	{ 
		setStatus(STATUS.IN_PROGRESS);
		logger.info("Transferring Configuration files to Hermes Docker");
		try{
			
			Result result = exec("docker cp "+fileName+" "+containerName + ":/root/OPAL/DEVELOPING_OPAL/tools/src/main/resources", new File(workSpace));
	        output = result.stdout;
	        errorOutput = result.stderr;
	        setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
			 
		}catch(Throwable t){
			
			logger.error("Couldn't extract results; container:" + containerName, t);
	        errorOutput = BuildUtils.createErrorString("Couldn't extract results; container:" + containerName, t);
	        setThrowable(t);
		}
		
		if(getStatus()==STATUS.SUCCESS) return true;
		else
		return false;
		
		
	}
	
}
