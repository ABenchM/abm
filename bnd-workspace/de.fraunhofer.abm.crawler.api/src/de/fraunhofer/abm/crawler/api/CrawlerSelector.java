package de.fraunhofer.abm.crawler.api;

import de.fraunhofer.abm.domain.RepositoryDTO;

public interface CrawlerSelector {
	
	public Crawler selectCrawler(RepositoryDTO repo);

}
