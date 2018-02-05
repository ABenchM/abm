package de.fraunhofer.abm.hermes.impl;




import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
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

@Designate(ocd = HermesConfiguration.class)
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
	
	private File hermesConfigDir;
	
	private Map<String,HermesProcess> hermesProcesses = new HashMap<>();
	
	    
		@Override
	    public HermesProcess initialize(VersionDTO version , String repoDir , List<RepositoryDTO> repo) throws Exception {
	       File hermesWS = createHermesWS(); 
	       HermesProcess hermesProcess = new HermesProcess(version,hermesWS,repoDir,repo,hermesResultDao);
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
	    
	    private File createHermesWS() {
	        File hermesWS = hermesConfigDir;
	        if(!hermesWS.exists())
	        	{hermesWS.mkdirs();}
	        return hermesWS;
	    }
	
		@Activate
	    void activate(HermesConfiguration config) {
	        
	        String hermesConfigDir = config.hermesConfigDir();
	        logger.info("Creating Hermes Config root: {}", hermesConfigDir);
	        this.hermesConfigDir = new File(hermesConfigDir);
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
