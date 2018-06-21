package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

// TODO write test for unfreeze ?!?
public class VersionControllerTest extends AbstractHttpTest {

	public static final int NUM403 = 403;
	public static final int NUM123 = 123;

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostVersionShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/version";
        HttpUtils.post(uri, null, "{}".getBytes(), charset);
    }

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPutVersionShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/version";
        HttpUtils.put(uri, null, "{}".getBytes(), charset);
    }

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedDeleteVersionShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/version";
        HttpUtils.delete(uri, null, charset);
    }

    @Test
    public void deriveShouldReturnNewVersion() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        // derive with wrong user
        String user = "demo"; String password = "demo";
        login(user, password);
        String uri = baseUri + "/rest/version/derive";
        addSessionCookie(headers);
        try {
            HttpResponse response = HttpUtils.post(uri, headers, version.toString().getBytes(), charset);
            Assert.assertEquals(NUM403, response.getResponseCode());
        } catch(IOException e) {
            // expected 403 unauthorized
            Assert.assertNotNull(e);
        }

        // login with the correct user again
        login();
        addSessionCookie(headers);

        // derive the version
        HttpResponse resp = HttpUtils.post(uri, headers, version.toString().getBytes(), charset);
        JSONObject derivedVersion = new JSONObject(resp.getContent());
        Assert.assertEquals(2, derivedVersion.getInt("number"));

        // clean up, delete the collection again
        deleteCollection(headers, collection.getString("id"));
    }

    @Test(expected=IOException.class) // 400 bad request - action unknown
    public void unknownActionShouldThrowException() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String uri = baseUri + "/rest/version/foobar";
        HttpUtils.post(uri, headers, "{}".getBytes(), charset);
    }

    @Test(expected=IOException.class) // 400 bad request - id is missing
    public void invalidVersionShouldThrowException() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String uri = baseUri + "/rest/version/derive";
        HttpUtils.post(uri, headers, "{}".getBytes(), charset);
    }

    @Test(expected=IOException.class) // 400 bad request - collection not found
    public void nonExistingCollectionShouldThrowException() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        version.put("collectionId", "does not exist");
        String uri = baseUri + "/rest/version/derive";
        try {
            HttpUtils.post(uri, headers, version.toString().getBytes(), charset);
        } finally {
            // clean up, delete the collection again
            deleteCollection(headers, collection.getString("id"));
        }
    }

    @Test
    public void putVersionShouldSaveAndReturnVersion() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        // change the version
        version.put("number", NUM123);
        version.put("comment", "asdf");

        // put with wrong user
        String user = "demo"; String password = "demo";
        login(user, password);
        String uri = baseUri + "/rest/version";
        addSessionCookie(headers);
        try {
            HttpResponse response = HttpUtils.put(uri, headers, version.toString().getBytes(), charset);
            Assert.assertEquals(NUM403, response.getResponseCode());
        } catch(IOException e) {
            // expected 403 unauthorized
            Assert.assertNotNull(e);
        }

        // login with the correct user again
        login();
        addSessionCookie(headers);

        // update the version
        HttpUtils.put(uri, headers, version.toString().getBytes(), charset);

        // get the collection again and check, that the version is changed
        collection = getCollection(headers);
        version = collection.getJSONArray("versions").getJSONObject(0);
        Assert.assertEquals(NUM123, version.getInt("number"));
        Assert.assertEquals("asdf", version.getString("comment"));

        // clean up, delete the collection again
        deleteCollection(headers, collection.getString("id"));
    }

    @Test(expected=IOException.class)
    public void deleteVersionShouldNotDeleteSolitaryVersion() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        // delete the version
        String uri = baseUri + "/rest/version/" + version.getString("id");
        try {
            HttpUtils.delete(uri, headers, charset);
        } finally {
            // clean up, delete the collection again
            deleteCollection(headers, collection.getString("id"));
        }
    }

    @Test
    public void deleteVersionShouldWork() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        try {
            // derive a new version, so that we have 2 and can delete one of them
            String uri = baseUri + "/rest/version/derive";
            HttpResponse response = HttpUtils.post(uri, headers, version.toString().getBytes(), charset);
            JSONObject derivedVersion = new JSONObject(response.getContent());
            Assert.assertEquals(2, derivedVersion.getInt("number"));

            // delete the original version
            uri = baseUri + "/rest/version/" + version.getString("id");
            HttpUtils.delete(uri, headers, charset);

            // get the collection again, and check that the only version is the derived version
            collection = getCollection(headers);
            version = collection.getJSONArray("versions").getJSONObject(0);
            Assert.assertEquals(2, version.getInt("number"));
        } finally {
            // clean up, delete the collection again
            deleteCollection(headers, collection.getString("id"));
        }
    }

    @Test(expected=IOException.class) // 403 wrong user
    public void deleteShouldOnlyBeAllowedForTheOwner() throws IOException {
        // create a collection to work with
        Map<String, String> headers = createCollection();

        // get the collection and the version
        JSONObject collection = getCollection(headers);
        JSONObject version = collection.getJSONArray("versions").getJSONObject(0);

        try {
            // derive a new version, so that we have 2 and can delete one of them
            String uri = baseUri + "/rest/version/derive";
            HttpResponse response = HttpUtils.post(uri, headers, version.toString().getBytes(), charset);
            JSONObject derivedVersion = new JSONObject(response.getContent());
            Assert.assertEquals(2, derivedVersion.getInt("number"));

            // login with wrong user
            login("demo", "demo");
            addSessionCookie(headers);

            // delete the original version
            uri = baseUri + "/rest/version/" + version.getString("id");
            HttpUtils.delete(uri, headers, charset);
        } finally {
            login();
            addSessionCookie(headers);
            // clean up, delete the collection again
            deleteCollection(headers, collection.getString("id"));
        }
    }
}
