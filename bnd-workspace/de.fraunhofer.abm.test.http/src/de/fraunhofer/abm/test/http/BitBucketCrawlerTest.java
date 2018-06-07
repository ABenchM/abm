package de.fraunhofer.abm.test.http;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import de.fraunhofer.abm.http.client.HttpUtils;

public class BitBucketCrawlerTest extends AbstractHttpTest {

	@Test

	public void testSearch() throws Exception {
	
		String uri = baseUri + "/rest/search/" + QUERY;
		Map<String, String> headers = login();
		String repo = HttpUtils.get(uri, headers, charset); 
		JSONArray array = new JSONArray(repo);
		Assert.assertNotNull(array);
		Assert.assertFalse(array.length() <=1 );

	}

}