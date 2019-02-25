package de.fraunhofer.abm.test.http;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
	
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import de.fraunhofer.abm.http.client.HttpResponse;
	import de.fraunhofer.abm.http.client.HttpUtils;
	public class GetUsersControllerTest extends AbstractHttpTest {
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
	        testNonApprovedUsers();
	        testApprovedUsers();
	    }	   
	    
		protected Map<String, String> login() throws IOException {
	        login(USER, PASSWORD);
	        Map<String, String> headers = HttpUtils.createFirefoxHeader();
	        addSessionCookie(headers);
	        return headers;
	    }
	    
		private void testApprovedUsers() throws IOException {
			//HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String payload = "{\"approved\":1}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/userList?username="+USER+"&approved=1";
	        String result = HttpUtils.get(uri, headers, charset);
	        JSONArray array = new JSONArray(result);
	        if (array.length()>0) {
		        assertEquals(true, array.getJSONObject(0).get("approved"));				
			}
		}
		
		private void testNonApprovedUsers() throws IOException {
			//HttpResponse response;
			Map<String, String> headers = login();
	        headers.put("Content-Type", "application/json;charset=UTF-8");
	        String payload = "{\"approved\":0, \"username\":\""+USER+"\"}";
	        headers.put("params", payload);
	        String uri = baseUri + "/rest/userList?username="+USER+"&approved=0";
	        String result = HttpUtils.get(uri, headers, charset);
	        JSONArray array = new JSONArray(result);
	        if (array.length()>0) {
		        assertEquals(false, array.getJSONObject(0).get("approved"));				
			}
		}
		
	} 