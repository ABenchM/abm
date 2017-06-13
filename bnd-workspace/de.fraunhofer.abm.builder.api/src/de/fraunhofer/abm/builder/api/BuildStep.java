package de.fraunhofer.abm.builder.api;

import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;

public interface BuildStep<T> {

    public static enum STATUS {
        WAITING,
        IN_PROGRESS,
        FAILED,
        SUCCESS,
        CANCELLED
    }

    public String getId();
    public String getName();
    public STATUS getStatus();
    public String getOutput();
    public String getErrorOutput();
    public Throwable getThrowable();
    public T execute();
    public RepositoryDTO getRepository();

    public void addBuildStepListener(BuildStepListener bsl);
    public void removeBuildStepListener(BuildStepListener bsl);

    public BuildStepDTO toDTO(int index);
}
