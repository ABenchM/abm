package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class CommitControllerTest extends AbstractHttpTest {

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostCommitShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/commit";
        HttpUtils.post(uri, null, new byte[0], charset);
    }

    @Test(expected=IOException.class) // 400 bad request
    public void postCommitWithEmptyIdArrayShouldThrowException() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);
        String uri = baseUri + "/rest/commit";
        String payload = "{\"action\":\"delete_multi\",\"ids\":[]}";
        HttpUtils.post(uri, headers, payload.getBytes(), charset);
    }

    @Test
    public void deletingMultipleCommitsShouldWork() throws IOException {
        Map<String, String> headers = createCollection();

        // get the collection to determine the commit id
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);
        JSONObject commit = version.getJSONArray("commits").getJSONObject(0);

        // delete non-existing commit
        String uri = baseUri + "/rest/commit";
        String payload = "{\"action\":\"delete_multi\",\"ids\":[\"dummy\"]}";
        try {
            HttpUtils.post(uri, headers, payload.getBytes(), charset);
            Assert.fail("Dummy commit should not exist");
        } catch (Exception e) {
            // expected ScopedWorkException(NoResultException)
        }

        // delete the commit
        uri = baseUri + "/rest/commit";
        payload = "{\"action\":\"delete_multi\",\"ids\":[\"" + commit.getString("id") + "\"]}";
        HttpResponse response = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, response.getResponseCode());

        // cleanup: delete the collection
        deleteCollection(headers, collection.getString("id"));
    }

    @Test(expected=IOException.class) // 400 bad request
    public void unknownPostActionShouldThrowException() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        // delete non-existing commit
        String uri = baseUri + "/rest/commit";
        String payload = "{\"action\":\"unknown_action\",\"ids\":[]}";
        HttpUtils.post(uri, headers, payload.getBytes(), charset);
    }

    @Test(expected=IOException.class)
    public void unauthorizedDeleteCommitShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/commit/dummyId";
        HttpUtils.delete(uri, null, charset);
    }

    @Test
    public void authorizedDeleteCommitShouldWork() throws IOException {
        Map<String, String> headers = createCollection();

        // get the collection to determine the commit id
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);
        JSONObject commit = version.getJSONArray("commits").getJSONObject(0);

        String uri = baseUri + "/rest/commit/"+commit.getString("id");
        HttpUtils.delete(uri, headers, charset);

        deleteCollection(headers, collection.getString("id"));
    }

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPutCommitShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/commit";
        HttpUtils.put(uri, null, new byte[0], charset);
    }

    @Test
    public void authorizedPutCommitShouldWork() throws IOException {
        Map<String, String> headers = createCollection();

        // get the collection to determine the commit id
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);
        JSONObject commit = version.getJSONArray("commits").getJSONObject(0);

        String uri = baseUri + "/rest/commit";
        commit.put("commitId", "asdf");
        HttpResponse response = HttpUtils.put(uri, headers, commit.toString().getBytes(), charset);
        Assert.assertEquals("", response.getContent());
        Assert.assertEquals(200, response.getResponseCode());

        deleteCollection(headers, collection.getString("id"));
    }
}
