package de.fraunhofer.abm.hermes.impl;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.abm.domain.FilterStatusDTO;
import de.fraunhofer.abm.hermes.FilterResults;

@Designate(ocd = HermesConfiguration.class)
@Component(name = "de.fraunhofer.abm.hermes.FilterResults")
public class FilterResultsImpl implements FilterResults {

	private static final transient Logger logger = LoggerFactory.getLogger(FilterResultsImpl.class);
	
	
	int noOfProjects;
	File hermesConfigDir;
    Map<String,List<String>> queryFeatureMap = new HashMap<String,List<String>>();

    Map<String,Map<String,Integer>> projects =  new HashMap<String,Map<String,Integer>>();

    Map<String,Map<String,Integer>> results =  new HashMap<String,Map<String,Integer>>();
    
    List<List<String>> rows = new ArrayList<List<String>>();
    
	
    @Activate
    void activate(HermesConfiguration config) {
        
        String hermesConfigDir = config.hermesConfigDir();
        logger.info("Setting Filter Results config path: {}", hermesConfigDir);
        this.hermesConfigDir = new File(hermesConfigDir);
        if(!this.hermesConfigDir.exists()) {
        	this.hermesConfigDir.mkdirs();
        }
        this.hermesConfigDir = new File(hermesConfigDir,"queryfeaturemap.json");
    }
    
	 
	
	  public String getKeyByValue(Map<String, List<String>> map, String value) {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				if (entry.getValue().toString().contains(value)) {
					return entry.getKey();
				}
			}
			return null;
		}
	 
	  
	  public List<String> getHeaders(String versionid) throws MalformedURLException, IOException{
		  
		  List<String> headers = new ArrayList<String>();
		  
		  ObjectMapper mapper = new ObjectMapper();
			
		   logger.info("Path for the Query header is {}",hermesConfigDir);
			
		    String url = "file://"+hermesConfigDir.toString();
			
			String genrejson = IOUtils.toString(new URL(url));

			queryFeatureMap = mapper.readValue(genrejson, new TypeReference<HashMap<String, List<String>>>() {
			});
			
			  for(Map.Entry<String,List<String>> entry : queryFeatureMap.entrySet() ) {
	    		   
	    		   headers.add(entry.getKey());
	    	   }
			
			return headers; 
	  }
	  
	  public int getThreshold(String filtername,List<FilterStatusDTO> filters) {
		
		  int filter_threshold = 0;
		  
		  for(int it = 0 ;it < filters.size() ;it++) {
			  
			  if(filtername==filters.get(it).filtername)
			  filter_threshold = filters.get(it).threshold;
			  
		  }
		  
		  return filter_threshold;
		  
	  }
	
	  
	  public Map<String,Map<String,Integer>> getFilterResults(String repoDir, String versionid , List<FilterStatusDTO> filters) throws  IOException{
	    	
	    	BufferedReader readCsv = new BufferedReader(new FileReader(repoDir+"/hermesResults.csv"));
	    	//System.out.println(repoDir+"/hermesResults.csv");
	    	Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(readCsv);
	    	
	    	projects.clear();
	    	rows.clear();
	    	
	    	for (CSVRecord record : records) {
	    		
	    	    List<String> row = new ArrayList<String>();
	    	    
	    	    
	    		
				for (int i = 0; i < record.size(); i++) {
					
	                        row.add(record.get(i));
			         
				}
	             						
	             rows.add(row);
			        
	             
			}
	    	
            ObjectMapper mapper = new ObjectMapper();
			
			String url = "file://"+hermesConfigDir.toString();
			
			String genrejson = IOUtils.toString(new URL(url));

			queryFeatureMap = mapper.readValue(genrejson, new TypeReference<HashMap<String, List<String>>>() {
			});

			noOfProjects = rows.size() - 1;
			int value = 0 , threshold = 0;
			  for(int n=1;n<=noOfProjects;n++) {
		    	   
		    	   Map<String,Integer> project = new HashMap<String,Integer>();
		    	   value = 0;
		    	   
		    	   for(Map.Entry<String,List<String>> entry : queryFeatureMap.entrySet() ) {
		    		   
		    		   project.put(entry.getKey(), 0);
		    	   }
		    	   
		    	   
		    	   for (int i=11;i<rows.get(0).size();i++)
		           {
		    		   for(Map.Entry<String,Integer> entry: project.entrySet()) {
		        		   
		        		   if(getKeyByValue(queryFeatureMap, rows.get(0).get(i))==entry.getKey()) {
		        			   
		        			   value = entry.getValue();
		        		  
		        			   
		        			   
		        			  project.replace(entry.getKey(), value , value+Integer.parseInt(rows.get(n).get(i)));
		        		   }
		        		   
		        	   }  
		        	   
		           }
		    	   logger.info("Adding projects as per threshold value");
		    	   projects.put(rows.get(n).get(0), project);
		       }
		      
			  
					  
			  
						
               for(Map.Entry<String,Map<String,Integer>> entry : projects.entrySet()) {
            	   
            	   Map<String,Integer> result = new HashMap<String,Integer>();
            	   
            	   for(Map.Entry<String,Integer> inner_entry : entry.getValue().entrySet()) {
            		   
            		   
            		       threshold = getThreshold("org.opalj.hermes.queries."+inner_entry.getKey(), filters);
            		   
            		   
            		         if(inner_entry.getValue()>=threshold) {
            		        	 result.put(inner_entry.getKey(), inner_entry.getValue());
            		         }
            	  
            	   }
            	   
            	   results.put(entry.getKey(), result);
               }
			  
			  
			  
			  
			  return results;
	    	
	    }
	 
	 
}
	