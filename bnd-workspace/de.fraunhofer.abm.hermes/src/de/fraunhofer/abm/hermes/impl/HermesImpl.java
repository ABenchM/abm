package de.fraunhofer.abm.hermes.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.abm.builder.api.HermesStep;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.HermesResultDTO;
import de.fraunhofer.abm.domain.HermesStepDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.hermes.Hermes;
import de.fraunhofer.abm.hermes.HermesProcess;





@Component(name="de.fraunhofer.abm.hermes.Hermes")
public class HermesImpl implements Hermes {

	
	@SuppressWarnings("unused")
	private static final transient Logger logger = LoggerFactory.getLogger(HermesImpl.class);
	
	private ThreadPoolExecutor executor;
	@SuppressWarnings("unused")
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
	
	@Reference
	private VersionDao versionDao;
	
	@Reference
	private HermesResultDao hermesResultDao;
	
	private Map<String,HermesProcess> hermesProcesses = new HashMap<>();
	
	    @Override
	    public HermesProcess initialize(VersionDTO version) throws Exception {
	        //File ws = createWorkspace();
	       // HermesProcess hermesProcess = new HermesProcess(version,/* ws, scms, builderFactories,*/ hermesResultDao/*, repoArchive*/);
	       // hermesProcesses.put(hermesProcess.getId(), hermesProcess);
	       // return hermesProcess;
	    	return null;
	    }
	
	    @Override
	    public void start(HermesProcess hermesProcess) throws Exception {
	        Future<HermesResultDTO> futureHermesResult = executor.submit(hermesProcess);
	        hermesProcess.setFutureHermesResult(futureHermesResult);

	        
	        VersionDTO version = hermesProcess.getVersion();
	        version.frozen = true;
	        versionDao.update(version);
	    }
	
	    @Override
	    public HermesProcess getHermesProcess(String id) {
	        return hermesProcesses.get(id);
	    }
        
	    public static List<HermesStepDTO> toHermesStepDTOs(List<HermesStep<?>> steps) {
	        List<HermesStepDTO> hermesStepDTOs = new ArrayList<>();
	        for (int i = 0; i < steps.size(); i++) {
	        	hermesStepDTOs.add(steps.get(i).toDTO(i));
	        }
	        return hermesStepDTOs;
	    }
	     
	    
	
}
