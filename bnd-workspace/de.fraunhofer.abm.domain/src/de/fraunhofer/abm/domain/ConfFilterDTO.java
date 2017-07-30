package de.fraunhofer.abm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ConfFilterDTO {

	public int maxLocations;
	public List<FilterDTO> registered = new ArrayList<>();
	public HashMap<String,Integer> FanInFanOut = new HashMap<>();
	
	@JsonIgnore
	public FilterDTO q;
	

	
	
}
