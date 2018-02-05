package de.fraunhofer.abm.hermes;


import java.io.File;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.fraunhofer.abm.builder.api.HermesProgressListener;
import de.fraunhofer.abm.builder.api.HermesStep;
import de.fraunhofer.abm.builder.api.HermesStep.STATUS;
import de.fraunhofer.abm.builder.api.HermesStepListener;
import de.fraunhofer.abm.hermes.impl.DeleteHermesContainer;
import de.fraunhofer.abm.hermes.impl.ExtractResults;
import de.fraunhofer.abm.hermes.impl.MoveConf;
import de.fraunhofer.abm.hermes.impl.RunDocker;
import de.fraunhofer.abm.hermes.impl.RunHermes;
import de.fraunhofer.abm.hermes.impl.StopDocker;

public class HermesDocker implements HermesStepListener {

	private static final transient Logger logger = LoggerFactory.getLogger(HermesDocker.class);

	private static final String NOT_SET = "NOT_SET";
	

	private List<HermesProgressListener> listeners = new ArrayList<>();
	protected List<HermesStep<?>> hermesSteps = new ArrayList<>();

	private Lock listenerLock = new ReentrantLock();

	private RunDocker runDocker;
	private MoveConf moveConf;
	private RunHermes runHermes;
	private ExtractResults extractResults;
	private StopDocker stopDocker;
	private DeleteHermesContainer deleteHermesContainer;
	

	private enum STATE {
		CONTINUE, CLEAN_UP
	}

	private STATE state = STATE.CONTINUE;

	private final String repoDir;
	private final File hermesConfigDir;

	public void addHermesProgressListener(HermesProgressListener hpl) {
		try {
			listenerLock.lock();
			listeners.add(hpl);
		} finally {
			listenerLock.unlock();
		}
	}

	public void removeHermesProgressListener(HermesProgressListener hpl) {

		try {
			listenerLock.lock();
			listeners.remove(hpl);
		} finally {
			listenerLock.unlock();
		}
	}

	@Override
	public void statusChanged(HermesStep<?> step) {
		fireHermesStepChanged(step);
	}

	public List<HermesStep<?>> getHermesSteps() {

		return hermesSteps;
	}

	protected void fireHermesInitialized(File repoDir, List<HermesStep<?>> steps) {
		try {
			listenerLock.lock();
			for (HermesProgressListener listener : listeners) {
				listener.hermesInitialized(repoDir, steps);
			}
		} finally {
			listenerLock.unlock();
		}
	}

	protected void fireHermesStepChanged(HermesStep<?> step) {
		try {
			listenerLock.lock();
			for (HermesProgressListener listener : listeners) {
				listener.hermesStepChanged(step);
			}
		} finally {
			listenerLock.unlock();
		}
	}

	protected void fireHermesFinished() {
		try {
			listenerLock.lock();
			for (HermesProgressListener listener : listeners) {
				listener.hermesFinished();
			}
		} finally {
			listenerLock.unlock();
		}
	}

	public <T> HermesStep<T> addHermesStep(HermesStep<T> step) {
		hermesSteps.add(step);
		step.addHermesStepListener(this);
		return step;
	}

	public void init(File repoDir) {
		// executor = Executors.newCachedThreadPool();
		logger.info("Adding Hermes Process steps");
		// runDocker = (RunDocker) addHermesStep(new RunDocker(repoDir/*,executor*/));
		// runHermes = (RunHermes) addHermesStep(new RunHermes(repoDir/*,executor*/));
		// extractResults = (ExtractResults) addHermesStep(new
		// ExtractResults(repoDir/*,executor*/));
		// stopDocker = (StopDocker) addHermesStep(new
		// StopDocker(repoDir/*,executor*/));
		// deleteHermesContainer = (DeleteHermesContainer) addHermesStep(new
		// DeleteHermesContainer(repoDir));
		// fireHermesInitialized(repoDir,hermesSteps);

	}

	
	
	public HermesDocker(String repoDir , File hermesConfigDir) {
		this.repoDir = repoDir;
		this.hermesConfigDir = hermesConfigDir;
		// executor = Executors.newCachedThreadPool();
		logger.info("Adding Hermes Process steps");

		runDocker = (RunDocker) addHermesStep(new RunDocker(repoDir,hermesConfigDir/* ,executor */));
		moveConf = (MoveConf) addHermesStep(new MoveConf(repoDir,hermesConfigDir/* ,executor */));
		runHermes = (RunHermes) addHermesStep(new RunHermes(repoDir,hermesConfigDir/* ,executor */));
		extractResults = (ExtractResults) addHermesStep(new ExtractResults(repoDir/* ,executor */));
		stopDocker = (StopDocker) addHermesStep(new StopDocker(repoDir/* ,executor */));
		deleteHermesContainer = (DeleteHermesContainer) addHermesStep(new DeleteHermesContainer(repoDir));
		// fireHermesInitialized(repoDir,hermesSteps);
	}

	public String hermesRun() throws Exception {

		String containerName = NOT_SET;
		String csvName = NOT_SET;

		try {
		      logger.info("Hermes Docker Config Dir {}", hermesConfigDir);				
			
		      if (state == STATE.CONTINUE) {
				runDocker.setImageName("opalj/sbt_scala_javafx ");
				containerName = runDocker.execute();
				System.out.println("in hermes docker container name " + containerName);
				if (runDocker.getStatus() != STATUS.SUCCESS) {
					logger.debug("Running docker failed");
					state = STATE.CLEAN_UP;

				}
			}

			if (state == STATE.CONTINUE) {
				
				moveConf.setContainerName(containerName);
				moveConf.setFileName("hermes.json");
			    moveConf.execute();
			    if(moveConf.getStatus()!=STATUS.SUCCESS) {
			    	state = STATE.CLEAN_UP;
			    }
			}
			
			if (state == STATE.CONTINUE) {
				
				moveConf.setContainerName(containerName);
				moveConf.setFileName("application.conf");
			    moveConf.execute();
			    if(moveConf.getStatus()!=STATUS.SUCCESS) {
			    	state = STATE.CLEAN_UP;
			    }
			}
			

			if (state == STATE.CONTINUE) {
				runHermes.setContainerName(containerName);
				
				csvName = runHermes.execute();
				if (runHermes.getStatus() != STATUS.SUCCESS) {
					state = STATE.CLEAN_UP;
				}
			}
			if (state == STATE.CONTINUE) {
				extractResults.setContainerName(containerName);
				extractResults.setCsvName(csvName);
				extractResults.execute();
				if (extractResults.getStatus() != STATUS.SUCCESS) {
					state = STATE.CLEAN_UP;
				}
			}
			
			 

		} finally {

			if (!NOT_SET.equals(containerName)) {
				stopDocker.setContainerName(containerName);
				stopDocker.execute();
				deleteHermesContainer.setContainerName(containerName);
				deleteHermesContainer.execute();
			}
           
		
			 
			
			// executor.shutdown();
		}

		fireHermesFinished();
		return state.toString();

	}

}
