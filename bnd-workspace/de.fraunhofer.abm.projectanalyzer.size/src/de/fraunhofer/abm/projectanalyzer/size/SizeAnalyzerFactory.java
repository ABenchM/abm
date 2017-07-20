package de.fraunhofer.abm.projectanalyzer.size;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;

@Component
public class SizeAnalyzerFactory implements ProjectAnalyzerFactory {

	private String type = "files";
	
    @Override
    public ProjectAnalyzer createNewAnalyzer() {
        return new SizeAnalyzer();
    }
    
    @Override
    public String getType(){
    	return type;
    }
}
