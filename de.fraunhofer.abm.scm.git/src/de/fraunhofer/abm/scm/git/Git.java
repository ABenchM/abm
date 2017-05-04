package de.fraunhofer.abm.scm.git;

import java.io.File;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.scm.api.SCM;

@Component
public class Git implements SCM {

    @Override
    public String clone(RepositoryDTO repo, File targetDir) throws Exception {
        CloneCommand cloneCommand = new CloneCommand().setURI(repo.repositoryUrl).setDirectory(targetDir);
        cloneCommand.call().close();

        Repository repository = new RepositoryBuilder().setWorkTree(targetDir).build();
        try {
            ObjectId headRef = repository.resolve("HEAD");
            String name = headRef.getName();
            return name;
        } finally {
            repository.close();
        }
    }

    @Override
    public void checkout(RepositoryDTO repo, File repoDir, String commitSha) throws Exception {
        Repository repository = new RepositoryBuilder().setWorkTree(repoDir).build();
        try {
            org.eclipse.jgit.api.Git git = new org.eclipse.jgit.api.Git(repository);
            CheckoutCommand cmd = git.checkout().setName(commitSha);
            cmd.call();
            git.close();
        } finally {
            repository.close();
        }
    }

    @Override
    public String update(RepositoryDTO repo, File repoDir) throws Exception {
        Repository repository = new RepositoryBuilder().setWorkTree(repoDir).build();
        try {
            org.eclipse.jgit.api.Git git = new org.eclipse.jgit.api.Git(repository);
            git.pull();
            git.close();
            ObjectId headRef = repository.resolve("HEAD");
            return headRef.getName();
        } finally {
            repository.close();
        }
    }

    @Override
    public String supports() {
        return RepositoryDTO.TYPE_GIT;
    }

}
