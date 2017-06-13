package de.fraunhofer.abm.projectanalyzer.size;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.FileVisitorAdapter;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;

public class SizeAnalyzer implements ProjectAnalyzer {

    private static final transient Logger logger = LoggerFactory.getLogger(SizeAnalyzer.class);

    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
        List<RepositoryPropertyDTO> properties = new ArrayList<>();
        int count = countFiles(directory);
        RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
        prop.repositoryId = repo.id;
        prop.name = "files";
        prop.value = Integer.toString(count);
        prop.description = "Number of files in the project";
        properties.add(prop);
        return properties;
    }

    private int count = 0;
    private int countFiles(File directory) {
        directory.toPath();
        try {
            Files.walkFileTree(directory.toPath(), new FileVisitorAdapter<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File f = file.toFile();
                    if(f.isFile()) {
                        count++;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Couldn't traverse file tree " + directory.getAbsolutePath() + " to determine programming languages", e);
        }
        return count;
    }
}
