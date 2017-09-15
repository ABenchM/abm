	package de.fraunhofer.abm.hermes.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractHermesStep;
import de.fraunhofer.abm.domain.HermesStepDTO;


public class ExtractResults extends AbstractHermesStep<Void> {
	
	  private static final transient Logger logger = LoggerFactory.getLogger(ExtractResults.class);
	  
	  BufferedReader r,e;
	  String line;
	  private String containerName;
	  private String repoDir;
	  private String csvName;
	  String[] env = environmentToArray();
	  
	  public ExtractResults(String repoDir /*, ExecutorService executor*/)
	  {
		  super(repoDir/*,executor*/);
		  this.repoDir = repoDir;
		  this.name = "Extract csv results";
	  }
	  
	  public void setCsvName(String name)
	  {
		  this.csvName = name;
	  }
	  
@Override
public Void execute()
{ 
	setStatus(STATUS.IN_PROGRESS);
	logger.info("Extracting Hermes results");
	try{
		
		 Process extractcsv = Runtime.getRuntime().exec("docker cp "+containerName+":/root/OPAL/DEVELOPING_OPAL/tools/"+csvName+".csv .",env,new File(repoDir));
		 
		    r = new BufferedReader(new InputStreamReader(extractcsv.getInputStream()));
			e = new BufferedReader(new InputStreamReader(extractcsv.getErrorStream()));
			
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
        /*output = result.stdout;
        errorOutput = result.stderr;
        setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);*/
		
	}catch(Throwable t){
		
		logger.error("Couldn't extract results; container:" + containerName, t);
        errorOutput = BuildUtils.createErrorString("Couldn't extract results; container:" + containerName, t);
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

public static String[] environmentToArray() {
    Map<String,String> env = System.getenv();
    String[] envArray = new String[env.size()];
    int index = 0;
    for (Entry<String,String> entry : env.entrySet()) {
        String arrayEntry = entry.getKey() + '=' + entry.getValue();
        envArray[index++] = arrayEntry;
    }
    return envArray;
}


	  
}
