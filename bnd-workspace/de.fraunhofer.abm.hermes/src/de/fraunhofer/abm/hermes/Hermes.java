package de.fraunhofer.abm.hermes;



import java.util.List;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.VersionDTO;

public interface Hermes {
	
	public HermesProcess initialize(VersionDTO version,String repoDir, List<RepositoryDTO> repo) throws Exception;
	public void start(HermesProcess hermesProcess) throws Exception;
    public HermesProcess getHermesProcess(String id);
}
