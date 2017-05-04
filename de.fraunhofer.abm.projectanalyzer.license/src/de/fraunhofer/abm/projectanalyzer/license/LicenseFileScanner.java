package de.fraunhofer.abm.projectanalyzer.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.projectanalyzer.license.LicenseAnalyzer.LICENSE;

/**
 * Compares files in a directory with common license texts and determines the equality
 * with the help of the Levensthein distance to determine the license in a fuzzy way.
 *
 * Since the Levensthein distance uses n*m (n,m the 2 strings to compare) memory we only compare the first 1024 bytes.
 *
 * @author henni
 */
public class LicenseFileScanner {
    private static final transient Logger logger = LoggerFactory.getLogger(LicenseFileScanner.class);
    private File directory;

    public LicenseFileScanner(File directory) {
        super();
        this.directory = directory;
    }

    public LICENSE determineLicense() {
        List<File> candidates = getFileCandidates();
        List<LicenseProbability> results = new ArrayList<>();
        for (File file : candidates) {
            try(FileInputStream in = new FileInputStream(file)) {
                logger.debug("Checking for license text in {}", file.getAbsolutePath());
                String fileContent = IO.readString(in);
                List<LicenseProbability> probabilities = compareToLicenses(file.getName(), fileContent);
                results.addAll(probabilities);
            } catch (IOException e) {
                logger.error("Couldn't load file ["+file+"] to look for license text", e);
            }
        }

        LicenseProbability bestMatch = getBestMatch(results);
        if(bestMatch.probability > 80) {
            logger.debug("Best license match: {} {} {}", bestMatch.probability, bestMatch.license, bestMatch.file);
            return bestMatch.license;
        } else {
            return LICENSE.UNKNOWN;
        }
    }

    private LicenseProbability getBestMatch(List<LicenseProbability> results) {
        LicenseProbability best = new LicenseProbability(LICENSE.UNKNOWN, 0, "");
        for (LicenseProbability licenseProbability : results) {
            if(licenseProbability.probability > best.probability) {
                best = licenseProbability;
            }
        }
        return best;
    }

    private List<LicenseProbability> compareToLicenses(String file, String fileContent) {
        List<LicenseProbability> probabilities = new ArrayList<>();
        for (Entry<LICENSE, Set<String>> entry : LicenseStore.getInstance().entrySet()) {
            for (String licenseText : entry.getValue()) {
                LICENSE license = entry.getKey();
                int equality = StringComparison.percentageOfEquality(licenseText, fileContent);
                probabilities.add(new LicenseProbability(license, equality, file));
            }
        }
        return probabilities;
    }

    /**
     * Filters the directory for files, which are candidates to contain a license text
     * @return
     */
    private List<File> getFileCandidates() {
        File[] files = directory.listFiles();
        List<File> candidates = retainSmallFiles(files);
        return candidates;
    }

    /**
     * Filters an array of files for files, which are not larger than a defined size.
     * @param files
     * @return
     */
    private List<File> retainSmallFiles(File[] files) {
        List<File> smallFiles = new ArrayList<>();
        for (File file : files) {
            long ONEHUNDRED_KILOBYTE = 100 * 1024;
            if(file.isFile() && file.length() < ONEHUNDRED_KILOBYTE) {
                smallFiles.add(file);
            }
        }
        return smallFiles;
    }

    private class LicenseProbability {
        LICENSE license;
        int probability;
        String file;

        public LicenseProbability(LICENSE license, int probability, String file) {
            this.license = license;
            this.probability = probability;
            this.file = file;
        }
    }
}
