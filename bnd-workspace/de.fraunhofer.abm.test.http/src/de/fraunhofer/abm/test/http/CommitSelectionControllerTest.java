package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.http.client.HttpResponse;
import de.fraunhofer.abm.http.client.HttpUtils;

public class CommitSelectionControllerTest extends AbstractHttpTest {

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostCommitsShouldThrowException() throws IOException {
        Map<String, String> headers = HttpUtils.createFirefoxHeader();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/commits";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpUtils.post(uri, headers, payload.getBytes(), charset);
    }

    @Test
    public void postCommitsShouldReturnAList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/commits";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray list1 = new JSONArray(resp.getContent());
        Assert.assertTrue(list1.length() > 0);

        payload = "{\"page\":2,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray list2 = new JSONArray(resp.getContent());
        Assert.assertTrue(list2.length() > 0);

        Assert.assertNotEquals(list1.getJSONObject(0).get("commitId"), list2.getJSONObject(0).get("commitId"));
    }

    @Test
    public void unknownCrawlerShouldReturnAnEmptyCommitList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/commits";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://foobar.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray list1 = new JSONArray(resp.getContent());
        Assert.assertTrue(list1.length() == 0);
    }

    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostBranchesShouldThrowException() throws IOException {
        Map<String, String> headers = HttpUtils.createFirefoxHeader();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/branches";
        HttpUtils.post(uri, headers, new byte[0], charset);
    }

    @Test
    public void postBranchesShouldReturnAList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/branches";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray branches = new JSONArray(resp.getContent());
        Assert.assertTrue(branches.length() > 0);
        Assert.assertEquals("master", branches.getJSONObject(0).get("name"));
    }

    @Test
    public void unknownCrawlerShouldReturnAnEmptyBranchList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/branches";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://foobar.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray list1 = new JSONArray(resp.getContent());
        Assert.assertTrue(list1.length() == 0);
    }


    @Test(expected=IOException.class) // 401 unauthorized
    public void unauthorizedPostTagsShouldThrowException() throws IOException {
        Map<String, String> headers = HttpUtils.createFirefoxHeader();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/tags";
        HttpUtils.post(uri, headers, new byte[0], charset);
    }

    @Test
    public void postTagsShouldReturnAList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/tags";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://github.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray tags = new JSONArray(resp.getContent());
        Assert.assertTrue(tags.length() > 0);
        Assert.assertTrue(!tags.getJSONObject(0).get("name").toString().isEmpty());
    }

    @Test
    public void unknownCrawlerShouldReturnAnEmptyTagList() throws IOException {
        Map<String, String> headers = login();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        String uri = baseUri + "/rest/tags";
        String payload = "{\"page\":1,\"repository\":{\"commits\":[\"60c0477a-eed1-49d5-be82-680012ade77d\",\"7a7ef745-3222-4374-a758-8765a1090249\"],\"commitsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/commits{/sha}\",\"contentsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contents/{+path}\",\"contributorsUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/contributors\",\"creationDate\":\"2011-09-30T17:49:23\",\"defaultBranch\":\"master\",\"description\":\"An implementation of the Simple VDR Protocol.\",\"forks\":1,\"hasDownloads\":true,\"hasWiki\":true,\"htmlUrl\":\"https://github.com/hampelratte/svdrp4j\",\"id\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"isPrivate\":false,\"issuesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/issues{/number}\",\"latestUpdate\":\"2016-10-04T18:43:16Z\",\"license\":\"\",\"name\":\"svdrp4j\",\"openIssues\":0,\"owner\":\"hampelratte\",\"ownerType\":\"User\",\"properties\":[{\"description\":\"License\",\"id\":\"1b890a77-4dc2-3997-8cf3-3a187fd7f0d9\",\"name\":\"license\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"BSD3CLAUSE\"},{\"description\":\"Built with Apache Maven\",\"id\":\"3160504c-062a-3493-ade2-361dff611ed5\",\"name\":\"build.system\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"maven\"},{\"description\":\"Number of files in the project\",\"id\":\"362272eb-e1d2-36d5-aadb-3fb70ea3159a\",\"name\":\"files\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"211\"},{\"description\":\"Programming language java\",\"id\":\"fdb45ab4-305b-36ae-8cb6-d8668f0237df\",\"name\":\"language\",\"repositoryId\":\"1c1dfb8c-2632-3346-9f33-8af508cba47d\",\"value\":\"java\"}],\"releasesUrl\":\"https://api.github.com/repos/hampelratte/svdrp4j/releases{/id}\",\"remoteId\":2490885,\"repositoryType\":\"git\",\"repositoryUrl\":\"https://foobar.com/hampelratte/svdrp4j.git\",\"score\":25,\"size\":1458,\"starred\":3,\"watched\":3}}";
        HttpResponse resp = HttpUtils.post(uri, headers, payload.getBytes(), charset);
        Assert.assertEquals(200, resp.getResponseCode());
        JSONArray tags = new JSONArray(resp.getContent());
        Assert.assertTrue(tags.length() == 0);
    }
}
