package de.fraunhofer.abm.hermes;

import java.io.IOException;

public interface HermesProjects {

	public void addProjects(String id,String cp,String libcp_defaults) throws IOException;
	public void addProjects(String id,String cp) throws IOException;
}
