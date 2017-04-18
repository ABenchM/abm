package de.fraunhofer.abm.builder.docker.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.AbstractBuildStep;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.domain.RepositoryDTO;

public abstract class AbstractDockerStep<T> extends AbstractBuildStep<T> {

    private static final transient Logger logger = LoggerFactory.getLogger(AbstractDockerStep.class);

    private ExecutorService executor;

    public AbstractDockerStep(RepositoryDTO repo, ExecutorService executor) {
        super(repo);
        this.executor = executor;
    }

    protected Result exec(String cmd, File dir) throws IOException, InterruptedException {
        logger.debug("Executing command [{}] in directory {}", cmd, dir.getAbsolutePath());
        String[] env = {};
        String[] _cmd = cmd.split(" ");
        Process p = Runtime.getRuntime().exec(_cmd, env, dir);
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        executor.submit(new StreamRedirectThread(p.getInputStream(), stdout));
        executor.submit(new StreamRedirectThread(p.getErrorStream(), stderr));
        Result result = new Result();
        result.exitValue = p.waitFor();
        result.stdout = BuildUtils.toString(stdout);
        result.stderr = BuildUtils.toString(stderr);
        return result;
    }

    public static class Result {
        public int exitValue;
        public String stdout = "";
        public String stderr = "";
    }
}
