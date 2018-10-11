package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class DeleteCollectionTest extends AbstractHttpTest {
	public static final int NUM200 = 200;
	public static final int NUM403 = 403;
	public static final int NUM10000 = 10000;
		
	@Test
	public void deletePublicCollection() throws IOException {
        /*Map<String, String> headers = login();
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
        */
		//HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
        //Assert.assertEquals("", response.getContent());
        //Assert.assertEquals(NUM200, response.getResponseCode());

        // get collections
		Map<String, String> headers=new HashMap<>();
        String uri = baseUri + "/rest/deletecollection";
        //HttpResponse response = HttpUtils.post(uri, headers, charset);
        String payload = "{id:1234}";
        	       
        HttpResponse collections = HttpUtils.post(uri, headers,payload.getBytes(charset), charset);
        /*JSONArray array = new JSONArray(collections);
        Assert.assertEquals(1, array.length());
        JSONObject collection = (JSONObject) array.get(0);
        String id = collection.getString("id");

        // get collection by id
        uri = baseUri + "/rest/deletepubliccollection/" + id;
        String json = HttpUtils.get(uri, headers, charset);
        collection = new JSONObject(json);
        Assert.assertEquals(id, collection.getString("id"));
        Assert.assertEquals("slf4j", collection.getString("name"));
        Assert.assertEquals("Simple Logging Facade 4 Java", collection.getString("description"));

        // delete
        uri = baseUri + "/rest/deletepubliccollection/" + id;
        collections = HttpUtils.delete(uri, headers, charset);
        Assert.assertEquals(NUM200, collections.getResponseCode());*/
	}
}
