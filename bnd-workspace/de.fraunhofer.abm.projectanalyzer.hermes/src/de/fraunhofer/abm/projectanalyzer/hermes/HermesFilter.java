package de.fraunhofer.abm.projectanalyzer.hermes;


import java.io.IOException;
import java.util.HashMap;


public interface HermesFilter {
	
	public void setRegistered();
	public void setMaxLocations();
	public void setFIFO();
	HashMap<String,Boolean> getFilters();
	HashMap<String,HashMap<String,Integer>> getFiFO();
	public  int getMaxLocation();
	public void updateMaxLocations(int ml) throws IOException;
	public void updateFIFO(String key , String parameter,int value) throws IOException;
	public void updateFilter() throws IOException;
	
	
}
