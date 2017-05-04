package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public abstract class AbstractHttpTest implements TestParameters {

    public String charset = "UTF-8";
    protected String baseUri;
    private String sessionCookie;

    public AbstractHttpTest() {
        this.baseUri = BASE_URI;
        HttpUtils.useCache(false);
    }

    public void login(String user, String password) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/login";
        String payload = "{\"username\": \""+user+"\", \"password\": \""+password+"\"}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        if(response.getResponseCode() == 200) {
            sessionCookie = HttpUtils.getHeaderField(response.getHeader(), "Set-Cookie");
        }
    }

    public void addSessionCookie(Map<String, String> headers) {
        headers.put("Cookie", sessionCookie);
    }

    protected Map<String, String> login() throws IOException {
        login(USER, PASSWORD);
        Map<String, String> headers = HttpUtils.createFirefoxHeader();
        addSessionCookie(headers);
        return headers;
    }

    protected Map<String, String> createCollection() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/collection";
        String payload = "{\"name\":\"slf4j\",\"description\":\"Simple Logging Facade 4 Java\",\"versions\":[{\"number\":1,\"creationDate\":\"2017-02-22T14:45:04.028Z\",\"comment\":\"Initial Version\",\"commits\":[{\"commitId\":\"HEAD\",\"repository\":{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/qos-ch/slf4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/qos-ch/slf4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/qos-ch/slf4j/contributors\",\"creationDate\":\"2009-08-20T16:25:49\",\"defaultBranch\":\"master\",\"description\":\"Simple Logging Facade for Java\",\"forks\":347,\"hasDownloads\":false,\"hasWiki\":false,\"htmlUrl\":\"https://github.com/qos-ch/slf4j\",\"id\":\"6b945e0c-b859-377c-b611-bcc7aa584272\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/qos-ch/slf4j/issues{/number}\",\"latestUpdate\":\"2017-02-21T13:56:39Z\",\"license\":\"\",\"name\":\"slf4j\",\"openIssues\":62,\"owner\":\"qos-ch\",\"ownerType\":\"Organization\",\"properties\":[],\"releasesUrl\":\"https://api.github.com/repos/qos-ch/slf4j/releases{/id}\",\"remoteId\":283187,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/qos-ch/slf4j.git\",\"score\":126,\"size\":9915,\"starred\":637,\"watched\":637},\"branchId\":\"master\"}]}]}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
        Assert.assertEquals("", response.getContent());
        Assert.assertEquals(200, response.getResponseCode());
        return headers;
    }

    protected JSONObject getCollection(Map<String, String> headers) throws IOException {
        String uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        JSONArray array = new JSONArray(collections);
        Assert.assertEquals(1, array.length());
        return (JSONObject) array.get(0);
    }

    protected void deleteCollection(Map<String, String> headers, String id) throws IOException {
        String uri = baseUri + "/rest/collection/" + id;
        HttpResponse response = HttpUtils.delete(uri, headers, charset);
        Assert.assertEquals(200, response.getResponseCode());
    }
}
