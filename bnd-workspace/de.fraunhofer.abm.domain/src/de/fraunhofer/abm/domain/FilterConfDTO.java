package de.fraunhofer.abm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FilterConfDTO {

	public int maxLocations;
	
	public HashMap<String,Integer> FanInFanOut = new HashMap<>();
	
	public HashMap<String,Boolean> registered = new HashMap<>();
	
	@JsonIgnore
	public FilterDTO q;
	

	
	
}
