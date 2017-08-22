package de.fraunhofer.abm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HermesBuildDTO {

	public String id = UUID.randomUUID().toString();
	public List<HermesStepDTO> hermesSteps = new ArrayList<>();
	public String repositoryId;
	public String hermesResultId;
	
	
}
