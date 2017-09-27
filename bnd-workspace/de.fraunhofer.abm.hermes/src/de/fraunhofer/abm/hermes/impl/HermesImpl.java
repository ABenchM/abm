package de.fraunhofer.abm.hermes.impl;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.HermesBuilderFactory;
import de.fraunhofer.abm.builder.api.HermesStep;

import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.HermesStepDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.hermes.Hermes;
import de.fraunhofer.abm.hermes.HermesProcess;







@Component(name="de.fraunhofer.abm.hermes.Hermes")
public class HermesImpl implements Hermes {

	
	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesImpl.class);
	
	//private ThreadPoolExecutor executor;
	
	ExecutorService executor = Executors.newCachedThreadPool();
	
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	
	@Reference(cardinality= ReferenceCardinality.MULTIPLE, bind="addHermesFactory", unbind="removeHermesFactory", policy = ReferencePolicy.DYNAMIC)
	private volatile List<HermesBuilderFactory> hermesFactories = new ArrayList<>();
	
	@Reference
	private VersionDao versionDao;
	
	@Reference
	private HermesResultDao hermesResultDao;
	
	private Map<String,HermesProcess> hermesProcesses = new HashMap<>();
	
	    
		@Override
	    public HermesProcess initialize(VersionDTO version , String repoDir , RepositoryDTO repo) throws Exception {
	        
	       HermesProcess hermesProcess = new HermesProcess(version,repoDir,repo,hermesResultDao);
	       hermesProcesses.put(hermesProcess.getId(), hermesProcess);
	       return hermesProcess;
	    	
	    }
	
	    @Override
	    public void start(HermesProcess hermesProcess) throws Exception {
	        Future<HermesResultDTO> futureHermesResult = executor.submit(hermesProcess);
	        hermesProcess.setFutureHermesResult(futureHermesResult);
        
	        VersionDTO version = hermesProcess.getVersion();
	        version.filtered = true;
	        versionDao.update(version);
       
        
	    }
	
	    
	    @Activate
	    void activate(Configuration config) {
	        int coreSize = config.coreSize();
	        int maxSize = config.maximumPoolSize();
	        long keepAlive = config.keepAliveTime();

	        logger.info("Creating hermes builder thread pool with coreSize:{}, maxPoolSize:{}, keepAlive:{}", coreSize, maxSize, keepAlive);
	        executor = new ThreadPoolExecutor(coreSize, maxSize, keepAlive, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

	       // String workspaceRoot = config.workspaceRoot();
	       // logger.info("Creating workspace root: {}", workspaceRoot);
	       // this.workspaceRoot = new File(workspaceRoot);
	    }
	    
	    @Deactivate
	    void deactivate() {
	        List<Runnable> running = executor.shutdownNow();
	        if (!running.isEmpty()) {
	            logger.warn("Shutting down while builds {} are running", running);
	        }
	    }
	    
	    
	    @Override
	    public HermesProcess getHermesProcess(String id) {
	        return hermesProcesses.get(id);
	    }
        
	    
	    protected void addHermesFactory(HermesBuilderFactory factory) {
	        hermesFactories.add(factory);
	    }

	    protected void removeHermesFactory(HermesBuilderFactory factory) {
	        hermesFactories.remove(factory);
	    }

	    
	    public static List<HermesStepDTO> toHermesStepDTOs(List<HermesStep<?>> steps) {
	        List<HermesStepDTO> hermesStepDTOs = new ArrayList<>();
	        for (int i = 0; i < steps.size(); i++) {
	        	//hermesStepDTOs.add(steps.get(i).toDTO(i));
	        }
	        return hermesStepDTOs;
	    }
	     
	    
	
}
