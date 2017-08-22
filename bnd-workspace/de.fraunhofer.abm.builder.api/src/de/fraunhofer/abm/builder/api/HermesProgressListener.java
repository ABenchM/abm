package de.fraunhofer.abm.builder.api;

import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface HermesProgressListener {
	
	public void hermesInitialized(RepositoryDTO repository,List<HermesStep<?>> steps);
	public void hermesStepChanged(HermesStep<?> step);
	public void hermesFinished(RepositoryDTO repository);
	public void hermesProcessComplete();

}
