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

       
        String user = "demo"; String password = "demo";
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
        String user = "demo"; String password = "demo";
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

    //@Test
    public void testLastSuccessfullyBuiltVersion() throws IOException {
    	// Create a private collection first.
    	Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/collection";
        String payload = "{\"name\":\"SimpleMavenApp\",\"description\":\"UnitTest\",\"versions\":"
                + "[{\"number\":1,\"commits\":[{\"commitId\":\"HEAD\",\"repository\":{"
                + "\"ownerType\":\"Organization\","
                + "\"issuesUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/issues{/number}\","
                + "\"releasesUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/releases{/id}\","
                + "\"description\":\"For an introductory tutorial on how to use Jenkins to build a simple Java application with Maven.\","
                + "\"contributorsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/contributors\","
                + "\"isPrivate\":false,\"commitsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/commits{/sha}\","
                + "\"openIssues\":9,\"latestUpdate\":\"2018-05-21T09:01:28Z\",\"score\":188,\"starred\":24,"
                + "\"id\":\"a38166a0-0661-3f51-8085-f22c1b163f78\",\"forks\":2216,\"owner\":\"jenkins-docs\","
                + "\"hasWiki\":true,\"defaultBranch\":\"master\","
                + "\"htmlUrl\":\"https://github.com/jenkins-docs/simple-java-maven-app\","
                + "\"creationDate\":\"2017-09-26T02:35:06\","
                + "\"contentsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/contents/{+path}\","
                + "\"remoteId\":104826554,\"repositoryUrl\":\"https://github.com/jenkins-docs/simple-java-maven-app.git\","
                + "\"hasDownloads\":true,\"license\":\"\",\"watched\":24,\"size\":13,\"name\":\"simple-java-maven-app\","
                + "\"repositoryType\":\"git\",\"properties\":[]},\"branchId\":\"master\"}],\"comment\":\"Initial Version\","
                + "\"creationDate\":\"2018-05-28T13:15:19\"}],\"privateStatus\":true,"
                + "\"creation_date\":\"2018-05-28T13:15:19\",\"user\":\"demo\"}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
        Assert.assertEquals("", response.getContent());
        Assert.assertEquals(NUM200, response.getResponseCode());

        // Get the collection from the server.
        uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        JSONArray array = new JSONArray(collections);

        // Get version id of the version that has to be built.
        JSONObject collection = null;
        for (int i = 0; i < array.length(); i++) {
            collection = (JSONObject)array.get(i);

            if (collection.getString("name").equals("SimpleMavenApp")) {
                break;
            }
        }
        String collectionid = collection.getString("id");
        String name = collection.getString("name");

        Assert.assertEquals("SimpleMavenApp", name);
        Assert.assertEquals(1,collection.getJSONArray("versions").length());
        JSONArray versions = collection.getJSONArray("versions");
        Assert.assertEquals(1, ((JSONObject)versions.get(0)).getJSONArray("commits").length());

        JSONObject commit = (JSONObject)((JSONObject)versions.get(0)).getJSONArray("commits").get(0);
        String versionid = commit.getString("versionId");
        String commitid = commit.getString("id");

        // Start the build.
        uri = baseUri + "/rest/build";
        payload = "{\"id\": \"" + versionid + "\", \"number\":1,\"commits\":[{\"versionId\":\"" + versionid + "\","
                + "\"commitId\":\"HEAD\",\"id\":\"" + commitid + "\",\"creationDate\":null,\"message\":null,"
                + "\"repository\":{\"ownerType\":\"Organization\","
                + "\"issuesUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/issues{/number}\","
                + "\"releasesUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/releases{/id}\","
                + "\"description\":\"For an introductory tutorial on how to use Jenkins to build a simple Java application with Maven.\","
                + "\"contributorsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/contributors\","
                + "\"isPrivate\":false,\"commitsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/commits{/sha}\","
                + "\"openIssues\":9,\"latestUpdate\":\"2018-05-21T09:01:28Z\",\"score\":188,"
                + "\"starred\":24,\"id\":\"a38166a0-0661-3f51-8085-f22c1b163f78\","
                + "\"forks\":2216,\"owner\":\"jenkins-docs\",\"hasWiki\":true,"
                + "\"defaultBranch\":\"master\",\"htmlUrl\":\"https://github.com/jenkins-docs/simple-java-maven-app\","
                + "\"creationDate\":\"2017-09-26T02:35:06\","
                + "\"contentsUrl\":\"https://api.github.com/repos/jenkins-docs/simple-java-maven-app/contents/{+path}\","
                + "\"remoteId\":104826554,\"repositoryUrl\":\"https://github.com/jenkins-docs/simple-java-maven-app.git\","
                + "\"hasDownloads\":true,\"license\":\"\",\"watched\":24,\"size\":13,"
                + "\"name\":\"simple-java-maven-app\",\"repositoryType\":\"git\","
                + "\"properties\":[]}}],\"filtered\":false,\"frozen\":false,\"comment\":\"Initial Version\","
                + "\"creationDate\":\"2018-05-28T13:15:19\",\"collectionId\":\"" + collectionid + "\"}";
        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
        Assert.assertEquals(NUM200, response.getResponseCode());

        // Get the BuildDTO.
        uri = baseUri + "/rest/builds/" + USER;

        float buildProgress = 0F;
        while (buildProgress < 1F) {
            String json = HttpUtils.get(uri, headers, charset);
            JSONArray builds = new JSONArray(json);

            if (builds.length() > 0) {
                try {
            	    System.out.println("Waiting for build...");
            	    Thread.sleep(NUM10000);
                } catch (Exception x) {
                    // Do nothing on interrupted Exception.
                }
            } else {
                buildProgress = 1F;
            }
        }
        System.out.println("Finished building");

        // Final testing of lastsuccessfullybuiltversion.
        uri = baseUri + "/rest/lastsuccessfullybuiltversion/" + collectionid;
        String json = HttpUtils.get(uri, headers, charset);
        
        //if build failed 
        Assert.assertEquals(true, json.isEmpty());
        //if build passed 
         if(!json.isEmpty()) {
        JSONObject resultVersion = new JSONObject(json);
        Assert.assertNotEquals(null, resultVersion);
        Assert.assertEquals("Initial Version", resultVersion.getString("comment"));
        Assert.assertEquals(1, resultVersion.getInt("number"));
        }
        System.out.println("Test passed. Unfreezing..");

        // Unfreeze version so it can be deleted.
        uri = baseUri + "/rest/version";
        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
        Assert.assertEquals(NUM200, response.getResponseCode());

        System.out.println("Deleting..");

        // Delete all mocked data.
        uri = baseUri + "/rest/collection/" + collectionid;
        response = HttpUtils.delete(uri, headers, charset);
        Assert.assertEquals(NUM200, response.getResponseCode());
    }
}