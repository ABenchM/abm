package de.fraunhofer.abm.projectanalyzer.license;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.projectanalyzer.license.LicenseAnalyzer.LICENSE;

public class LicenseStore extends HashMap<LICENSE, Set<String>> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final transient Logger logger = LoggerFactory.getLogger(LicenseStore.class);
    private static final LicenseStore instance = new LicenseStore();

    // disallow object creation, this is a singleton
    private LicenseStore() {
        loadLicenseFiles();
    }

    public static LicenseStore getInstance() {
        return instance;
    }

    private void loadLicenseFiles() {
        loadFile(LICENSE.APACHE2,       "Apache_License_2.0.txt");
        loadFile(LICENSE.APACHE2,       "Apache_Notice.txt");
        loadFile(LICENSE.BSD2CLAUSE,    "BSD_2_Clause.txt");
        loadFile(LICENSE.BSD3CLAUSE,    "BSD_3_Clause.txt");
        loadFile(LICENSE.CDDL,          "CDDL.txt");
        loadFile(LICENSE.EPL,           "EPL.txt");
        loadFile(LICENSE.GPL2,          "GPL2.txt");
        loadFile(LICENSE.GPL3,          "GPL3.txt");
        loadFile(LICENSE.LGPL21,        "LGPL21.txt");
        loadFile(LICENSE.LGPL3,         "LPGL3.txt");
        loadFile(LICENSE.MIT,           "MIT.txt");
        loadFile(LICENSE.MPL11,         "MPL11.txt");
        loadFile(LICENSE.MPL20,         "MPL20.txt");
    }

    private void loadFile(LICENSE license, String filename) {
        try(InputStream in = getClass().getResourceAsStream("/resources/" + filename)) {
            String licenseText = IO.readString(in);
            Set<String> licenseTexts = Optional.ofNullable(get(license)).orElse(new HashSet<>());
            licenseTexts.add(licenseText);
            put(license, licenseTexts);
        } catch (IOException e) {
            logger.error("Couldn't read license file "+filename+" from bundle", e);
        }
    }
}
