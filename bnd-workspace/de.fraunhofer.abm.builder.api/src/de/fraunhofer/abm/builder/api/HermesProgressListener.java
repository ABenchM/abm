package de.fraunhofer.abm.builder.api;

import java.io.File;
import java.util.List;



public interface HermesProgressListener {
	
	public void hermesInitialized(File repoDir,List<HermesStep<?>> steps);
	public void hermesStepChanged(HermesStep<?> step);
	public void hermesFinished();
	public void hermesProcessComplete();

}
