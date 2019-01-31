package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpUtils;

public class DelphiControllerTest extends AbstractHttpTest {
    
    @Test
    public void testGetDelphiFeatures() throws IOException{
    	Map<String, String> headers = new HashMap<>();
    	String uri = baseUri + "/rest/delphifeatures";
    	headers.put("Content-Type", "application/json;charset=UTF-8");
    	HttpUtils.get(uri, headers, charset);
    }
    
    @Test
    public void testSearchProjects() throws IOException{
    	Map<String, String> headers = new HashMap<>();
    	String uri = baseUri + "/rest/searchquery";
    	String body = "{\"query\":\"[using KeyStore]>10\"}";
    	headers.put("Content-Type", "application/json;charset=UTF-8");
    	HttpUtils.post(uri, headers, body.getBytes(), charset);
    }
    
    @Test
    public void testAddProject() throws IOException{
    	Map<String, String> headers = new HashMap<>();
    	headers.put("Content-Type", "application/json;charset=UTF-8");
    	String uri = baseUri + "/rest/addprojects";
    	String body = "{\"version_id\": \"fdaa77ff-8b2d-4f3e-958c-413eb6d62894\" , \"project_id\": \"1\", \"source\": \"Maven\"}";
        HttpUtils.post(uri, headers, body.getBytes(charset), charset);
    }
    
    @Test
    public void testRemoveProject() throws IOException{
    	Map<String, String> headers = new HashMap<>();
    	headers.put("Content-Type", "application/json;charset=UTF-8");
    	String uri = baseUri + "/rest/removeproject/"+"1703e7af-26df-47ae-b451-4d26558cb340";
        HttpUtils.delete(uri, headers, charset);
    }
}
