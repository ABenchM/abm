package de.fraunhofer.abm.builder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.fraunhofer.abm.domain.RepositoryDTO;
// TODO secure firing of events by wrapping them in a try/catch block?!?
public abstract class AbstractProjectBuilder implements ProjectBuilder, BuildStepListener {

    private List<BuildProgressListener> listeners = new ArrayList<>();
    protected List<BuildStep<?>> buildSteps = new ArrayList<>();

    private Lock listenerLock = new ReentrantLock();

    @Override
    public void addBuildProgressListener(BuildProgressListener bpl) {
        try {
            listenerLock.lock();
            listeners.add(bpl);
        } finally {
            listenerLock.unlock();
        }
    }

    @Override
    public void removeBuildProgressListener(BuildProgressListener bpl) {
        try {
            listenerLock.lock();
            listeners.remove(bpl);
        } finally {
            listenerLock.unlock();
        }
    }

    @Override
    public void statusChanged(BuildStep<?> step) {
        fireBuildStepChanged(step);
    }

    @Override
    public List<BuildStep<?>> getBuildSteps() {
        return buildSteps;
    }

    protected void fireBuildInitialized(RepositoryDTO repository, List<BuildStep<?>> steps) {
        try {
            listenerLock.lock();
            for (BuildProgressListener listener : listeners) {
                listener.buildInitialized(repository, steps);
            }
        } finally {
            listenerLock.unlock();
        }
    }

    protected void fireBuildStepChanged(BuildStep<?> step) {
        try {
            listenerLock.lock();
            for (BuildProgressListener listener : listeners) {
                listener.buildStepChanged(step);
            }
        } finally {
            listenerLock.unlock();
        }
    }

    protected void fireBuildFinished(RepositoryDTO repository) {
        try {
            listenerLock.lock();
            for (BuildProgressListener listener : listeners) {
                listener.buildFinished(repository);
            }
        } finally {
            listenerLock.unlock();
        }
    }

    @Override
    public <T> BuildStep<T> addBuildStep(BuildStep<T> step) {
        buildSteps.add(step);
        step.addBuildStepListener(this);
        return step;
    }
}
