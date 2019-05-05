package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class CollectionControllerTest extends AbstractHttpTest {

	public static final int NUM200 = 200;
	public static final int NUM403 = 403;
	public static final int NUM401 = 401;
	public static final int NUM10000 = 10000;

    @Test(expected=IOException.class) // 401 unauthorized
    public void testCollectionsByUserAuth() throws IOException {
        String uri = baseUri + "/rest/collection?user=" + USER;
        HttpUtils.get(uri, null, charset);
    }

    @Test
    public void testEmptyCollectionsByUser() throws IOException {
        Map<String, String> headers = login();
        String uri = baseUri + "/rest/collection?user=" + USER;
        String response = HttpUtils.get(uri, headers, charset);
        Assert.assertEquals("[]", response);
    }

    @Test(expected=IOException.class) // 401 unauthorized
    public void testPostAndDeleteCollectionAuth() throws IOException {
        Map<String,String> headers = HttpUtils.createFirefoxHeader();
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
        HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
    }

    @Test
    public void testPostAndDeleteCollection() throws IOException {
        // create
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
        		+ "https://api.github.com/repos/qos-ch/slf4j/issues{/number}\",\"latestUpdate\":\"2017-02-21T13:56:39Z\",\"license\":\"\",\""
        		+ "name\":\"slf4j\",\"openIssues\":62,\"owner\":\"qos-ch\",\"ownerType\":\"Organization\",\"properties\":[],\"releasesUrl\":\""
        		+ "https://api.github.com/repos/qos-ch/slf4j/releases{/id}\",\"remoteId\":283187,\"repositoryType\":\"git\",\"repositoryUrl\":\""
        		+ "https://github.com/qos-ch/slf4j.git\",\"score\":126,\"size\":9915,\"starred\":637,\"watched\":637},\"branchId\":\"master\"}]}]}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
        Assert.assertEquals("", response.getContent());
        Assert.assertEquals(NUM200, response.getResponseCode());
        
        // get collections
        uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        JSONArray array = new JSONArray(collections);

        Assert.assertTrue(array.length() >= 1);
//        Assert.assertEquals(1, array.length());
        JSONObject collection = (JSONObject) array.get(0);
        String id = collection.getString("id");

       
        String user = "test1"; String password = "test1";
        login(user, password);
        uri = baseUri + "/rest/collection/" + id;
        addSessionCookie(headers);
        try {
            response = HttpUtils.delete(uri, headers, charset);
            Assert.assertEquals(NUM200, response.getResponseCode());
        } catch(IOException e) {
            // expected 403 unauthorized
            Assert.assertNotNull(e);
        }

        // get collections
        System.out.println(id);
        uri = baseUri + "/rest/collection/" + id;
        collections = HttpUtils.get(uri, headers, charset);
        Assert.assertEquals("", collections);
    }

    @Test
    public void testPutCollection() throws IOException {
        // create
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

        // get collections
        uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        JSONArray array = new JSONArray(collections);
        Assert.assertTrue(array.length()>=1);
        JSONObject collection = (JSONObject) array.get(0);
        String id = collection.getString("id");
        String name = collection.getString("name");
        String description = collection.getString("description");
        Assert.assertEquals("slf4j", name);
        Assert.assertEquals("Simple Logging Facade 4 Java", description);

        // update with wrong user
        String user = "test1"; String password = "test1";
        login(user, password);
        uri = baseUri + "/rest/collection";
        addSessionCookie(headers);
        try {
            collection.put("name", "abc");
            collection.put("description", "def");
            payload = collection.toString();
            response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
            Assert.assertEquals(NUM200, response.getResponseCode());
        } catch(IOException e) {
            // expected 403 unauthorized
            Assert.assertNotNull(e);
        }

        // update
        login();
        addSessionCookie(headers);
        collection.put("name", "abc");
        collection.put("description", "def");
        payload = collection.toString();
        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);

        // get collection by id
        uri = baseUri + "/rest/collection/" + id;
        String json = HttpUtils.get(uri, headers, charset);
        collection = new JSONObject(json);
        Assert.assertEquals(id, collection.getString("id"));
        Assert.assertEquals("abc", collection.getString("name"));
        Assert.assertEquals("def", collection.getString("description"));

        // get version details - test case for testGetVersionDetails
        JSONArray versionAray = collection.getJSONArray("versions");
        JSONObject versionDetails = (JSONObject) versionAray.get(0);
        String versionId = versionDetails.getString("id");
        uri = baseUri + "/rest/versionDetails/" + versionId;
        String result = HttpUtils.get(uri, headers, charset);
        JSONObject obj = new JSONObject(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(id, obj.getString("id"));
        JSONArray newversionAray = collection.getJSONArray("versions");
        Assert.assertEquals(1, newversionAray.length());
        Assert.assertEquals("abc", obj.getString("name"));
        Assert.assertEquals("def", obj.getString("description"));

        // delete
        uri = baseUri + "/rest/collection/" + id;
        response = HttpUtils.delete(uri, headers, charset);
        Assert.assertEquals(NUM200, response.getResponseCode());
    }

   @Test
    public void testCollectionById() throws IOException {
        // create
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

        // get collections
        uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        JSONArray array = new JSONArray(collections);
        Assert.assertEquals(1, array.length());
        JSONObject collection = (JSONObject) array.get(0);
        String id = collection.getString("id");

        // get collection by id
        uri = baseUri + "/rest/collection/" + id;
        String json = HttpUtils.get(uri, headers, charset);
        collection = new JSONObject(json);
        Assert.assertEquals(id, collection.getString("id"));
        Assert.assertEquals("slf4j", collection.getString("name"));
        Assert.assertEquals("Simple Logging Facade 4 Java", collection.getString("description"));

        // delete
        uri = baseUri + "/rest/collection/" + id;
        response = HttpUtils.delete(uri, headers, charset);
        Assert.assertEquals(NUM200, response.getResponseCode());
    }

  
    
}