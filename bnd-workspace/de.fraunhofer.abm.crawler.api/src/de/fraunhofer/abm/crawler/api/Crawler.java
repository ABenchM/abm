package de.fraunhofer.abm.crawler.api;

import java.util.List;

import de.fraunhofer.abm.domain.BranchDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.TagDTO;

public interface Crawler {

    public List<RepositoryDTO> search(Criteria searchCriteria) throws Exception;
    public List<RepositoryDTO> search(String query) throws Exception;

    public boolean isSourceFor(RepositoryDTO repo);

    public List<BranchDTO> getBranches(RepositoryDTO repo) throws Exception;
    public List<CommitDTO> getCommits(RepositoryDTO repo, int page) throws Exception;
    public List<TagDTO> getTags(RepositoryDTO repo) throws Exception;

}
