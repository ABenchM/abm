package de.fraunhofer.abm.hermes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface FilterResults {

	public Map<String,Map<String,Integer>> getFilterResults() throws JsonParseException, JsonMappingException, IOException;
    public String getKeyByValue(Map<String, List<String>> map, String value);
	
}
