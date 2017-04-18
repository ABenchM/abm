package de.fraunhofer.abm.crawler.github;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import de.fraunhofer.abm.crawler.api.Crawler;
import de.fraunhofer.abm.domain.BranchDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import junit.framework.TestCase;

public class GithubCrawlerTest extends TestCase {

    private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

    @Test
    public void testSearch() throws Exception {
        Crawler crawler = getService(Crawler.class);
        List<RepositoryDTO> repos = crawler.search("slf4j");
        Assert.assertNotNull(repos);
        Assert.assertFalse(repos.isEmpty());
    }

    @Test
    public void testIsSourceFor() throws Exception {
        Crawler crawler = getService(Crawler.class);
        RepositoryDTO repo = new RepositoryDTO();
        repo.repositoryUrl = "http://github.com/foo/bar.git";
        Assert.assertTrue(crawler.isSourceFor(repo));
        repo.repositoryUrl = "http://sourceforge.net/foo/bar.git";
        Assert.assertFalse(crawler.isSourceFor(repo));
    }

    @Test
    public void testGetBranches() throws Exception {
        Crawler crawler = getService(Crawler.class);
        RepositoryDTO repo = new RepositoryDTO();
        repo.owner = "qos-ch";
        repo.name = "slf4j";
        List<BranchDTO> branches = crawler.getBranches(repo);
        Assert.assertNotNull(branches);
        Assert.assertFalse(branches.isEmpty());
    }

    <T> T getService(Class<T> clazz) throws InterruptedException {
        ServiceTracker<T,T> st = new ServiceTracker<>(context, clazz, null);
        st.open();
        return st.waitForService(1000);
    }
}
