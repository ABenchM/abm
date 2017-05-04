package de.fraunhofer.abm.projectanalysis.impl;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.collection.dao.RepositoryDao;
import de.fraunhofer.abm.collection.dao.RepositoryPropertyDao;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalysis.ProjectAnalysis;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;
import de.fraunhofer.abm.repoarchive.api.RepoArchive;
import de.fraunhofer.abm.scm.api.SCM;
import de.fraunhofer.abm.util.FileUtil;

@Component(immediate=true)
public class ProjectAnalysisImpl implements ProjectAnalysis {

    private static final transient Logger logger = LoggerFactory.getLogger(ProjectAnalysisImpl.class);

    @Reference(cardinality = MULTIPLE, policy = DYNAMIC, policyOption = ReferencePolicyOption.RELUCTANT)
    private volatile List<ProjectAnalyzerFactory> analyzerFactories = new ArrayList<>();

    @Reference(cardinality=ReferenceCardinality.OPTIONAL, policy=ReferencePolicy.DYNAMIC, policyOption=ReferencePolicyOption.GREEDY, bind="setRepoArchive", unbind="unsetRepoArchive")
    private volatile RepoArchive repoArchive;

    @Reference
    private SCM scm;

    @Reference
    private RepositoryDao repoDao;

    @Reference
    private RepositoryPropertyDao repoPropDao;

    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo) throws Exception {
        return new Analyzer().analyze(repo);
    }

    private class Analyzer {
        public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo) throws Exception {
            //            List<RepositoryPropertyDTO> properties = loadPropertiesFromDb(repo);
            //            if(!properties.isEmpty()) {
            //                return properties;
            //            }

            logger.debug("Analyzing repo {}", repo.name);

            String tmpId = UUID.randomUUID().toString();
            File analysisDir = new File("/tmp/analysis/", tmpId);
            File targetDir = new File(analysisDir, repo.id);

            if(repoArchive != null && repoArchive.exists(repo.id)) {
                logger.debug("Repo found in repo archive {}", repo.name);
                repoArchive.retrieve(repo.id, targetDir);
            } else {
                logger.debug("Cloning repo archive {}", repo.name);
                targetDir.mkdirs();

                // clone the repo
                scm.clone(repo, targetDir);

                // archive repo for future uses
                if(repoArchive != null) {
                    try {
                        repoArchive.archive(repo.id, targetDir);
                    } catch (IOException e) {
                        logger.error("Couldn't archive repo {}", e);
                    }
                }
            }

            List<RepositoryPropertyDTO> properties = new ArrayList<>();
            for (ProjectAnalyzerFactory projectAnalyzerFactory : analyzerFactories) {
                ProjectAnalyzer analyzer = projectAnalyzerFactory.createNewAnalyzer();
                try {
                    List<RepositoryPropertyDTO> analyzerResult = analyzer.analyze(repo, targetDir);
                    properties.addAll(analyzerResult);
                } catch (Throwable e) {
                    // errors in an analyzer shouldn't not effect the overall analysis, so we catch all
                    logger.error("Analyzer {} threw an exception", analyzer.getClass().getSimpleName(), e);
                }
            }

            try {
                if(analysisDir.exists()) {
                    FileUtil.deleteRecursively(analysisDir);
                }
            } catch (IOException e2) {
                throw new ServletException("Couldn't delete analysis directory", e2);
            }

            savePropertiesToDb(properties);

            return properties;
        }

        @SuppressWarnings("unused")
        private List<RepositoryPropertyDTO> loadPropertiesFromDb(RepositoryDTO repo) {
            try {
                List<RepositoryPropertyDTO> properties = repoPropDao.findByRepository(repo.id);
                if(!properties.isEmpty()) {
                    logger.debug("Repo properties already in database. Not analyzing {} again!", repo.name);
                    return properties;
                }
            } catch(Exception e) {
                logger.debug("Properties not found in db, because: {}", e.getMessage());
            }
            return Collections.emptyList();
        }

        private void savePropertiesToDb(List<RepositoryPropertyDTO> properties) {
            for (RepositoryPropertyDTO prop : properties) {
                try {
                    RepositoryDTO repo = repoDao.findById(prop.repositoryId);
                    // only save the properties, if the repository exists in the database
                    if (repo != null) {
                        prop.id = UUID.nameUUIDFromBytes( (prop.repositoryId + "_" + prop.name).getBytes("utf-8") ).toString();
                        try {
                            repoPropDao.findById(prop.id);
                            repoPropDao.update(prop);
                        } catch(Exception e) {
                            if(e.getCause().getMessage().equals("No entity found for query")) {
                                repoPropDao.save(prop);
                            } else {
                                logger.error("Couldn't save repository property {}={} for {}", prop.name, prop.value, prop.repositoryId, e);
                            }
                        }
                    }
                } catch (Exception e) {
                    if(e.getCause() != null && e.getCause().getMessage().contains("No entity found for query")) {
                        logger.debug("Repository {} not yet in DB. Not saving repository property {}={}", prop.repositoryId, prop.name, prop.value);
                    } else {
                        logger.error("Couldn't save repository property {}={} for {}", prop.name, prop.value, prop.repositoryId, e);
                    }
                }
            }
        }
    }


    public void setRepoArchive(RepoArchive repoArchive) {
        this.repoArchive = repoArchive;
    }

    public void unsetRepoArchive(RepoArchive repoArchive) {
        this.repoArchive = null;
    }
}
