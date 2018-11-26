package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
	
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
	import de.fraunhofer.abm.http.client.HttpUtils;
	public class UserInfoControllerTest extends AbstractHttpTest {
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
	        testRegisterUser();
	        testUpdateUser();
	    }	   
	    
		protected Map<String, String> login() throws IOException {
	        login(USER, PASSWORD);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
	    
		private void testRegisterUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser\", \"firstname\":\"myfirstname\", \"lastname\":\"mylastname\", " 
	 						+ "\"password\":\"testUser\", \"email\":\"anut347@gmail.com\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testUpdateUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser\", \"firstname\":\"mynewfirstname\", \"lastname\":\"mynewlastname\", " 
	 						+ "\"password\":\"testUser\", \"email\":\"anut347@gmail.com\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
	} 