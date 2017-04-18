package de.fraunhofer.abm.projectanalyzer.license;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;

@Component
public class LicenseAnalyzerFactory implements ProjectAnalyzerFactory {

    private LicenseStore licenseStore;

    @Override
    public ProjectAnalyzer createNewAnalyzer() {
        return new LicenseAnalyzer();
    }

    @Activate
    public void activate() {
        licenseStore = LicenseStore.getInstance();
    }

    @Deactivate
    public void deactivate() {
        licenseStore.clear();
        licenseStore = null;
    }
}
