package de.fraunhofer.abm.builder.api;

public interface BuildStepListener {

    public void statusChanged(BuildStep<?> step);
}
