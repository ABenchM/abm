package de.fraunhofer.abm.test.http;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
	
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.Base64.InputStream;
import de.fraunhofer.abm.http.client.HttpResponse;
	import de.fraunhofer.abm.http.client.HttpUtils;
	public class ActivateCollectionControllerTest extends AbstractHttpTest {
	    @Test
	    public void testCollectionstatus() throws IOException {
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
	        String collectionId = testPutCollection();
	        testActiveCollection(collectionId);
	        HttpUtils.get(baseUri + "/rest/logout", headers, charset);
	        
	    }	   
	    private void testActiveCollection(String collectionId) throws IOException {
	    	Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
			String jsonString = new JSONObject().put("collectionid", collectionId).toString();
			
			String payload = jsonString;
			String uri = baseUri + "/rest/collectionstatus/";
			HttpResponse collections = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
			Assert.assertEquals(NUM200, collections.getResponseCode());
	        System.out.println(collections.getResponseCode());
			
		}
	   
		private String testGetCollection(String uri, Map<String, String> headers, String charset) throws IOException {
	    	uri = baseUri + "/rest/collection?user=" + USER;
	        String collections = HttpUtils.get(uri, headers, charset); 
	        System.out.println(collections);
	        JSONArray array = new JSONArray(collections);
	        //Assert.assertEquals(1, array.length());
	        JSONObject collection = (JSONObject) array.get(0);
	        String id = collection.getString("id");
	        String name = collection.getString("name");
	        String description = collection.getString("description");
	        collection.put("isActive", 1);
	        Assert.assertEquals("slf4j", name);
	        Assert.assertEquals("Simple Logging Facade 4 Java", description);
	        return id;
		}
		protected Map<String, String> login() throws IOException {
	        login(USER, PASSWORD);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
	    
		private String testPutCollection() throws IOException {
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String uri = baseUri + "/rest/collection";
	        String payload = "{\"name\":\"slf4j\",\"description\":\"Simple Logging Facade 4 Java\",\"versions\":[{\"number\":1,\"creationDate\":\""
	        		+ "2017-02-22T14:45:04.028Z\",\"comment\":\"Initial Version\",\"commits\":[{\"commitId\":\"HEAD\",\"repository\":{\"commits\":null,\""
	        		+ "commitsUrl\":\"https://api.github.com/repos/qos-ch/slf4j/commits{/sha}\",\"contentsUrl\":\""
	        		+ "https://api.github.com/repos/qos-ch/slf4j/contents/{+path}\",\"contributorsUrl\":\""
	        		+ "https://api.github.com/repos/qos-ch/slf4j/contributors\",\"creationDate\":\"2009-08-20T16:25:49\",\"defaultBranch\":\"master\",\""
	        		+ "description\":\"Simple Logging Facade for Java\",\"forks\":347,\"hasDownloads\":false,\"hasWiki\":false,\"htmlUrl\":\""
	        		+ "https://github.com/qos-ch/slf4j\",\"id\":\"6b945e0c-b859-377c-b611-bcc7aa584272\",\"isPrivate\":true,\"issuesUrl\":\""
	        		+ "https://api.github.com/repos/qos-ch/slf4j/issues{/number}\",\"latestUpdate\":\"2017-02-21T13:56:39Z\",\"license\":\"\",\"name\":\""
	        		+ "slf4j\",\"openIssues\":62,\"owner\":\"qos-ch\",\"ownerType\":\"Organization\",\"properties\":[],\"releasesUrl\":\""
	        		+ "https://api.github.com/repos/qos-ch/slf4j/releases{/id}\",\"remoteId\":283187,\"repositoryType\":\"git\",\"repositoryUrl\":\""
	        		+ "https://github.com/qos-ch/slf4j.git\",\"score\":126,\"size\":9915,\"starred\":637,\"watched\":637},\"branchId\":\"master\"}]}]}";
	        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals("", response.getContent());
	        Assert.assertEquals(NUM200, response.getResponseCode());
	        String id = testGetCollection(uri, headers, charset);
	    	return id;
		}
	}