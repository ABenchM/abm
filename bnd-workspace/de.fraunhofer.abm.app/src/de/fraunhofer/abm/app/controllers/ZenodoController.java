package de.fraunhofer.abm.app.controllers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.upb.cs.swt.zenodo.ZenodoAPI;
import de.upb.cs.swt.zenodo.ZenodoClient;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;


@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.zenodo")
public class ZenodoController implements REST {
	
	private static final transient Logger logger = LoggerFactory.getLogger(ZenodoController.class);
	
	@Reference
    private CollectionDao collectionDao;
	
	@Reference
	 private BuildResultDao buildResultDao;
	
	@Reference
	 private HermesResultDao hermesResultDao;
	
	 @Reference
	 private VersionDao versionDao;
	 
     
	 private static String url = "https://sandbox.zenodo.org/";
	 private static String token = "HWiH1QCdIj81fj0a9vB9knBzfH8puk55NXiEZqkumpILavP2BHgKnjgUEyc9";
	 
   
	 
	 public void postPublishCollection(String versionId) throws IOException
	 {
		 
		 Map<String, String> AUTHOR_BOOK_MAP = new HashMap<String,String>();
		 
		 AUTHOR_BOOK_MAP.put("Dan Simmons", "Hyperion");
		 AUTHOR_BOOK_MAP.put("Douglas Adams", "The Hitchhiker's Guide to the Galaxy");
			    
		
		String[] HEADERS = { "author", "title"};
			
		
		try {
		
//		 ZenodoAPI client = new ZenodoClient(url,token);
		 FileWriter out = new FileWriter("/home/ankur/SHK/book_new.csv");
		   
		 CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS));
		        AUTHOR_BOOK_MAP.forEach((author, title) -> {
					try {
						printer.printRecord(author, title);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		} catch(IOException e) {
			
			System.out.println(e);
			
		}
		 
	 }
	
	

}
