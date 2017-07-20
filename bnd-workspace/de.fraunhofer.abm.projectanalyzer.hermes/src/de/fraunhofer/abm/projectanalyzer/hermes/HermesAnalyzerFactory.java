package de.fraunhofer.abm.projectanalyzer.hermes;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzerFactory;

@Component
public class HermesAnalyzerFactory implements ProjectAnalyzerFactory {

	private String type = null;
	
    @Override
    public ProjectAnalyzer createNewAnalyzer() {
        return new HermesAnalyzer();
    }
    
    @Override
    public String getType(){
    	return type;
    }

    @Activate
    public void activate() {
        System.out.println("Activate hermes analyzer");
    }
}
