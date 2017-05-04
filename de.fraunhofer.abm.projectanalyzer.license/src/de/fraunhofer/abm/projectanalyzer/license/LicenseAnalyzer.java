package de.fraunhofer.abm.projectanalyzer.license;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;

public class LicenseAnalyzer implements ProjectAnalyzer {

    enum LICENSE {
        APACHE2,
        BSD2CLAUSE,
        BSD3CLAUSE,
        CDDL,
        EPL,
        GPL2,
        GPL3,
        LGPL21,
        LGPL3,
        MIT,
        MPL11,
        MPL20,
        UNKNOWN
    }

    private static final List<String> files = new ArrayList<>();
    static {
        files.add("COPYING");
        files.add("LEGAL");
        files.add("LICENSE");
        files.add("LICENCE");
        files.add("README");
    }

    private static final List<String> extensions = new ArrayList<>();
    static {
        extensions.add("txt");
        extensions.add("md");
    }

    private static final Map<String, LICENSE> searchStrings = new HashMap<>();
    static {
        searchStrings.put("Apache License.*?Version 2.0", LICENSE.APACHE2);
        searchStrings.put("GNU LESSER GENERAL PUBLIC LICENSE.*?Version 2.1", LICENSE.LGPL21);
        searchStrings.put("GNU GENERAL PUBLIC LICENSE.*?Version 2", LICENSE.GPL2);
        searchStrings.put("GNU GENERAL PUBLIC LICENSE.*?Version 3", LICENSE.GPL3);
    }

    private LICENSE license;

    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
        List<RepositoryPropertyDTO> properties = new ArrayList<>();

        determineLicense(directory);
        RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
        prop.repositoryId = repo.id;
        prop.name = "license";
        prop.value = license.name();
        prop.description = "License";
        properties.add(prop);

        return properties;
    }

    private void determineLicense(File directory) {
        license = new LicenseFileScanner(directory).determineLicense();

        //        directory.toPath();
        //        try {
        //            Files.walkFileTree(directory.toPath(), new FileVisitorAdapter<Path>() {
        //                @Override
        //                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        //                    File f = file.toFile();
        //                    if(f.isFile() && matchesFileName(f)) {
        //
        //                    }
        //                    return FileVisitResult.CONTINUE;
        //                }
        //
        //                private boolean matchesFileName(File f) {
        //                    for (String name : files) {
        //                        // check files without extension
        //                        if(f.getName().equalsIgnoreCase(name)) {
        //                            return true;
        //                        }
        //
        //                        // check files with extension
        //                        for (String ext : extensions) {
        //                            String filename = name + '.' + ext;
        //                            if(f.getName().equalsIgnoreCase(filename)) {
        //                                return true;
        //                            }
        //                        }
        //                    }
        //                    return false;
        //                }
        //            });
        //        } catch (IOException e) {
        //            logger.error("Couldn't traverse file tree " + directory.getAbsolutePath() + " to determine programming languages", e);
        //        }
    }
}
