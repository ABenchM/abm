package de.fraunhofer.abm.suitebuilder.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildStep;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CommitDao;
import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.repoarchive.api.RepoArchive;
import de.fraunhofer.abm.scm.api.SCM;
import de.fraunhofer.abm.suitebuilder.BuildProcess;
import de.fraunhofer.abm.suitebuilder.SuiteBuilder;

@Designate(ocd = Configuration.class, factory=false)
@Component(name = "de.fraunhofer.abm.suitebuilder.SuiteBuilder", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SuiteBuilderImpl implements SuiteBuilder {
    private static final transient Logger logger = LoggerFactory.getLogger(SuiteBuilderImpl.class);

    private ThreadPoolExecutor executor;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    @Reference(cardinality= ReferenceCardinality.MULTIPLE, bind="addSCM", unbind="removeSCM", policy = ReferencePolicy.DYNAMIC)
    private volatile List<SCM> scms = new ArrayList<>();

    @Reference(cardinality= ReferenceCardinality.MULTIPLE, bind="addBuilderFactory", unbind="removeBuilderFactory", policy = ReferencePolicy.DYNAMIC)
    private volatile List<ProjectBuilderFactory> builderFactories = new ArrayList<>();

    @Reference
    private CommitDao commitDao;

    @Reference
    private VersionDao versionDao;

    @Reference
    private BuildResultDao buildResultDao;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY,
            bind = "bindRepoArchive", unbind = "unbindRepoArchive")
    private volatile RepoArchive repoArchive;

    /**
     * The base directory for all project build workspaces
     */
    private File workspaceRoot;

    /**
     * Maps a build process ID to the actual BuildProcess object
     */
    private Map<String, BuildProcess> buildProcesses = new HashMap<>();

    @Override
    public BuildProcess initialize(VersionDTO version) throws Exception {
        File ws = createWorkspace();
        BuildProcess buildProcess = new BuildProcess(version, ws, scms, builderFactories, commitDao, buildResultDao, repoArchive);
        buildProcesses.put(buildProcess.getId(), buildProcess);
        return buildProcess;
    }

    @Override
    public void start(BuildProcess buildProcess) throws Exception {
        Future<BuildResultDTO> futureBuildResult = executor.submit(buildProcess);
        buildProcess.setFutureBuildResult(futureBuildResult);

        /* TODO add completion service to get the final result and save it to DB
         * create archive zip
         * clean up ?!?
         */

        VersionDTO version = buildProcess.getVersion();
        version.frozen = true;
        versionDao.update(version);
    }

    @Override
    public BuildProcess getBuildProcess(String id) {
        return buildProcesses.get(id);
    }

    private File createWorkspace() {
        File ws = new File(workspaceRoot, UUID.randomUUID().toString());
        ws.mkdirs();
        return ws;
    }

    @Activate
    void activate(Configuration config) {
        int coreSize = config.coreSize();
        int maxSize = config.maximumPoolSize();
        long keepAlive = config.keepAliveTime();

        logger.info("Creating suite builder thread pool with coreSize:{}, maxPoolSize:{}, keepAlive:{}", coreSize, maxSize, keepAlive);
        executor = new ThreadPoolExecutor(coreSize, maxSize, keepAlive, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

        String workspaceRoot = config.workspaceRoot();
        logger.info("Creating workspace root: {}", workspaceRoot);
        this.workspaceRoot = new File(workspaceRoot);
    }

    /*
     * Cancels the tasks submitted by the exiting bundle, shutting down the executor service if no more bundle is using it
     */
    @Deactivate
    void deactivate() {
        List<Runnable> running = executor.shutdownNow();
        if (!running.isEmpty()) {
            logger.warn("Shutting down while builds {} are running", running);
        }
    }

    protected void addSCM(SCM scm){
        scms.add(scm);
    }

    protected void removeSCM(SCM scm){
        scms.remove(scm);
    }

    int i = 0;
    protected void addBuilderFactory(ProjectBuilderFactory factory) {
    	
        builderFactories.add(factory);
    }

    protected void removeBuilderFactory(ProjectBuilderFactory factory) {
        builderFactories.remove(factory);
    }

    public static List<BuildStepDTO> toBuildStepDTOs(List<BuildStep<?>> steps) {
        List<BuildStepDTO> buildStepDTOs = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            buildStepDTOs.add(steps.get(i).toDTO(i));
        }
        return buildStepDTOs;
    }

    public void bindRepoArchive(RepoArchive repoArchive) {
        this.repoArchive = repoArchive;
    }

    public void unbindRepoArchive(RepoArchive repoArchive) {
        this.repoArchive = null;
    }
}
