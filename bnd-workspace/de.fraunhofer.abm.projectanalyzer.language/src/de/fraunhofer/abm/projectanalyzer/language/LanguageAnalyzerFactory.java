package de.fraunhofer.abm.projectanalyzer.language;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;

@Component
public class LanguageAnalyzerFactory implements ProjectAnalyzerFactory {

	private String type = "language";
	
    @Override
    public ProjectAnalyzer createNewAnalyzer() {
        return new LanguageAnalyzer();
    }
    
    @Override
    public String getType(){
    	return type;
    }
    
}
