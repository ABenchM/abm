package de.fraunhofer.abm.hermes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import de.fraunhofer.abm.domain.FilterStatusDTO;

public interface FilterResults {

	
	public Map<String,Map<String,Integer>> getFilterResults(String repodir,String versionid,List<FilterStatusDTO> filters) throws   IOException;
    public String getKeyByValue(Map<String, List<String>> map, String value);
    public List<String> getHeaders(String versionid) throws MalformedURLException, IOException;
	
    
}
