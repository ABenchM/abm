package de.fraunhofer.abm.projectanalyzer.hermes;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public interface HermesFilter {

	public void setRegistered();
	public void setMaxLocations();
	public void setFIFO();
	HashMap<String,Boolean> getFilters();
	Map<String,Integer> getFiFO();
	public  int getMaxLocation();
	public void updateMaxLocations(int ml) throws IOException;
	public void updateFIFO(String key , String parameter, int value) throws IOException;
	public void updateFilter(String query,boolean value) throws IOException;
	public void updateFilters(HashMap<String,Boolean> query) throws IOException;
	public void addFilter(String filterName , boolean activate) throws IOException;
}

