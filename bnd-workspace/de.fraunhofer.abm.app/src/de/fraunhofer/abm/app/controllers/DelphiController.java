package de.fraunhofer.abm.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name = "de.fraunhofer.abm.rest.delphi")
public class DelphiController implements REST {
    static Map<String, String> header = new HashMap<>();

	private String featuresString;
	
	public String getDelphifeatures() throws Exception {
	  String uri = "https://delphi.cs.uni-paderborn.de/api/features";   
	  String resp = HttpUtils.get(uri,header,"UTF-8");   
	  JSONObject json = new JSONObject(resp); 
	  System.out.println("delphi response"+json); 
		
		return featuresString;
	}

}
