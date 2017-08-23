package de.fraunhofer.abm.builder.api;

import java.io.File;

public interface HermesBuilderFactory {

	public HermesBuilder createHermesBuilder(File repoDir);
	
}
