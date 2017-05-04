package de.fraunhofer.abm.builder.api;

import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface BuildProgressListener {

    public void buildInitialized(RepositoryDTO repository, List<BuildStep<?>> steps);
    public void buildStepChanged(BuildStep<?> step);
    public void buildFinished(RepositoryDTO repository);
    public void buildProcessComplete();

}
