package de.fraunhofer.abm.hermes.impl;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.hermes.FilterResults;

public class FilterResultsImpl implements FilterResults {

	
	private String repoDir;
	private String versionId;
	int noOfProjects;
    Map<String,List<String>> queryFeatureMap = new HashMap<String,List<String>>();
    Map<String,Map<String,Integer>> results =  new HashMap<String,Map<String,Integer>>();
    Map<String,Integer> project_result = new HashMap<String,Integer>();
    List<List<String>> projects = new ArrayList<List<String>>();
    FilterStatusDao filterStatusDao;
	
	 public void FilterResults(String repoDir , String versionId) {
	    	
	    	this.repoDir = repoDir;
	    	this.versionId = versionId;
	    	//this.name = "Filtering Results as per threshold value";
	    }
	
	  public String getKeyByValue(Map<String, List<String>> map, String value) {
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				if (entry.getValue().toString().contains(value)) {
					return entry.getKey();
				}
			}
			return null;
		}
	 
	  public Map<String,Map<String,Integer>> getFilterResults() throws JsonParseException, JsonMappingException, IOException{
	    	
	    	BufferedReader readCsv = new BufferedReader(new FileReader(repoDir+"/hermesResults.csv"));
	    	Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(readCsv);
	    	
	    	for (CSVRecord record : records) {
				List<String> row = new ArrayList<String>();
				for (int i = 0; i < record.size(); i++) {
					row.add(record.get(i));

				}
				projects.add(row);

			}

			noOfProjects = projects.size() - 1;
			
			ObjectMapper mapper = new ObjectMapper();
			
			String url = "file:///opt/abm/queryfeaturemap.json";
			String genrejson = IOUtils.toString(new URL(url));

			queryFeatureMap = mapper.readValue(genrejson, new TypeReference<HashMap<String, List<String>>>() {
			});

			
			//Initializing results with zero value l
			List<String> row = projects.get(0);
			int value , threshold =0;
			for(int i=1;i<=noOfProjects;i++) {
				
				project_result.clear();
				
				for (Map.Entry<String, List<String>> entry : queryFeatureMap.entrySet()) {

					project_result.put(entry.getKey(), 0);
	         }
				List<String> project = projects.get(i);
				
				for (int j = 12; j < row.size(); j++) {

					for (Map.Entry<String, Integer> entry : project_result.entrySet()) {

						if (getKeyByValue(queryFeatureMap, row.get(j)) == entry.getKey()) {

							value = entry.getValue();
							threshold = threshold + filterStatusDao.findThreshold(versionId, "org.opalj.hermes.queries."+entry.getKey());
							project_result.replace(entry.getKey(), value, value+Integer.parseInt(project.get(j)));
							

						}

					}

				}

				results.put(projects.get(i).get(0), project_result);
				
			}
			
			
	    	
	    	
	    	return results;
	    	
	    }
	 
	 
}
	