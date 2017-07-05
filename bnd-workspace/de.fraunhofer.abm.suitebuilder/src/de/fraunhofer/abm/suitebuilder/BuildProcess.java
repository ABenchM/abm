package de.fraunhofer.abm.suitebuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildProgressListener;
import de.fraunhofer.abm.builder.api.BuildStep;
import de.fraunhofer.abm.builder.api.ProjectBuilder;
import de.fraunhofer.abm.builder.api.ProjectBuilderFactory;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.CommitDao;
import de.fraunhofer.abm.domain.BuildResultDTO;
import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.ProjectBuildDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.VersionDTO;
import de.fraunhofer.abm.repoarchive.api.RepoArchive;
import de.fraunhofer.abm.scm.api.SCM;
import de.fraunhofer.abm.suitebuilder.impl.CheckoutRepo;
import de.fraunhofer.abm.suitebuilder.impl.CreateBuilderStep;
import de.fraunhofer.abm.suitebuilder.impl.Zip;
import de.fraunhofer.abm.util.FileUtil;

public class BuildProcess implements Callable<BuildResultDTO> {

    private static final transient Logger logger = LoggerFactory.getLogger(BuildProcess.class);

    public static enum STATUS {
        WAITING, RUNNING, FINISHED, CANCELLED, FAILED
    }

    private STATUS status = STATUS.WAITING;
    private VersionDTO version;
    private File workspace;
    private List<SCM> scms;
    private List<ProjectBuilderFactory> builderFactories;
    private Future<BuildResultDTO> futureBuildResult;
    private BuildResultDTO buildResult;
    private List<BuildProgressListener> listeners = new ArrayList<>();
    private String id;
    private String repoId;
    private ProjectBuilder builder;
    private CommitDao commitDao;
    private BuildResultDao buildResultDao;
    private RepoArchive repoArchive;

    public BuildProcess(VersionDTO version, File workspace, List<SCM> scms, List<ProjectBuilderFactory> builderFactories, CommitDao commitDao, BuildResultDao buildResultDao, RepoArchive repoArchive) {
        this.version = version;
        this.workspace = workspace;
        this.scms = scms;
        this.builderFactories = builderFactories;
        this.commitDao = commitDao;
        this.buildResultDao = buildResultDao;
        this.repoArchive = repoArchive;
        this.id = UUID.randomUUID().toString();
        this.buildResult = new BuildResultDTO();
        this.buildResult.id = this.id;
        this.buildResult.status = status.toString();
        createBuildResultStructure();
    }

    private void createBuildResultStructure() {
        this.buildResult.versionId = version.id;
        this.buildResult.dir = workspace.getAbsolutePath();
        for (CommitDTO commit : version.commits) {
            RepositoryDTO repo = commit.repository;
            ProjectBuildDTO projectBuildDTO = new ProjectBuildDTO();
            projectBuildDTO.commit = commit;
            projectBuildDTO.repository = repo;
            projectBuildDTO.repositoryId = repo.id;
            projectBuildDTO.buildResultId = this.buildResult.id;
            projectBuildDTO.buildSteps = new ArrayList<>();
            this.buildResult.projectBuilds.add(projectBuildDTO);
        }
        buildResultDao.save(buildResult);
    }

    @Override
    public BuildResultDTO call() throws Exception {
        this.status = STATUS.RUNNING;
        buildResult.status = this.status.toString();
        buildResult.date = new Date();
        buildResultDao.update(buildResult);

        String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(buildResult.date);
        File archive = new File(workspace, "abm-"+date);
        archive.mkdirs();

        for (ProjectBuildDTO projectBuild: buildResult.projectBuilds) {
            if(status == STATUS.CANCELLED || status == STATUS.FAILED) {
                break;
            }

            CommitDTO commit = projectBuild.commit;
            RepositoryDTO repo = commit.repository;
            this.repoId = repo.id;
            File repoDir = new File(workspace, repo.id+'_'+System.currentTimeMillis());

            // the checkout step is common to all builders, so it is handled by BuildProcess,
            // but it is still added to the list of build steps, so that it is visible to the user
            // and is saved in the DB
            CheckoutRepo checkoutRepoStep = createCheckoutStep(repo, repoDir, commit);
            checkoutRepoStep.execute();
            CreateBuilderStep createBuilder = new CreateBuilderStep(repo, repoDir, builderFactories);
            try {
                builder = createBuilder.execute();
                builder.addBuildStep(checkoutRepoStep);
                //builder.addBuildStep(createBuilder);

                FileUtil.deleteRecursively(repoDir); // this is inefficient, we download the project, to check, which builder to use and
                // afterwards delete the directory again, so that it does not interfere with the build process
                logger.info("Building {} with {}", repo.name, builder.getClass().getName());
                builder.addBuildProgressListener(new BuildProgressListener() {
                    @Override
                    public void buildStepChanged(BuildStep<?> step) {
                        switch (step.getStatus()) {
                        case SUCCESS:
                            logger.info("{}: Build step finished with success: {}\n{}", step.getRepository().name, step.getName(), step.getOutput());
                            break;
                        case FAILED:
                            logger.info("{}: Build step finished with error: {}\nSTDOUT:{}\nSTDERR:{}", step.getRepository().name, step.getName(), step.getOutput(),
                                    step.getErrorOutput());
                            break;
                        default:
                            logger.info("{}: Build step status changed: {} - {}", step.getRepository().name, step.getName(), step.getStatus());
                        }

                        for (BuildStepDTO dto : projectBuild.buildSteps) {
                            if(dto.id.equals(step.getId())) {
                                dto.status = step.getStatus().toString();
                                dto.stdout = step.getOutput();
                                dto.stderr = step.getErrorOutput();
                            }
                        }
                        buildResultDao.update(buildResult);
                    }

                    @Override
                    public void buildInitialized(RepositoryDTO repository, List<BuildStep<?>> steps) {
                        logger.info("Builder has been initialized");
                        for (BuildStep<?> buildStep : steps) {
                            logger.info("{}: Step: {}", buildStep.getRepository().name, buildStep.getName());
                        }
                        projectBuild.buildSteps.addAll(toBuildStepDTOs(builder.getBuildSteps(), projectBuild));
                        buildResultDao.update(buildResult);
                    }

                    @Override
                    public void buildFinished(RepositoryDTO repository) {
                        logger.info("Build {} is finished for {}", getId(), repository.name);
                    }

                    @Override
                    public void buildProcessComplete() {
                        logger.info("Build {} finished", getId());
                    }
                });
                for (BuildProgressListener buildProgressListener : listeners) {
                    builder.addBuildProgressListener(buildProgressListener);
                }

                // initialize builder
                builder.init(repo, repoDir);

                // run checkout step
                boolean ok = checkoutRepoStep.execute();
                if(!ok) {
                    continue;
                }

                // run builder specific steps
                List<File> results = builder.build(repo, repoDir);
                if(results != null) {
                    for (File file : results) {
                        File dest = new File(archive, file.getName());
                        FileUtil.copy(file, dest);
                    }
                }
            } catch (NoBuilderFoundException e) {
                logger.warn(e.getLocalizedMessage());
            } catch (InterruptedException ie) {
                logger.info("Build has been interrupted");
            } catch (Exception e) {
                // TODO save the exception, so that we can tell the user what happened
                logger.error("Couldn't build " + repo + "[" + repo.id + "]", e);
                this.status = STATUS.FAILED;
            }
        }
        if(status == STATUS.RUNNING) {
            this.status = STATUS.FINISHED;
        }

        // create build archive files (zip, tar.xz)
        createBuildArchiveFile(archive);

        buildResult.status = this.status.name();
        buildResultDao.update(buildResult);

        for (BuildProgressListener buildProgressListener : listeners) {
            buildProgressListener.buildProcessComplete();
        }

        return buildResult;
    }

    private CheckoutRepo createCheckoutStep(RepositoryDTO repo, File repoDir, CommitDTO commit) {
        CheckoutRepo checkoutRepo = new CheckoutRepo(repo, repoDir);
        checkoutRepo.setScms(scms);
        checkoutRepo.setCommit(commit);
        checkoutRepo.setCommitDao(commitDao);
        checkoutRepo.setRepoArchive(repoArchive);
        return checkoutRepo;
    }

    private void createBuildArchiveFile(File archiveDir) throws Exception {
        File archiveFile = new File(workspace, "archive.zip");
        Zip.zipFolder(archiveDir.getAbsolutePath(), archiveFile.getAbsolutePath());
    }

    private List<BuildStepDTO> toBuildStepDTOs(List<BuildStep<?>> buildSteps, ProjectBuildDTO projectBuild) {
        List<BuildStepDTO> dtos = new ArrayList<>();
        for (int i = 0; i < buildSteps.size(); i++) {
            BuildStep<?> step = buildSteps.get(i);
            BuildStepDTO dto = step.toDTO(i);
            dto.projectBuildId = projectBuild.id;
            dtos.add(dto);
        }
        return dtos;
    }

    public STATUS getStatus() {
        return status;
    }

    public boolean isFinished() {
        return futureBuildResult.isDone();
    }

    public void setFutureBuildResult(Future<BuildResultDTO> futureBuildResult) {
        this.futureBuildResult = futureBuildResult;
    }

    public void addBuildProgressListener(BuildProgressListener bpl) {
        listeners.add(bpl);
        if(builder != null) {
            builder.addBuildProgressListener(bpl);
        }
    }

    public void removeBuildProgressListener(BuildProgressListener bpl) {
        listeners.remove(bpl);
        if(builder != null) {
            builder.removeBuildProgressListener(bpl);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RepositoryDTO> getRepositories() {
        List<RepositoryDTO> repos = new ArrayList<>();
        for (CommitDTO commit : version.commits) {
            repos.add(commit.repository);
        }
        return repos;
    }

    public VersionDTO getVersion() {
        return version;
    }

    /**
     * Cancels this build process. If the build is running, the thread is interrupted. The workspace directory will be deleted.
     *
     * @return false if the process could not be cancelled, typically because it has already completed normally; true otherwise
     */
    public String cancel() {
        this.status = STATUS.CANCELLED;
        this.buildResult.status = STATUS.CANCELLED.toString();
        buildResultDao.update(buildResult);
        boolean futureCancelled = futureBuildResult.cancel(true);
        if (workspace.exists()) {
            try {
                FileUtil.deleteRecursively(workspace);
            } catch (IOException e) {
                logger.error("Couldn't delete workspace directory {}", workspace, e);
            }
        }
        if(futureCancelled){
        	return this.repoId;
        } else {return null;}
    }

    public BuildResultDTO getBuildResultDTO() {
        return this.buildResult;
    }
}
