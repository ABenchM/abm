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

	import de.fraunhofer.abm.http.client.HttpResponse;
	import de.fraunhofer.abm.http.client.HttpUtils;

	public class DeletePublicCollectionControllerTest extends AbstractHttpTest {

	    @Test
	    public void testDeletePublicCollection() throws IOException {

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
	        System.out.println(collectionId);
	        testDeleteCollection(collectionId);
	        testInvalidDeleteCollection(collectionId);
	        HttpUtils.get(baseUri + "/rest/logout", headers, charset);
	        
	        //Assert.assertNull(collections);
	    }
	   
	    private void testDeleteCollection(String collectionId) throws IOException {
	    	Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
			String jsonString = new JSONObject().put("deleteCollections", collectionId).toString();
			
			String payload = jsonString;
			String uri = baseUri + "/rest/deletepubliccollection/";
	        HttpResponse collections = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
			Assert.assertEquals(NUM200, collections.getResponseCode());
	        System.out.println(collections.getResponseCode());
			
		}
	    
	    private void testInvalidDeleteCollection(String collectionId) throws IOException {
	    	Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");

	        String jsonString = new JSONObject().put("deleteCollections", collectionId+"1").toString();
			
			String payload = jsonString;
			String uri = baseUri + "/rest/deletepubliccollection/";
	        HttpResponse collections = null;
			try {
				collections = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
	        System.out.println(collections.getResponseCode());	
		}

		private String testGetCollection(String uri, Map<String, String> headers, String charset) throws IOException {
	    	uri = baseUri + "/rest/collection?user=" + USER;
	        String collections = HttpUtils.get(uri, headers, charset);
	        JSONArray array = new JSONArray(collections);
	        //Assert.assertEquals(1, array.length());
	        JSONObject collection = (JSONObject) array.get(0);
	        String id = collection.getString("id");
	        String name = collection.getString("name");
	        String description = collection.getString("description");
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
	        		+ "https://github.com/qos-ch/slf4j\",\"id\":\"6b945e0c-b859-377c-b611-bcc7aa584272\",\"isPrivate\":false,\"issuesUrl\":\""
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
