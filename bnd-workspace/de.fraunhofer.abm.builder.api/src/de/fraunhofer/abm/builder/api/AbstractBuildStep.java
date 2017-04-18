package de.fraunhofer.abm.builder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;

public abstract class AbstractBuildStep<T> implements BuildStep<T> {

    protected String id = UUID.randomUUID().toString();
    protected String name = "Unnamed step";
    protected STATUS status = STATUS.WAITING;
    protected String output = "";
    protected String errorOutput = "";
    protected Throwable throwable;
    protected RepositoryDTO repository;

    private List<BuildStepListener> listeners = new ArrayList<>();

    public AbstractBuildStep(RepositoryDTO repository) {
        this.repository = repository;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public STATUS getStatus() {
        return status;
    }

    /**
     * Sets the status of this build step to the given status and fires a status changed event
     * @param status
     */
    protected void setStatus(STATUS status) {
        this.status = status;
        fireStatusChanged();
    }

    @Override
    public RepositoryDTO getRepository() {
        return repository;
    }

    public void setRepository(RepositoryDTO repository) {
        this.repository = repository;
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public String getErrorOutput() {
        return errorOutput;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Sets the throwable and sets the status to FAILED
     * @param t
     */
    protected void setThrowable(Throwable t) {
        this.throwable = t;
        setStatus(STATUS.FAILED);
    }

    @Override
    public void addBuildStepListener(BuildStepListener bsl) {
        listeners.add(bsl);
    }

    @Override
    public void removeBuildStepListener(BuildStepListener bsl) {
        listeners.remove(bsl);
    }

    protected void fireStatusChanged() {
        for (BuildStepListener buildStepListener : listeners) {
            buildStepListener.statusChanged(this);
        }
    }

    @Override
    public BuildStepDTO toDTO(int index) {
        BuildStepDTO dto = new BuildStepDTO();
        dto.idx = index;
        dto.id = getId();
        dto.name = getName();
        dto.status = getStatus().name();
        dto.stderr = getErrorOutput();
        dto.stdout = getOutput();
        return dto;
    }
}
