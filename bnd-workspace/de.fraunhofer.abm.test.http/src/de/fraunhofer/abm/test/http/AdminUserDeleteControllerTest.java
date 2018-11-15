package de.fraunhofer.abm.test.http;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
	
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
	import de.fraunhofer.abm.http.client.HttpUtils;
	public class AdminUserDeleteControllerTest extends AbstractHttpTest {
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
	        testAdminDeleteUser();
	    }	   
	   
	    
		protected Map<String, String> login() throws IOException {
	        login(USER, PASSWORD);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
	    
		private void testAdminDeleteUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String uri = baseUri + "/rest/username";
	        String payload = "{\"username\":\"demo2\",\"firstname\":\"demo2\",\"lastname\":\"demo22\",\"email\":\"demo2@gmail.com\",\"affiliation\":\"upb\",\"password\":\"K25eHhV5v/qByG8oTP2VySQ4iPv4ZPFr0Bkf3uTnTwA=$lSghSVxaYoDynPR7B3LChprsbvvYP2M8lEKI9SWH52g=\",\"token\":\"testtoken\"}";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());	        
	        
	        uri = baseUri + "/rest/adminDeleteUsers";
	        String [] deleteUserList = {"demo2"};
	        payload = new JSONObject().put("deleteUsers", deleteUserList.toString()).toString();
		    response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());	        
	    
	        
	        
		}
}