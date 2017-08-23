package de.fraunhofer.abm.hermes;

import de.fraunhofer.abm.domain.VersionDTO;

public interface Hermes {
	
	public HermesProcess initialize(VersionDTO version) throws Exception;
	public void start(HermesProcess hermesProcess) throws Exception;
    public HermesProcess getHermesProcess(String id);
}
