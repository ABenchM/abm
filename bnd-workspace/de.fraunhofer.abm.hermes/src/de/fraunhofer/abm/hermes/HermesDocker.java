package de.fraunhofer.abm.hermes;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	private ExecutorService executor;

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

	
	
	public HermesDocker(String repoDir) {
		this.repoDir = repoDir;
		// executor = Executors.newCachedThreadPool();
		logger.info("Adding Hermes Process steps");

		runDocker = (RunDocker) addHermesStep(new RunDocker(repoDir/* ,executor */));
		moveConf = (MoveConf) addHermesStep(new MoveConf(repoDir/* ,executor */));
		runHermes = (RunHermes) addHermesStep(new RunHermes(repoDir/* ,executor */));
		extractResults = (ExtractResults) addHermesStep(new ExtractResults(repoDir/* ,executor */));
		stopDocker = (StopDocker) addHermesStep(new StopDocker(repoDir/* ,executor */));
		deleteHermesContainer = (DeleteHermesContainer) addHermesStep(new DeleteHermesContainer(repoDir));
		// fireHermesInitialized(repoDir,hermesSteps);
	}

	public void hermesRun() throws Exception {

		String containerName = NOT_SET;
		String csvName = NOT_SET;

		try {
			/*UUID uuid = UUID.randomUUID();
			File f = new File("/home/ankur/scripts/docker.sh");
			ProcessBuilder pb = new ProcessBuilder("sh", "/home/ankur/scripts/docker.sh", uuid.toString());
			
			pb.redirectOutput(Redirect.to(new File("/home/ankur/scripts/out.txt")));
			Process p = pb.start();
			
			p.wait()*/;
			
			runDocker.setImageName("opalj/sbt_scala_javafx ");
			containerName = runDocker.execute();
			System.out.println("in hermes docker container name " + containerName);
			Process runCheck = Runtime.getRuntime().exec("docker ps");
			BufferedReader r = new BufferedReader(new InputStreamReader(runCheck.getInputStream()));
			BufferedReader e = new BufferedReader(new InputStreamReader(runCheck.getErrorStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
			}

			while (true) {
				line = e.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
			}
			moveConf.setWorkSpace("/opt/abm/");
			moveConf.setContainerName(containerName);
			moveConf.setFileName("hermes.json");
			System.out.println("Executing moveconf Hermes.json");
			boolean result = moveConf.execute();
			moveConf.setFileName("application.conf");
			System.out.println("Executing moveconf application.conf");
			result = moveConf.execute();

			
			csvName = UUID.randomUUID().toString();
			runHermes.setContainerName(containerName);
			runHermes.setCsvName(csvName);
			runHermes.execute();
            extractResults.setContainerName(containerName);
			extractResults.setCsvName(csvName);
			extractResults.execute();
			stopDocker.setContainerName(containerName);
			stopDocker.execute();
			deleteHermesContainer.setContainerName(containerName);
			deleteHermesContainer.execute();

			/*
			 * if(state == STATE.CONTINUE) {
			 * runDocker.setImageName("opalj/sbt_scala_javafx "); containerName =
			 * runDocker.execute(); System.out.println("in hermes docker container name "+
			 * containerName); if(runDocker.getStatus()!=STATUS.SUCCESS) { state =
			 * STATE.CLEAN_UP; } }
			 * 
			 * 
			 * if(state == STATE.CONTINUE) {
			 * moveConf.setWorkSpace("c:\\Ankur\\shk\\suitebuilder");
			 * moveConf.setFileName("hermes.json"); boolean result = moveConf.execute();
			 * moveConf.setFileName("application.conf"); result = moveConf.execute();
			 * if(moveConf.getStatus()!=STATUS.SUCCESS) { state = STATE.CLEAN_UP; } }
			 */

			/*
			 * if(state == STATE.CONTINUE){ runHermes.setContainerName(containerName);
			 * csvName = runHermes.execute(); if(runHermes.getStatus()!= STATUS.SUCCESS){
			 * state = STATE.CLEAN_UP; } } if(state == STATE.CONTINUE){
			 * extractResults.setContainerName(containerName);
			 * extractResults.setCsvName(csvName); extractResults.execute();
			 * if(extractResults.getStatus()!= STATUS.SUCCESS){ state = STATE.CLEAN_UP; } }
			 * if(state == STATE.CONTINUE){ stopDocker.setContainerName(containerName);
			 * stopDocker.execute(); if(stopDocker.getStatus()!=STATUS.SUCCESS) { state =
			 * STATE.CLEAN_UP; } }
			 */

		} finally {

			if (!NOT_SET.equals(containerName)) {
				// stopDocker.setContainerName(containerName);
				// stopDocker.execute();
				// deleteHermesContainer.setContainerName(containerName);
				// deleteHermesContainer.execute();
			}

			// executor.shutdown();
		}

		fireHermesFinished();

	}

}
