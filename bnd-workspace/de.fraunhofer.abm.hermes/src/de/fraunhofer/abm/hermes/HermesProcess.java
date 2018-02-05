package de.fraunhofer.abm.hermes;




import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.builder.api.HermesProgressListener;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.hermes.impl.HermesConfiguration;
import de.fraunhofer.abm.hermes.impl.HermesProjectsImpl;




public class HermesProcess implements Callable<HermesResultDTO> {
	
	 private static final transient Logger logger = LoggerFactory.getLogger(HermesProcess.class);
	 
	 public static enum STATUS {
	        WAITING, RUNNING, FINISHED, CANCELLED, FAILED
	    }
	 
	 private STATUS status = STATUS.WAITING;
	 private VersionDTO version;
	 private List<RepositoryDTO> repo;
	 HashMap<String,String> projects = new HashMap<String,String>();
	 private String repoDir;
	 private Future<HermesResultDTO> futureHermesResult;
	 private HermesResultDTO hermesResult;
	 private HermesDocker hermesDocker;
	 private List<HermesProgressListener> listeners = new ArrayList<>();
	 private String id;
	 private HermesResultDao hermesResultDao;
	 private String result;
	 private File hermesWS;
	 
	 
	 public HermesProcess(VersionDTO version, File hermesWS , String repoDir,List<RepositoryDTO> repo,HermesResultDao hermesResultDao)
	 {
		 this.version = version;
		 this.hermesWS = hermesWS;
		 this.hermesResultDao = hermesResultDao;
		 this.id = UUID.randomUUID().toString();
		 this.hermesResult = new HermesResultDTO();
		 this.hermesResult.id = this.id;
		 this.hermesResult.status = status.toString();
		 this.hermesResult.versionId = version.id;
		 this.hermesResult.dir = repoDir;
		 hermesResultDao.save(hermesResult);
		 this.repoDir = repoDir;
		 this.repo = repo;
		 
	 }
	 

	 
	 @Override
	 public HermesResultDTO call() throws Exception{
		
		 this.status = STATUS.RUNNING;
		 hermesResult.status = this.status.toString();
		 hermesResult.date = new Date();
		 hermesResultDao.update(hermesResult);
		
		 
		 try{
			 HermesProjects hermesProject = new HermesProjectsImpl(hermesWS);
			 for(int i=0;i<repo.size();i++) {
				 
				   String cmd[] = {"/bin/sh","-c","ls | grep \""+repo.get(i).id+"*"+"\""};
				 
				 
				    Process p = Runtime.getRuntime().exec(cmd,null,new File(repoDir));
					BufferedReader s  = new BufferedReader(new InputStreamReader(p.getInputStream()));
					//BufferedReader e = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String line;
					line = s.readLine();
				    logger.info("Adding Project into Hermes.json file with ProjectId:{} and ProjectPath:{}",repo.get(i).name,"repodir/"+line);
					projects.put(repo.get(i).name, "/repodir/"+line);
					
				
				 hermesProject.addProjects(projects);
			    hermesDocker = new HermesDocker(repoDir , hermesWS);
				 
			 }
			 
			 
					 
			 logger.debug("Running hermes steps");
			 result =  hermesDocker.hermesRun();
			 
			/* hermesDocker.addHermesProgressListener(new HermesProgressListener(){
					
				 @Override
				 public void hermesStepChanged(HermesStep<?> step){
					 switch(step.getStatus()){
					 case SUCCESS:
						 logger.info("Hermes step finished with success: {}\n {}",step.getName(),step.getOutput());
						 break;
					 case FAILED:
						 logger.info("Hermes step finished with error: {}\nSTDOUT:{}\nSTDERR:{}",step.getName(),step.getOutput(),step.getErrorOutput());
						 
						 break;
					 default:
						 logger.info("Hermes Step status changed: {} - {}",step.getName(),step.getStatus());
					 }
					 
				 }
				 
				   @Override
				   public void hermesInitialized(File repoDir,List<HermesStep<?>> steps){
					   logger.info("Hermes has been initialized");
					   
					   
					   
				   }
					
				   @Override 
				   public void hermesFinished(){
					   logger.info("Hermes Process {} is finished for {}", getId(), repo.name);
				   }
				 
				   @Override
				   public void hermesProcessComplete(){
					   logger.info("Hermes {} finished", getId());
				   }
				 
			});
			 
			for (HermesProgressListener hermesProgressListener : listeners) {
	              hermesDocker.addHermesProgressListener(hermesProgressListener);
	         }*/

			    
			    
			    
			    
			    
			 
			     
			 
		 }catch (Exception e) {
             
             logger.error("Couldn't complete hermes for the version " + version.id , e);
             this.status = STATUS.FAILED;
         }     
		
		 if(result == "CONTINUE") {
			 this.status = STATUS.FINISHED;
			 hermesResult.status = this.status.name();
			 hermesResultDao.update(hermesResult);
		 }
		 else {
			 this.status = STATUS.FAILED;
			 hermesResult.status = this.status.name();
			 hermesResultDao.update(hermesResult);
		 }
		 
		 
		 for (HermesProgressListener hermesProgressListener : listeners) {
	            hermesProgressListener.hermesProcessComplete();
	        }
		    
		
		
		 return hermesResult;
		 
	 }
	 
	 
	 public STATUS getStatus()
	 {
		 return status;
	 }
	 
	 public boolean isFinished()
	 {
		 return futureHermesResult.isDone();
	 }
	 
	 
	 public void setFutureHermesResult(Future<HermesResultDTO> futureHermesResult)
	 {
		 this.futureHermesResult = futureHermesResult;
		 
	 }
	 
	 
	 public void addHermesProgressListener(HermesProgressListener hpl)
	 {
		 listeners.add(hpl);
		 
	 }
	 
	 public void removeHermesProgressListener(HermesProgressListener hpl)
	 {
		 listeners.remove(hpl);
		 	 
	 }
	 
	 
	 
	 public String getId()
	 {
		 return id;
	 }
	 
	 
	 public void setId(String id)
	 {
		 this.id = id;
	 }
	 
	 
	 public VersionDTO getVersion()
	 {
		 return version;
	 }
	 
	 
	 
	 public String cancel()
	 {
		 this.status = STATUS.CANCELLED;
		 this.hermesResult.status =STATUS.CANCELLED.toString();
		 hermesResultDao.update(hermesResult);
		 boolean futureCancelled = futureHermesResult.cancel(true);
		 
		 if(futureCancelled)
		 {return this.id;}
		 else{return null;}
		 
		
		 
	 }
	 
	 
	 public HermesResultDTO getHermesResultDTO()
	 {
		 return this.hermesResult;
	 }

}
