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
	        testUpdateUserDemo();
	        testRegisterUser1();
	        testApproveUser1();
	        testUpdateUser1InfoNull();
	        testDeleteUser1();
	        testRegisterUser2NullInfo();
	        testRejectUser2();
	        testRegisterUser3();
	        testApproveUser3();
	        //testApprovedUser3Status();
	        
	        testAdminDeleteUser3();
	    }	   
	    
		protected Map<String, String> login() throws IOException {
	        login(USER, PASSWORD);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
		
		protected Map<String, String> loginTestUser(String testUser) throws IOException {
	        login(testUser, testUser);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
	    
		private void testUpdateUserDemo() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\""+USER+"\", \"firstname\":\"mynewfirstname\", \"lastname\":\"mylastname\", " 
					+ "\"password\":null, \"email\":\"demo@gmail.com\", \"affiliation\":\"Uni Paderborn\", \"locked\":\"0\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
	        
	        uri = baseUri + "/rest/username?username=" + USER;
	        String result = HttpUtils.get(uri, headers, charset);
	        System.out.println(result);
	        Assert.assertNotNull(result);
            JSONObject obj = new JSONObject(result);
		    Assert.assertEquals("mynewfirstname", obj.get("firstname"));
		}
		
		private void testRegisterUser1() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser1\", \"firstname\":\"myfirstname\", \"lastname\":\"mylastname\", " 
	 						+ "\"password\":\"testUser1\", \"email\":\"thottam@mail.uni-paderborn.de\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testApproveUser1() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String payload = "{\"isApprove\":\"true\", \"userList\":\"testUser1\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/approval";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testUpdateUser1InfoNull() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser1\", " 
	 						+ "\"password\":\"testUser1\", \"email\":\"thottam@mail.uni-paderborn.de\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testDeleteUser1() throws IOException {
			HttpResponse response;
			Map<String, String> headers = loginTestUser("testUser1");
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "testUser1";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username/testUser1";
	        response = HttpUtils.delete(uri, headers, charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testRegisterUser2NullInfo() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testUser2\", " 
	 						+ "\"password\":\"testUser2\", \"email\":\"thottam@mail.uni-paderborn.de\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testRejectUser2() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"isApprove\":\"false\", \"userList\":\"testUser2\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/approval";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testRegisterUser3() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "{\"username\":\"testApproveUser1\", \"firstname\":\"myfirstname\", \"lastname\":\"mylastname\", " 
	 						+ "\"password\":\"testApproveUser1\", \"email\":\"thottam@mail.uni-paderborn.de\", \"affiliation\":\"Uni Paderborn\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/username";
	        response = HttpUtils.post(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testApproveUser3() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String payload = "{\"isApprove\":\"true\", \"userList\":\"testApproveUser1\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/approval";
	        response = HttpUtils.put(uri, headers, payload.getBytes(charset), charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testAdminDeleteUser3() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	 		String payload = "testApproveUser1";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/adminDeleteUsers/testApproveUser1";
	        response = HttpUtils.delete(uri, headers, charset);
	        Assert.assertEquals(NUM200, response.getResponseCode());
		}
		
		private void testApprovedUser3Status() throws IOException {
			HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        
	        String uri = baseUri + "/rest/username?username=" + "testUser1";
	        String result = HttpUtils.get(uri, headers, charset);
	        Assert.assertNotNull(result);
            JSONObject obj = new JSONObject(result);
            Assert.assertEquals(true, obj.get("approved"));
		}
		
	} 
