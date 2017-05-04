package de.fraunhofer.abm.repoarchive.local;

import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.repoarchive.api.RepoArchive;
import de.fraunhofer.abm.util.FileUtil;

@Designate(ocd = Configuration.class, factory=false)
@Component(immediate=true)
public class LocalRepoArchive implements RepoArchive {
    private static final transient Logger logger = LoggerFactory.getLogger(LocalRepoArchive.class);

    private File directory;

    @Override
    public boolean exists(String repoId) {
        return new File(directory, repoId).exists();
    }

    @Override
    public void archive(String repoId, File repoDir) throws IOException {
        File target = new File(directory, repoId);
        target.mkdirs();
        File source = repoDir;
        copy(source, target);
    }

    @Override
    public void retrieve(String repoId, File targetDir) throws IOException {
        File source = new File(directory, repoId);
        File target = targetDir;
        target.mkdirs();
        copy(source, target);
    }

    private void copy(File source, File target) throws IOException {
        logger.info("Copying {} to {}", source.getAbsolutePath(), target.getAbsolutePath());
        FileUtil.copy(source, target);
    }

    @Activate
    public void activate(Configuration config) {
        initDirectory(config.directory());
    }

    @Deactivate
    public void deactivate() {
    }

    private void initDirectory(String path) {
        directory = new File(path);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        logger.info("Using repo archive directory {}", directory.getAbsolutePath());
    }
}
