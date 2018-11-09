package de.fraunhofer.abm.crawler.github;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.crawler.api.Crawler;
import de.fraunhofer.abm.crawler.api.Criteria;
import de.fraunhofer.abm.domain.BranchDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.TagDTO;
import de.fraunhofer.abm.http.client.Base64;
import de.fraunhofer.abm.http.client.HttpUtils;
import de.fraunhofer.abm.util.AbmApplicationConstants;

@Component
public class GithubCrawler implements Crawler {

    private static final transient Logger logger = LoggerFactory.getLogger(GithubCrawler.class);

    @Override
    public List<RepositoryDTO> search(Criteria searchCriteria) throws Exception {
        logger.debug("Searching for [{}]", searchCriteria.toString());
        return Collections.emptyList();
    }

    @Override
    public List<RepositoryDTO> search(String query) throws Exception {
        logger.debug("Searching for [{}]", query);
        List<RepositoryDTO> result = new ArrayList<>();
        try{
        String uri = "https://api.github.com/search/repositories?q=" + URLEncoder.encode(query, "UTF-8");
        String resp = HttpUtils.get(uri, header, "utf-8");
        JSONObject json = new JSONObject(resp);
        if(json.has("items")) {
            JSONArray items = json.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                result.add(createRepository(items.getJSONObject(i)));
            }
        }
    }catch(Exception e)
    {
    	logger.debug("Couldn't find the repository for the owner given", e);	
    }
        return result;
    }

    public RepositoryDTO createRepository(JSONObject item) throws JSONException, UnsupportedEncodingException {
        RepositoryDTO repo = new RepositoryDTO();
        repo.remoteId = item.getInt("id");
        repo.id = UUID.nameUUIDFromBytes(Integer.toString(repo.remoteId).getBytes("utf-8")).toString();
        repo.forks = item.getInt("forks_count");
        repo.openIssues = item.getInt("open_issues_count");
        repo.size = item.getInt("size");

        repo.name = item.getString("name"); // full_name
        if (!item.isNull("description")) {
            repo.description = item.getString("description");
        }
        if (item.has("pushed_at") && (item.get("pushed_at") instanceof String)) {
            repo.latestUpdate = item.getString("pushed_at");
        }
        repo.creationDate = parseCreationDate(item.getString("created_at"));
        repo.score = item.getInt("score");

        repo.hasDownloads = item.getBoolean("has_downloads");
        repo.hasWiki = item.getBoolean("has_wiki");
        repo.isPrivate = item.getBoolean("private");

        repo.htmlUrl = item.getString("html_url");
        repo.releasesUrl = item.getString("releases_url");
        repo.commitsUrl = item.getString("commits_url");
        repo.issuesUrl = item.getString("issues_url");
        repo.contentsUrl = item.getString("contents_url");
        repo.contributorsUrl = item.getString("contributors_url");
        repo.repositoryUrl = item.getString("clone_url");
        repo.repositoryType = RepositoryDTO.TYPE_GIT;
        repo.defaultBranch = item.getString("default_branch");

        if (item.has("licence")) {
            JSONObject lic = item.getJSONObject("license");
            repo.license = lic.getString("name");
        }

        repo.starred = item.getInt("stargazers_count");
        repo.watched = item.getInt("watchers_count");

        JSONObject owner = item.getJSONObject("owner");
        repo.owner = owner.getString("login");
        repo.ownerType = owner.getString("type");
        return repo;
    }

    private Date parseCreationDate(String string) {
        Date date = new Date();
        date.setTime(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // 2016-03-02T18:31:49Z
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            logger.warn("Couldn't parse repository creation date [" + string + "]", e);
        }
        return date;
    }

    @Override
    public boolean isSourceFor(RepositoryDTO repo) {
        return repo.repositoryUrl.contains("github.com");
    }

    @Override
    public List<BranchDTO> getBranches(RepositoryDTO repo) throws IOException {
        List<BranchDTO> branches = new ArrayList<>();
        int page = 1;
        boolean resultEmpty = false;
        while(!resultEmpty) {
            String uri = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/branches?page=" + page;
            String resp = HttpUtils.get(uri, header, "utf-8");
            JSONArray json = new JSONArray(resp);
            if(json.length() == 0) {
                resultEmpty = true;
            }
            for (int i = 0; i < json.length(); i++) {
                JSONObject branch = json.getJSONObject(i);
                BranchDTO dto = new BranchDTO();
                dto.name = branch.getString("name");
                dto.commit = branch.getJSONObject("commit").getString("sha");
                branches.add(dto);
            }
            page++;
        }
        return branches;
    }

    @Override
    public List<CommitDTO> getCommits(RepositoryDTO repo, int page) throws IOException {
        List<CommitDTO> commits = new ArrayList<>();
        String uri = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/commits?page=" + page;
        String resp = HttpUtils.get(uri, header, "utf-8");
        JSONArray json = new JSONArray(resp);
        for (int i = 0; i < json.length(); i++) {
            JSONObject commit = json.getJSONObject(i);
            CommitDTO dto = new CommitDTO();
            dto.commitId = commit.getString("sha");
            dto.message = commit.getJSONObject("commit").getString("message");
            dto.creationDate = getCommitDate(commit);
            commits.add(dto);
        }
        return commits;
    }

    private Date getCommitDate(JSONObject commit) {
        Date date = new Date(0);
        try {
            String dateString = commit.getJSONObject("commit").getJSONObject("author").getString("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // 2014-09-29T13:13:15Z
            date = sdf.parse(dateString);
        } catch (Exception e) {
            logger.warn("Couldn't parse commit date", e);
        }
        return date;
    }

    @Override
    public List<TagDTO> getTags(RepositoryDTO repo) throws Exception {
        List<TagDTO> tags = new ArrayList<>();
        int page = 1;
        boolean resultEmpty = false;
        while(!resultEmpty) {
            String uri = "https://api.github.com/repos/" + repo.owner + "/" + repo.name + "/tags?page=" + page;
            String resp = HttpUtils.get(uri, header, "utf-8");
            JSONArray json = new JSONArray(resp);
            if(json.length() == 0) {
                resultEmpty = true;
            }
            for (int i = 0; i < json.length(); i++) {
                JSONObject branch = json.getJSONObject(i);
                TagDTO dto = new TagDTO();
                dto.name = branch.getString("name");
                dto.commit = branch.getJSONObject("commit").getString("sha");
                tags.add(dto);
            }
            page++;
        }
        return tags;
    }

    static Map<String, String> header = new HashMap<>();
    static {
		String token = AbmApplicationConstants.githubToken();
		String authString = "Basic " + Base64.encodeBytes(token.getBytes());
        header.put("Authorization", authString);
    }
}
