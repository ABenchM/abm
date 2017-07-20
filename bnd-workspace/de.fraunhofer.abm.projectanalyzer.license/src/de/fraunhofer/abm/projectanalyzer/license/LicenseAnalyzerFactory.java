package de.fraunhofer.abm.projectanalyzer.license;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;

@Component
public class LicenseAnalyzerFactory implements ProjectAnalyzerFactory {

    private LicenseStore licenseStore;
    private String type = "license";

    @Override
    public ProjectAnalyzer createNewAnalyzer() {
        return new LicenseAnalyzer();
    }
    
    @Override
    public String getType(){
    	return type;
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
