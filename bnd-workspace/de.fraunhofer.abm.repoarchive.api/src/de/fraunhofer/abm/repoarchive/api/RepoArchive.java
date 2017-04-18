package de.fraunhofer.abm.repoarchive.api;

import java.io.File;
import java.io.IOException;

public interface RepoArchive {
    public boolean exists(String repoId);
    public void archive(String repoId, File repoDir) throws IOException;
    public void retrieve(String repoId, File targetDir) throws IOException;
}
