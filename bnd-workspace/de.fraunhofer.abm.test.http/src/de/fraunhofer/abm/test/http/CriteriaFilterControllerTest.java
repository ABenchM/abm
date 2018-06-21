package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class CriteriaFilterControllerTest extends AbstractHttpTest {


    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostCommitShouldThrowException() throws IOException {
        String uri = baseUri + "/rest/criteria";
        HttpUtils.post(uri, null, new byte[0], charset);
    }

    @Test(expected=IOException.class) // 400 bad parameters
    public void invalidParametersShouldThrowException() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(1, filteredList.length());
    }

    @Test
    public void matchAllShouldNotFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[\"maven\"],\"languages\":[\"java\"],\"licenses\":[\"BSD3CLAUSE\"],\"sizes\":[1]},\"repos\""
        		+ ":[{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\""
        		+ "git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(1, filteredList.length());
    }

    @Test
    public void noCriteriaShouldNotFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[],\"languages\":[],\"licenses\":[],\"sizes\":[]},\"repos\":[{\"commits\":null,\""
        		+ "commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\""
        		+ "repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(1, filteredList.length());
    }

    @Test
    public void buildsystemMismatchShouldFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[\"ant\"],\"languages\":[\"java\"],\"licenses\":[\"BSD3CLAUSE\"],\"sizes\":[1]},\"repos\""
        		+ ":[{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\""
        		+ "git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(0, filteredList.length());
    }

    @Test
    public void languageMismatchShouldFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[\"maven\"],\"languages\":[\"c++\"],\"licenses\":[\"BSD3CLAUSE\"],\"sizes\":[1]},\"repos\""
        		+ ":[{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\""
        		+ "git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(0, filteredList.length());
    }

    @Test
    public void licenseMismatchShouldFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[\"maven\"],\"languages\":[\"java\"],\"licenses\":[\"APACHE\"],\"sizes\":[1]},\"repos\""
        		+ ":[{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\""
        		+ "git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(0, filteredList.length());
    }

    @Test
    public void sizeMismatchShouldFilter() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        addSessionCookie(headers);

        String payload = "{\"criteria\":{\"buildsystems\":[\"maven\"],\"languages\":[\"java\"],\"licenses\":[\"BSD3CLAUSE\"],\"sizes\":[0]},\""
        		+ "repos\":[{\"commits\":null,\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\""
        		+ "https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\""
        		+ "master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\""
        		+ "htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\""
        		+ "issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\""
        		+ "license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[],\""
        		+ "releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\""
        		+ "git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}]}";
        String uri = baseUri + "/rest/criteria";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        JSONArray filteredList = new JSONArray(resp.getContent());
        Assert.assertEquals(0, filteredList.length());
    }
}
