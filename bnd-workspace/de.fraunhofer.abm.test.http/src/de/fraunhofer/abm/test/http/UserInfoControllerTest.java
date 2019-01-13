package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
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
	        testUpdateUserInfoNull();
	        testRegisterUserNullInfo();
	        testApproveUser();
	        //testApprovedUserStatus();
	        // testRejectUser();
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
	 		String payload = "{\"username\":\"testUser1\", \"firstname\":\"myfirstname\", \"lastname\":\"mylastname\", " 
	 						+ "\"password\":\"testUser1\", \"email\":\"anut347@gmail.com\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testRegisterUserNullInfo() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser2\", " 
	 						+ "\"password\":\"testUser2\", \"email\":\"anut347@gmail.com\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testUpdateUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser1\", \"firstname\":\"mynewfirstname\", \"lastname\":\"mynewlastname\", " 
	 						+ "\"password\":\"testUser1\", \"email\":\"anut347@gmail.com\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testUpdateUserInfoNull() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser1\", " 
	 						+ "\"password\":\"testUser1\", \"email\":\"anut347@gmail.com\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testApproveUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String payload = "{\"isApprove\":\"true\", \"userList\":\"testUser1\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/approval";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testApprovedUserStatus() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        
	        String uri = baseUri + "/rest/username?username=" + "testUser1";
	        String result = HttpUtils.get(uri, headers, charset);
	        Assert.assertNotNull(result);
            JSONObject obj = new JSONObject(result);
            Assert.assertEquals(true, obj.get("approved"));
		}
		
		private void testRejectUser() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"isApprove\":\"false\", \"userList\":\"testUser2\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/approval";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
	} 