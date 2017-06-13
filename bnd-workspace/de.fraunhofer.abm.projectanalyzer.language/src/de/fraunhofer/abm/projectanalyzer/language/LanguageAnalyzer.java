package de.fraunhofer.abm.projectanalyzer.language;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.FileVisitorAdapter;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;

public class LanguageAnalyzer implements ProjectAnalyzer {

    private static final transient Logger logger = LoggerFactory.getLogger(LanguageAnalyzer.class);
    private static Map<String, String> extensionsToLanguage = new HashMap<>();
    static {
        extensionsToLanguage.put("c", "c");
        extensionsToLanguage.put("cc", "c");
        extensionsToLanguage.put("h", "c");
        extensionsToLanguage.put("cpp", "cpp");
        extensionsToLanguage.put("groovy", "groovy");
        extensionsToLanguage.put("htm", "html");
        extensionsToLanguage.put("html", "html");
        extensionsToLanguage.put("js", "javascript");
        extensionsToLanguage.put("php", "php");
        extensionsToLanguage.put("py", "python");
        extensionsToLanguage.put("rb", "ruby");
        extensionsToLanguage.put("java", "java");
        extensionsToLanguage.put("scala", "scala");
        extensionsToLanguage.put("xml", "xml");
    }

    private Map<String, Integer> languageFilesCount = new HashMap<>();

    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
        List<RepositoryPropertyDTO> properties = new ArrayList<>();

        countFiles(directory);
        String language = getMostUsed();
        if(language != null) {
            RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
            prop.repositoryId = repo.id;
            prop.name = "language";
            prop.value = language;
            prop.description = "Programming language " + language;
            properties.add(prop);
        }

        return properties;
    }

    private String getMostUsed() {
        int max = 0;
        String mostUsed = null;
        for (Entry<String, Integer> languageCount : languageFilesCount.entrySet()) {
            String lang = languageCount.getKey();
            int count = languageCount.getValue();
            if(count > max) {
                max = count;
                mostUsed = lang;
            }
        }
        return mostUsed;
    }

    private void countFiles(File directory) {
        directory.toPath();
        try {
            Files.walkFileTree(directory.toPath(), new FileVisitorAdapter<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File f = file.toFile();
                    if(f.isFile()) {
                        String ext = getExtension(f);
                        String language = extensionsToLanguage.get(ext);
                        if(language != null) {
                            increaseLanguageCount(language);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                private void increaseLanguageCount(String language) {
                    Integer count = languageFilesCount.get(language);
                    languageFilesCount.put(language, Optional.ofNullable(count).orElse(0).intValue() + 1);
                }

                private String getExtension(File f) {
                    String name = f.getName();
                    int lastDot = name.lastIndexOf('.');
                    if(lastDot >= 0) {
                        return name.substring(lastDot+1, name.length());
                    } else {
                        return "";
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Couldn't traverse file tree " + directory.getAbsolutePath() + " to determine programming languages", e);
        }
    }
}
