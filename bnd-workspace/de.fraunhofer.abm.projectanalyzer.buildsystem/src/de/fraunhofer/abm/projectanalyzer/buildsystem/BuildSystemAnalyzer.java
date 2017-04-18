package de.fraunhofer.abm.projectanalyzer.buildsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;

public class BuildSystemAnalyzer implements ProjectAnalyzer {

    private static final transient Logger logger = LoggerFactory.getLogger(BuildSystemAnalyzer.class);

    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
        List<RepositoryPropertyDTO> properties = new ArrayList<>();

        checkForMaven(properties, repo, directory);
        checkForAnt(properties, repo, directory);
        checkForGradle(properties, repo, directory);
        checkForSBT(properties, repo, directory);

        return properties;
    }

    private void checkForAnt(List<RepositoryPropertyDTO> properties, RepositoryDTO repo, File directory) {
        File buildXml = new File(directory, "build.xml");
        if(buildXml.exists()) {
            RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
            prop.repositoryId = repo.id;
            prop.name = "build.system";
            prop.value = "ant";
            prop.description = "Built with Apache Ant";
            properties.add(prop);
        }
    }

    private void checkForMaven(List<RepositoryPropertyDTO> properties, RepositoryDTO repo, File directory) {
        File pom = new File(directory, "pom.xml");
        if(pom.exists()) {
            RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
            prop.repositoryId = repo.id;
            prop.name = "build.system";
            prop.value = "maven";
            prop.description = "Built with Apache Maven";
            properties.add(prop);
        }
    }

    private void checkForGradle(List<RepositoryPropertyDTO> properties, RepositoryDTO repo, File directory) {
        File gradlew = new File(directory, "gradlew");
        File buildGradle = new File(directory, "build.gradle");
        if(gradlew.exists() || buildGradle.exists()) {
            RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
            prop.repositoryId = repo.id;
            prop.name = "build.system";
            prop.value = "gradle";
            prop.description = "Built with Gradle";
            properties.add(prop);
        }
    }

    private void checkForSBT(List<RepositoryPropertyDTO> properties, RepositoryDTO repo, File directory) {
        File buildSbt = new File(directory, "build.sbt");
        File buildProperties = new File(directory, "build.properties");
        if(buildSbt.exists()) {
            RepositoryPropertyDTO prop = new RepositoryPropertyDTO();
            prop.repositoryId = repo.id;
            prop.name = "build.system";
            prop.value = "sbt";
            prop.description = "Built with SBT";
            properties.add(prop);

            if(buildProperties.exists()) {
                Properties sbtProperties = new Properties();
                try(InputStream file = new FileInputStream(buildProperties)) {
                    sbtProperties.load(file);
                    String sbtVersion = sbtProperties.getProperty("sbt.version");
                    prop = new RepositoryPropertyDTO();
                    prop.repositoryId = repo.id;
                    prop.name = "sbt.version";
                    prop.value = sbtVersion;
                    prop.description = "SBT version";
                    properties.add(prop);
                } catch (Exception e) {
                    logger.error("Couldn't read build.properties", e);
                }
            }
        }
    }
}
