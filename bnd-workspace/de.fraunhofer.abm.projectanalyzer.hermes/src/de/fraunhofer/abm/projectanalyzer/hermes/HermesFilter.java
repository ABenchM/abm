package de.fraunhofer.abm.projectanalyzer.hermes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import de.fraunhofer.abm.domain.FilterConfDTO;

public class HermesFilter {

	private static final transient Logger logger = LoggerFactory.getLogger(HermesFilter.class);
	HashMap<String,Boolean> registered = new HashMap<>();
	private File FilterPath;
	int maxLocations;
	HashMap<String,Integer> temp = new HashMap<String,Integer>();
	HashMap<String,HashMap<String,Integer>> FanInFanOut = new HashMap<String,HashMap<String,Integer>>();

	
	Config config = ConfigFactory.parseFile(FilterPath);
	Config newconfig;
	FileWriter writer;
	ConfigObject co, co2;
	Set<Entry<String, ConfigValue>> configNode , configNode2;
	Iterator<Entry<String, ConfigValue>> itr , itr2;
	String key , k;
	ConfigValue value ,v;
	Entry<String, ConfigValue> fld , e;
	
	
	@Activate
    public void activate(Configuration config) {
        initFilterPath(config.filterpath());
        
    }
    
    @Deactivate
    public void deactivate() {
    }
	
	 private void initFilterPath(String path) {
	        FilterPath = new File(path);
	        if(!FilterPath.exists()) {
	            FilterPath.mkdirs();
	        }
	        logger.info("Using repo archive directory {}", FilterPath.getAbsolutePath());
	    }
	
	public void setRegistered()
	{
		config = ConfigFactory.parseFile(FilterPath);
		  ConfigList co = config.getList("org.opalj.hermes.queries.registered");
	    for(int i=0;i<co.size();i++)
	    {
	    	  co2= (ConfigObject) co.get(i); 	
	    	  registered.put(co2.get("query").unwrapped().toString(), (Boolean)co2.get("activate").unwrapped());
              //System.out.println( c.get("activate").unwrapped()); 
	    	  
	    }
	}
	
	public void setMaxLocations()
	{
		maxLocations = config.getInt("org.opalj.hermes.maxLocations");
	}
	
	public void setFIFO()
	{
		co = config.getObject("org.opalj.hermes.queries.FanInFanOut");
		configNode = co.entrySet();
		  itr = configNode.iterator();
		  while(itr.hasNext()){
 		    fld = itr.next();
 		    key = fld.getKey();
 		    co2 = config.getObject("org.opalj.hermes.queries.FanInFanOut."+key);
          configNode2 = co2.entrySet();
          itr2 = configNode2.iterator();
          while(itr2.hasNext())
           {e=itr2.next();
            k = e.getKey();
            v = e.getValue();
           // System.out.println(k +":"+v.unwrapped()); 	 
           temp.put(k, (Integer) v.unwrapped()) ;
           
           }
          FanInFanOut.put(key, temp);
 		   	   }
	}
		
	public	HashMap<String,Boolean> getFilters()
		{
		     setRegistered();
		     return registered;
		}
	public  HashMap<String,HashMap<String,Integer>> getFiFO()
    {
         setFIFO();   
         return FanInFanOut;
		
    }
	 public  int getMaxLocation()
	    {
		    setMaxLocations();
	    	return maxLocations;
	    }
		
	 public void updateMaxLocations(int ml) throws IOException
		{
		
			writer = new FileWriter(FilterPath);
			newconfig = config.withValue("org.opalj.hermes.maxLocations", ConfigValueFactory.fromAnyRef(ml));
			
			writer.write(newconfig.root().render(ConfigRenderOptions.concise().
			           setComments(true).setFormatted(true)).toString());
			writer.close();
		}	
	
	 public void updateFIFO(String key , String parameter,int value) throws IOException
	 {
		 writer = new FileWriter(FilterPath);
		 newconfig = config.withValue("org.opalj.hermes.queries.FanInFanOut."+key+"."+parameter, ConfigValueFactory.fromAnyRef(value));
		 
		 writer.write(newconfig.root().render(ConfigRenderOptions.concise().
		           setComments(true).setFormatted(true)).toString());
		writer.close();
		 
	 }
	 
	 public void updateFilter() throws IOException
	 {
		 config = ConfigFactory.parseFile(FilterPath);
			writer = new FileWriter(FilterPath);
			ConfigList co = config.getList("org.opalj.hermes.queries.registered");
			co.set(0, ConfigValueFactory.fromAnyRef(value));
	  	    for(int i=0;i<co.size();i++)
	  	    {
	  	    	  co2= (ConfigObject) co.get(i); 
	  	    	  
	  	    	//  if(co2.get("query").unwrapped().toString().contentEquals(query))
	  	    	  {
	  	    		newconfig = config.withValue("org.opalj.hermes.queries.registered.activate", ConfigValueFactory.fromAnyRef(value));
	  	    	  }
	  	    	 
	              
	  	    	  
	  	    }
	  	  
			
			writer.write(newconfig.root().render(ConfigRenderOptions.concise().
			           setComments(true).setFormatted(true)).toString());
			writer.close(); 
			
		
		 
	 }
	
	
	
	
}
