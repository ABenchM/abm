package de.fraunhofer.abm.docker.Hermes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.docker.Hermes.RunHermes.STATUS;







public class HermesDocker {
	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesDocker.class);
	
		
	
	private enum STATE {
        CONTINUE,
        CLEAN_UP
    }
	
	private STATE state = STATE.CONTINUE;
	
    private ExecutorService executor;
    
    RunHermes runDocker;
    RunHermes runSBT;
    RunHermes extractCSV;
    RunHermes stopDocker;
	
	
	
	
	public void init()
	{
		executor = Executors.newCachedThreadPool();
		runDocker = new RunHermes("RunDocker");
		runSBT = new RunHermes("RunSBT");
		extractCSV = new RunHermes("ExtractCSV");
		stopDocker = new RunHermes("StopDocker");           
	
	}
	
	public void run()
	{
		 try {
	            if(state == STATE.CONTINUE) {
	                runDocker.execute("docker run -i --rm -v "+runDocker.getConfDir()+" --name "+runDocker.getContainerName()+ " " + "opalj/sbt_scala_javafx bash" );
	                if(runDocker.getStatus() != STATUS.SUCCESS) {
	                    state = STATE.CLEAN_UP;
	                }
	            }

	            if(state == STATE.CONTINUE) {
	                runSBT.execute("docker exec"+ runSBT.getContainerName()+ "sbt" + " " + "\"project OPAL-DeveloperTools\""+" "+ "\"runMain org.opalj.hermes.HermesCLI src/main/resources/hermes.json -csv Hermes.csv\"");
	                if(runSBT.getStatus() != STATUS.SUCCESS) {
	                    state = STATE.CLEAN_UP;
	                }
	            }

	            if(state == STATE.CONTINUE) {
	                extractCSV.execute("docker cp hermes:/root/OPAL/DEVELOPING_OPAL/tools/Hermes.csv .");  //This csv file name can be changed to collection name as per the requirement.
	                
	                if(extractCSV.getStatus() != STATUS.SUCCESS) {
	                    state = STATE.CLEAN_UP;
	                }
	            }

	            if(state == STATE.CONTINUE) {
	                stopDocker.execute("docker stop"+" "+stopDocker.getContainerName());
	                
	                if(stopDocker.getStatus() != STATUS.SUCCESS) {
	                    state = STATE.CLEAN_UP;
	                } 
	            }
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	                      

	            executor.shutdown();
	        } 
	}
	 
	
	 
	 
	 
	 

	 
	 
	 
	 

	
	 
}
