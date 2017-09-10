package de.fraunhofer.abm.hermes;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.HermesBuilder;
import de.fraunhofer.abm.builder.api.HermesBuilderFactory;
import de.fraunhofer.abm.builder.api.HermesProgressListener;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.VersionDTO;



public class HermesProcess implements Callable<HermesResultDTO> {
	
	 private static final transient Logger logger = LoggerFactory.getLogger(HermesProcess.class);
	 
	 public static enum STATUS {
	        WAITING, RUNNING, FINISHED, CANCELLED, FAILED
	    }
	 
	 private STATUS status = STATUS.WAITING;
	 private VersionDTO version;
	 private File workspace;
	 private List<HermesBuilderFactory> hermesBuilderFactories;
	 private Future<HermesResultDTO> futureHermesResult;
	 private HermesResultDTO hermesResult;
	 private List<HermesProgressListener> listeners = new ArrayList<>();
	 private String id;
	 private HermesResultDao hermesResultDao;
	 private HermesBuilder builder;
	 
	 
	 public HermesProcess(VersionDTO version,List<HermesBuilderFactory> hermesFactories,HermesResultDao hermesResultDao)
	 {
		 this.version = version;
		 //this.workspace = workspace;
		 this.hermesBuilderFactories = hermesFactories;
		 this.hermesResultDao = hermesResultDao;
		 this.id = UUID.randomUUID().toString();
		 this.hermesResult = new HermesResultDTO();
		 this.hermesResult.id = this.id;
		 this.hermesResult.status = status.toString();
		 createHermesResultStructure();
		 
	 }
	 
	 private void createHermesResultStructure()
	 {
		 this.hermesResult.versionId = version.id;
		 //this.hermesResult.dir = workspace.getAbsolutePath();

		 //Need to add build steps in HermesBuild 
		 
		 hermesResultDao.save(hermesResult);
	 }
	 
	 @Override
	 public HermesResultDTO call() throws Exception{
		
		 this.status = STATUS.RUNNING;
		 hermesResult.status = this.status.toString();
		 hermesResult.date = new Date();
		 hermesResultDao.update(hermesResult);
		 String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(hermesResult.date);
		 
		
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
