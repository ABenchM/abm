package de.fraunhofer.abm.app.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.crawler.api.Crawler;
import de.fraunhofer.abm.domain.BranchDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.TagDTO;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.commit.selection")
public class CommitSelectionController extends AbstractController implements REST {

    private static final transient Logger logger = LoggerFactory.getLogger(CommitSelectionController.class);

    @Reference(cardinality= ReferenceCardinality.MULTIPLE, bind="addCrawler", unbind="removeCrawler", policy = ReferencePolicy.DYNAMIC)
    private volatile List<Crawler> crawlers = new ArrayList<>();

    interface PostRequest extends RESTRequest {
        RequestData _body();
    }

    public List<CommitDTO> postCommits(PostRequest pr) throws Exception {
        List<CommitDTO> commits = new ArrayList<>();
        RequestData data = pr._body();
        if(data.repository == null) {
            sendError(pr._response(), HttpServletResponse.SC_BAD_REQUEST, "repository is missing");
        }

        RepositoryDTO repo = data.repository;
        int page = data.page;
        for (Crawler crawler : crawlers) {
            if(crawler.isSourceFor(repo)) {
                logger.debug("Using {} to fetch the commit list", crawler.getClass().getName());
                commits = crawler.getCommits(repo, page);
                break;
            }
        }
        return commits;
    }

    public List<BranchDTO> postBranches(PostRequest pr) throws Exception {
        List<BranchDTO> branches = new ArrayList<>();
        RequestData data = pr._body();
        RepositoryDTO repo = data.repository;
        for (Crawler crawler : crawlers) {
            if(crawler.isSourceFor(repo)) {
                logger.debug("Using {} to fetch the branch list", crawler.getClass().getName());
                branches = crawler.getBranches(repo);
                break;
            }
        }
        return branches;
    }

    public List<TagDTO> postTags(PostRequest pr) throws Exception {
        List<TagDTO> tags = new ArrayList<>();
        RequestData data = pr._body();
        RepositoryDTO repo = data.repository;
        for (Crawler crawler : crawlers) {
            if(crawler.isSourceFor(repo)) {
                logger.debug("Using {} to fetch the tag list", crawler.getClass().getName());
                tags = crawler.getTags(repo);
                break;
            }
        }
        return tags;
    }

    public void addCrawler(Crawler crawler) {
        crawlers.add(crawler);
    }

    public void removeCrawler(Crawler crawler) {
        crawlers.remove(crawler);
    }

    public static class RequestData {
        public RepositoryDTO repository;
        public int page;
        public RequestData() {}
    }

    @Override
    Logger getLogger() {
        return logger;
    }
}
