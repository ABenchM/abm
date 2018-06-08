package de.fraunhofer.abm.crawler.bitbucket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.crawler.api.Crawler;
import de.fraunhofer.abm.crawler.api.Criteria;
import de.fraunhofer.abm.domain.BranchDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.TagDTO;
import de.fraunhofer.abm.http.client.HttpUtils;

@Component(name="de.fraunhofer.abm.crawler.bitbucket")
public class BitBucketCrawler implements Crawler{
	
	private static final transient Logger logger = LoggerFactory.getLogger(BitBucketCrawler.class);
	
	@Override
	public List<RepositoryDTO> search(Criteria searchCriteria) throws Exception {
		logger.debug("Searching for [{}]", searchCriteria.toString());
		return Collections.emptyList();
	}
	
	@Override
	/**
	 * This method is used to search for repositories that contain a specific string
	 * in either the repository name, owner name or description.
	 * 
	 * @param query
	 *            This is the String that is searched for.
	 * @return List<RepositoryDTO> This returns a list of the first 30 repositories
	 *         that match the query.
	 */
	public List<RepositoryDTO> search(String query) throws Exception {
		List<RepositoryDTO> result = new ArrayList<>();
		String urlpattern = "https://bitbucket.org/repo/all/%s?name=%s";
		int pages = 3;

		for (int i = 1; i <= pages; i++) {
			String url = String.format(urlpattern, i, query);
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Elements articles = doc.getElementsByTag("article");
			if (articles.size() == 0)
				break;

			for (Element article : articles) {
				Element header = article.getElementsByTag("h1").first();
				Element atag = header.getElementsByTag("a").first();
				String href = atag.attr("href");
				try {
					String itemUri = "https://api.bitbucket.org/2.0/repositories" + href;
					String itemResp = HttpUtils.get(itemUri, null, "utf-8");
					JSONObject repo = new JSONObject(itemResp);
					result.add(createRepository(repo));
				} catch (Exception e) {
					logger.debug("Couldn't find the repository for the owner given", e);
				}
			}
		}
		return result;
	}

	/**
	 * This method takes a json object that holds the data for a repository and
	 * parses it into a Data Transfer Object. (Slashed out lines are variables that
	 * cannot be directly obtained from the bitbucket API)
	 * 
	 * @param item
	 *            This is the JSONObject that will be parsed.
	 * @return RepositoryDTO This is the parsed Data Transfer Object.
	 */
	public RepositoryDTO createRepository(JSONObject item) throws JSONException, UnsupportedEncodingException {
		RepositoryDTO repo = new RepositoryDTO();

		// repo.remoteId = item.getInt("id");
		// repo.forks = item.getInt("forks_count");
		// repo.openIssues = item.getInt("open_issues_count");
		// repo.score = item.getInt("score");
		// repo.hasDownloads = item.getBoolean("has_downloads");
		// repo.starred = item.getInt("stargazers_count");
		// repo.watched = item.getInt("watchers_count");

		String uuid = item.getString("uuid");
		uuid = uuid.replace("{", "");// remove brackets from string
		uuid = uuid.replace("}", "");
		repo.id = uuid;

		repo.size = item.getInt("size");

		String[] nameHolder = item.getString("full_name").split("/");
		repo.name = nameHolder[1]; // uses "full_name" as "name" may contain
									// unicode or other unsafe characters
		if (!item.isNull("description")) {
			repo.description = item.getString("description");
		}
		if (item.has("updated_on") && (item.get("updated_on") instanceof String)) {
			repo.latestUpdate = item.getString("updated_on");
		}
		repo.creationDate = parseCreationDate(item.getString("created_on"));
		repo.hasWiki = item.getBoolean("has_wiki");
		repo.isPrivate = item.getBoolean("is_private");

		JSONObject links = item.getJSONObject("links");

		// repo.releasesUrl = item.getString("releases_url");
		// repo.issuesUrl = item.getString("issues_url");
		// repo.contentsUrl = item.getString("contents_url");
		// repo.contributorsUrl = item.getString("contributors_url");

		repo.htmlUrl = links.getJSONObject("html").getString("href");
		repo.commitsUrl = links.getJSONObject("commits").getString("href");
		repo.repositoryUrl = links.getJSONArray("clone").getJSONObject(0).getString("href");
		if (item.getString("scm").equals("git")) {
			repo.repositoryType = RepositoryDTO.TYPE_GIT;
		} else if (item.getString("scm").equals("hg")) {
			repo.repositoryType = RepositoryDTO.TYPE_HG;
		}

		if (!item.isNull("mainbranch")) {
			repo.defaultBranch = item.getJSONObject("mainbranch").getString("name");
		}
		if (item.has("license")) {
			JSONObject license = item.getJSONObject("license");
			repo.license = license.getString("name");
		}

		JSONObject owner = item.getJSONObject("owner");
		repo.owner = owner.getString("username");
		repo.ownerType = owner.getString("type");
		return repo;
	}

	/**
	 * This method parses the date from a String.
	 * 
	 * @param string
	 *            This is the original date string.
	 * @return Date This is the parsed date.
	 */
	private Date parseCreationDate(String string) {
		Date date = new Date();
		date.setTime(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // 2008-06-25T00:53:00.273366+00:00
		try {
			date = sdf.parse(string);
		} catch (ParseException e) {
			logger.warn("Couldn't parse repository creation date [" + string + "]", e);
		}
		return date;
	}

	/**
	 * This method will return true if the repository is from Github. (In the
	 * context of this crawler it should always return false)
	 * 
	 * @param repo
	 *            This is the repository that is checked.
	 * @return boolean The result.
	 */
	@Override
	public boolean isSourceFor(RepositoryDTO repo) {
		return repo.repositoryUrl.contains("github.com");
	}

	/**
	 * This method takes a RepositoryDTO that holds the data for a repository and
	 * retrieves information about all of the branches from that repository.
	 * 
	 * @param repo
	 *            This is the repository from which the branches be obtained.
	 * @return List<BranchDTO> This is the list of branches from the repository.
	 */
	@Override
	public List<BranchDTO> getBranches(RepositoryDTO repo) throws Exception {
		List<BranchDTO> branches = new ArrayList<>();
		int page = 1;
		boolean resultEmpty = false;
		while (!resultEmpty) {
			String uri = "https://api.bitbucket.org/2.0/repositories/" + repo.owner + "/" + repo.name
					+ "/refs/branches?page=" + page;
			String resp = HttpUtils.get(uri, null, "utf-8");
			JSONObject jsonObj = new JSONObject(resp);
			JSONArray json = jsonObj.getJSONArray("values");
			if (json.length() == 0) {
				resultEmpty = true;
			}
			for (int i = 0; i < json.length(); i++) {
				JSONObject branch = json.getJSONObject(i);
				BranchDTO dto = new BranchDTO();
				dto.name = branch.getString("name");
				dto.commit = branch.getJSONObject("target").getString("hash");
				branches.add(dto);
			}
			page++;
		}
		return branches;
	}

	/**
	 * This method takes a RepositoryDTO that holds the data for a repository and
	 * retrieves information about the commits from a single page of that
	 * repository.
	 * 
	 * @param repo
	 *            This is the repository from which the commits be obtained.
	 * @param page
	 *            This is the page number from which the commits be obtained.
	 * @return List<CommitDTO> This is the list of commits from the repository.
	 */
	@Override
	public List<CommitDTO> getCommits(RepositoryDTO repo, int page) throws Exception {
		List<CommitDTO> commits = new ArrayList<>();
		String uri = "https://api.bitbucket.org/2.0/repositories/" + repo.owner + "/" + repo.name + "/commits?page="
				+ page;
		String resp = HttpUtils.get(uri, null, "utf-8");
		JSONObject jsonObj = new JSONObject(resp);
		JSONArray json = jsonObj.getJSONArray("values");
		for (int i = 0; i < json.length(); i++) {
			JSONObject commit = json.getJSONObject(i);
			CommitDTO dto = new CommitDTO();
			dto.commitId = commit.getString("hash");
			dto.message = commit.getString("message");
			dto.creationDate = getCommitDate(commit);
			commits.add(dto);
		}
		return commits;
	}

	/**
	 * This method retrieves the date from a JSONObject that holds data on a commit.
	 * 
	 * @param commit
	 *            This is the commit the date will be found for.
	 * @return Date This is the date of the commit.
	 */
	private Date getCommitDate(JSONObject commit) {
		Date date = new Date(0);
		try {
			String dateString = commit.getString("date");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // 2008-06-25T01:01:47+00:00
			date = sdf.parse(dateString);
		} catch (Exception e) {
			logger.warn("Couldn't parse commit date", e);
		}
		return date;
	}

	/**
	 * This method takes a RepositoryDTO that holds the data for a repository and
	 * retrieves information about all of the tags from that repository.
	 * 
	 * @param repo
	 *            This is the repository from which the tags be obtained.
	 * @return List<TagDTO> This is the list of tags from the repository.
	 */
	@Override
	public List<TagDTO> getTags(RepositoryDTO repo) throws Exception {
		List<TagDTO> tags = new ArrayList<>();
		boolean resultEmpty = false;
		String uri = "https://api.bitbucket.org/2.0/repositories/" + repo.owner + "/" + repo.name + "/refs/tags";
		while (!resultEmpty) {
			String resp = HttpUtils.get(uri, null, "utf-8");
			JSONObject jsonObj = new JSONObject(resp);
			JSONArray json = jsonObj.getJSONArray("values");
			if (json.length() == 0) {
				resultEmpty = true;
			}
			for (int i = 0; i < json.length(); i++) {
				JSONObject tag = json.getJSONObject(i);
				TagDTO dto = new TagDTO();
				dto.name = tag.getString("name");
				dto.commit = tag.getJSONObject("target").getString("hash");
				tags.add(dto);
			}
			if (jsonObj.has("next")) {
				uri = jsonObj.getString("next");
			} else {
				resultEmpty = true;
			}
		}
		return tags;
	}	
	
	
}

