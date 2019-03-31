package de.fraunhofer.abm.zenodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by agupta on 19.11.2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepositionFile {

	public String id;
	public String filename;
	public Integer filesize;
	public String checksum;
	
	public Links links;
	
	
	/**
	 * Created by agupta on 19.11.18.
	 */
	 @JsonIgnoreProperties(ignoreUnknown = true)
	    public static class Links {
	        public String download;
	        public String self;
	    }
	 
	 
}

