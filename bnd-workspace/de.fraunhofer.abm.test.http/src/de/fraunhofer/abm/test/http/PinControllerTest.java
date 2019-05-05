package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;


public class PinControllerTest extends AbstractHttpTest {
	
	String versionId="" ;
	String projectId = "";

	@Test
    public void testCollectionstatus() throws IOException, ParseException {
    	final int num200 = 200;
        // send a login request
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/login";
        String payload = "{\"username\": \""+USER+"\", \"password\": \""+PASSWORD+"\"}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(num200, response.getResponseCode());
        String sessionCookie = HttpUtils.getHeaderField(response.getHeader(), "Set-Cookie");
        Assert.assertTrue(sessionCookie.contains("JSESSIONID"));
        // try to get a secured resource
        headers.put("Cookie", sessionCookie);
         testCreateCollection();
         testmakeCollectionPublic();
         testpinCollection();
//         testunpinCollection();
        
    }	
	
	protected Map<String, String> login() throws IOException {
        login(USER, PASSWORD);
        Map<String, String> headers = HttpUtils.createFirefoxHeader();
        addSessionCookie(headers);
        return headers;
    }
	
	
	private void testCreateCollection() throws IOException {
		 Map<String,String> headers = login();
	     headers.put("Content-Type", "application/json;charset=UTF-8");
	     String uri = baseUri + "/rest/collection";
	     String payload = "{\"name\":\"testcase14\",\"description\":\"testcase14\",\"creation_date\":\"2019-05-05T16:05:10.442Z\",\"privateStatus\":true,\"versions\":[{\"number\":1,\"creationDate\":\""
	        		+ "2019-05-05T16:05:10.442Z\",\"comment\":\"Initial Version\",\"privateStatus\":true,\"projects\":[{\"project_id\":\"http://repo1.maven.org/maven2/:com.sun.jersey:jersey-bundle:1.12\",\"source\":\"Maven\"}]}]}";
	     
	  
	     HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	     Assert.assertEquals("", response.getContent());
	     Assert.assertEquals(NUM200, response.getResponseCode());
	        
	}
	
	private String getCollections( ) throws IOException, ParseException {
		 Map<String,String> headers = login();
		 headers.put("Content-Type", "application/json;charset=UTF-8");
		 String uri = baseUri + "/rest/collection?user="+USER;
		 String result = HttpUtils.get(uri, headers,charset);
		 Object obj = new JSONParser().parse(result);		 
		 org.json.simple.JSONArray ar = (org.json.simple.JSONArray) obj;
		 org.json.simple.JSONObject jo = (org.json.simple.JSONObject) ar.get(ar.size()-1);
		 System.out.println(jo.get("id").toString());
		 
		org.json.simple.JSONArray versions = (org.json.simple.JSONArray) jo.get("versions");
		org.json.simple.JSONObject version = (org.json.simple.JSONObject) versions.get(0);
		versionId = version.get("id").toString();
		org.json.simple.JSONArray projects = (org.json.simple.JSONArray) version.get("projects");
		org.json.simple.JSONObject project = (org.json.simple.JSONObject) projects.get(0);
		projectId = project.get("id").toString();
		System.out.println(projectId);
		return jo.get("id").toString();
		    
	}
	
	private void testmakeCollectionPublic() throws IOException, ParseException {
		 String id =  getCollections();
		 Map<String,String> headers = login();
	     headers.put("Content-Type", "application/json;charset=UTF-8");
	     String uri = baseUri + "/rest/collection";
	     String payload = "{\"name\":\"testcase14\",\"description\":\"testcase14\",\"id\":\""+id + 
                 "\",\"isActive\":true,\"creation_date\":\"2019-05-05T16:05:10.442Z\",\"user\":\"" + USER
                 + "\",\"privateStatus\":true,\"versions\":[{\"number\":1,\"creationDate\":\""
      		+ "2019-05-05T16:05:10.442Z\",\"comment\":\"Initial Version\",\"privateStatus\":false,\"collectionId\":\"" + id + "\",\"doi\":null,\"filtered\":false,\"frozen\":false,\"id\":\"" + versionId  
      		+ "\",\"projects\":[{\"id\":\""+projectId+"\",\"project_id\":\"http://repo1.maven.org/maven2/:com.sun.jersey:jersey-bundle:1.12\",\"version_id\":\"" +versionId+ "\",\"source\":\"Maven\"}]}]}";	     
	     HttpResponse response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	     Assert.assertEquals("", response.getContent());
	     Assert.assertEquals(NUM200, response.getResponseCode());
	     uri = baseUri + "/rest/version";
	    payload =  "{\n" + 
	    		"	  		  \"collectionId\": \""+id+ "\",\n" + 
	    		"			  \"comment\": \"Initial Version\",\n" + 
	    		"			  \"commits\": null,\n" + 
	    		"			  \"creationDate\": \"2019-05-05T16:05:10\",\n" + 
	    		"			  \"derivedFrom\": \""+ versionId + "\",\n" + 
	    		"			  \"doi\": null,\n" + 
	    		"			  \"filtered\": false,\n" + 
	    		"			  \"frozen\": false,\n" + 
	    		"			  \"id\": \""+versionId+"\",\n" + 
	    		"			  \"name\": \"initial version\",\n" + 
	    		"			  \"number\": 1,\n" + 
	    		"			  \"privateStatus\": false,\n" + 
	    		"			  \"projects\": [\n" + 
	    		"			    {\n" + 
	    		"			      \"id\": \""+projectId+"\",\n" + 
	    		"			      \"project_id\": \"http://repo1.maven.org/maven2/:com.sun.jersey:jersey-bundle:1.12\",\n" + 
	    		"			      \"source\": \"Maven\",\n" + 
	    		"			      \"version_id\": \"" +versionId+ "\"\n" + 
	    		"			    }\n" + 
	    		"			  ]\n" + 
	    		"		}	";	
	     response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	     //Assert.assertEquals("", response.getContent());
	     Assert.assertEquals(NUM200, response.getResponseCode());
	     
	     uri = baseUri + "/rest/publishCollection/";
	     response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	     Assert.assertEquals(NUM200, response.getResponseCode());
	    
	}
	
	
	private void testpinCollection() throws IOException, ParseException {
		 String id =  getCollections();
		Map<String,String> headers = login();
		headers.put("Content-Type", "application/json;charset=UTF-8");
		 String uri = baseUri + "/rest/pin";
		 String payload = "{\"type\":\"collection\",\"user\":\""+USER+"\",\"id\":\""+id+"\"}";
		 HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	     Assert.assertEquals("", response.getContent());
	     Assert.assertEquals(NUM200, response.getResponseCode());
		 
		 
		 
		
	}
	

	
	
	
}
