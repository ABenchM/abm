package de.fraunhofer.abm.hermes;

import java.io.IOException;
import java.util.HashMap;

public interface HermesProjects {

	public void addProjects(String id,String cp,String libcp_defaults) throws IOException;
	public void addProjects(HashMap<String,String> projects) throws IOException;
}
