package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class LoginTest extends AbstractHttpTest {

    @Test
    public void testLoginLogout() throws IOException {

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
        uri = baseUri + "/rest/collection?user=" + USER;
        String collections = HttpUtils.get(uri, headers, charset);
        Assert.assertNotNull(collections);
        Assert.assertFalse(collections.isEmpty());

        // logout
        HttpUtils.get(baseUri + "/rest/logout", headers, charset);

        // try again to get a secured resource, should fail now
        try {
            collections = HttpUtils.get(uri, headers, charset);
            Assert.assertNull(collections);
        } catch (IOException e) {
            // expected 401
            Assert.assertNotNull(e);
        }
    }
}
