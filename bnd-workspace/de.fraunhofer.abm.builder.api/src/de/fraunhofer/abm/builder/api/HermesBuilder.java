package de.fraunhofer.abm.builder.api;

import java.io.File;
import java.util.List;

public interface HermesBuilder {

public void init(File repoDir);
List<File> build(File repoDir) throws Exception;
public List<HermesStep<?>> getHermesSteps();
public <T> HermesStep<T> addHermesStep(HermesStep<T> hermesStep);
public void addHermesProgressListener(HermesProgressListener hpl);
public void removeHermesProgressListener(HermesProgressListener hpl);

}
