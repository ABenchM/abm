package de.fraunhofer.abm.suitebuilder;

import de.fraunhofer.abm.domain.VersionDTO;

public interface SuiteBuilder {

    public BuildProcess initialize(VersionDTO collection) throws Exception;
    public void start(BuildProcess buildProcess) throws Exception;
    public BuildProcess getBuildProcess(String id);
}
