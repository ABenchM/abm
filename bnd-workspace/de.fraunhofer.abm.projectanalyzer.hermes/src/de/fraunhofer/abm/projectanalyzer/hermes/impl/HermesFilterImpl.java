package de.fraunhofer.abm.projectanalyzer.hermes.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueFactory;

import de.fraunhofer.abm.domain.QueriesDTO;
import de.fraunhofer.abm.projectanalyzer.hermes.HermesFilter;


//@Designate(ocd = Configuration.class, factory=false)
@Component(name = "de.fraunhofer.abm.projectanalyzer.hermes.HermesFilter"/*, configurationPolicy = ConfigurationPolicy.OPTIONAL*/)
public class HermesFilterImpl implements HermesFilter {


	private static final transient Logger logger = LoggerFactory.getLogger(HermesFilter.class);
	
	
	HashMap<String,Boolean> registered = new HashMap<String,Boolean>();
	private File FilterPath = new File("/home/almacken/Desktop/abm/hermes/application.conf");//TODO: Actually get the configuration file to work
	
	
	Map<String,Integer> FanInFanOut = new TreeMap<String,Integer>();
	
	QueriesDTO dto = new QueriesDTO();
	Config config,newconfig;
	FileWriter writer;
	ConfigObject co, co2;
	Set<Entry<String, ConfigValue>> configNode , configNode2;
	String key , k;
	//ConfigValue value ,v;
		
	
	
	/*@Activate
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
	    }*/
	
	@Override 
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
	
	@Override
	public void setMaxLocations()
	{
        config = ConfigFactory.parseFile(FilterPath);
		
	    dto.maxlocations = config.getInt("org.opalj.hermes.maxLocations");
	}
	
	@Override
	public void setFIFO()
	{   
		config = ConfigFactory.parseFile(FilterPath);
		
		
		dto.fanin_categories = config.getInt("org.opalj.hermes.queries.FanInFanOut.fanin.categories");
		FanInFanOut.put("FanIn_Categories", dto.fanin_categories);
		dto.fanin_categorySize = config.getInt("org.opalj.hermes.queries.FanInFanOut.fanin.categorySize");
		FanInFanOut.put("FanIn_Categorysize", dto.fanin_categorySize);
		dto.fanout_categories = config.getInt("org.opalj.hermes.queries.FanInFanOut.fanout.categories");
		FanInFanOut.put("FanOut_Categories", dto.fanout_categories);
		dto.fanout_categorySize = config.getInt("org.opalj.hermes.queries.FanInFanOut.fanout.categorySize");
		FanInFanOut.put("FanOut_CategorySize", dto.fanout_categorySize);
		dto.ratio_categories = config.getInt("org.opalj.hermes.queries.FanInFanOut.ratio.categories");
		FanInFanOut.put("Ratio_Categories", dto.ratio_categories);
		dto.ratio_categorySize = config.getInt("org.opalj.hermes.queries.FanInFanOut.ratio.categorySize");
		FanInFanOut.put("Ratio_CategorySize", dto.ratio_categorySize);
		
		
	}
	
	@Override
	public	HashMap<String,Boolean> getFilters()
	{
	     setRegistered();
	     return registered;
	}
	
	@Override
	public  Map<String,Integer> getFiFO()
    {
         setFIFO();   
         return FanInFanOut;
		
    }
	
    @Override
    public int getMaxLocation()
    {
	    setMaxLocations();
    	return dto.maxlocations;
    }
    
    @Override
    public void updateMaxLocations(int ml) throws IOException
	{
		config = ConfigFactory.parseFile(FilterPath);
		writer = new FileWriter(FilterPath);
		newconfig = config.withValue("org.opalj.hermes.maxLocations", ConfigValueFactory.fromAnyRef(ml));
		
		writer.write(newconfig.root().render(ConfigRenderOptions.concise().
		           setComments(true).setFormatted(true)).toString());
		writer.close();
	}
	
    @Override
    public void updateFIFO(String key,String parameter, int value) throws IOException
	{
		config = ConfigFactory.parseFile(FilterPath);
		writer = new FileWriter(FilterPath);
		
		
		
		newconfig = config.withValue("org.opalj.hermes.queries.FanInFanOut."+key+"."+parameter, ConfigValueFactory.fromAnyRef(value));
		
		writer.write(newconfig.root().render(ConfigRenderOptions.concise().
		           setComments(true).setFormatted(true)).toString());
		writer.close();
		
	}
	 
    @Override
    public void updateFilter(String query,boolean value) throws IOException
	{ 
		ConfigList registered ;
		List<ConfigObject> newregistered = new ArrayList<ConfigObject>();
		config = ConfigFactory.parseFile(FilterPath);
		ConfigObject co , co2;
		registered = config.getList("org.opalj.hermes.queries.registered");
		
		for (int i=0;i<registered.size();i++)
		{
		   co = (ConfigObject) registered.get(i);
		  
		   if(co.get("query").unwrapped().toString().contains(query))
		   {	   
		   co2 = co.withValue("activate", ConfigValueFactory.fromAnyRef(value));
		   newregistered.add(co2);
		   }
		   else{
			   newregistered.add(co);
		   }
					   
		}
		
		newconfig = config.withValue("org.opalj.hermes.queries.registered", ConfigValueFactory.fromIterable(newregistered));
		writer.write(newconfig.root().render(ConfigRenderOptions.concise().
		           setComments(true).setFormatted(true)).toString());
		writer.close();
	    
	} 
    
    @Override
    public void updateFilters(HashMap<String,Boolean> query) throws IOException
	{ 
		ConfigList registered ;
		List<ConfigObject> newregistered = new ArrayList<ConfigObject>();
		config = ConfigFactory.parseFile(FilterPath);
		writer = new FileWriter(FilterPath);
		registered = config.getList("org.opalj.hermes.queries.registered");
		 boolean match ;
		for (int i=0;i<registered.size();i++)
		{
			   co = (ConfigObject) registered.get(i);
		       match = false;
			   for (Map.Entry<String, Boolean> entry : query.entrySet())
			   { 
				 if(entry.getKey().equals(co.get("query").unwrapped().toString()))
				   {	   
				   co2 = co.withValue("activate", ConfigValueFactory.fromAnyRef(entry.getValue()));
				   newregistered.add(co2);
				   match = true;
				   }
				 				  		   
		      }
			   
			  if(match==false)
			  {
				newregistered.add(co);  
			  }
				
		}
		
		
		
		
		newconfig = config.withValue("org.opalj.hermes.queries.registered", ConfigValueFactory.fromIterable(newregistered));
		writer.write(newconfig.root().render(ConfigRenderOptions.concise().
		           setComments(true).setFormatted(true)).toString());
		writer.close();
	    
	} 
      @Override
      public void addFilter(String filterName , boolean activate) throws IOException
	  {
		  config = ConfigFactory.parseFile(FilterPath);
		  
		  
    	  
	  }

	
	

}
