package de.fraunhofer.abm.projectanalyzer.hermes;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.domain.FilterAppDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalyzer.api.ProjectAnalyzer;



public class HermesAnalyzer implements ProjectAnalyzer {

	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesAnalyzer.class);
	private static Map<String, String> Qnamemap = new HashMap<>();
	
	static{
		Qnamemap.put("org.opalj.hermes.queries.Metrics","Metrics");
		Qnamemap.put("org.opalj.hermes.queries.FieldAccessStatistics","FieldAccessStatistics");
		Qnamemap.put("org.opalj.hermes.queries.TrivialReflectionUsage","TrivialReflectionUsage");
		Qnamemap.put("org.opalj.hermes.queries.BytecodeInstructions","BytecodeInstructions");
		Qnamemap.put("org.opalj.hermes.queries.RecursiveDataStructures","RecursiveDataStructures");
		Qnamemap.put("org.opalj.hermes.queries.MethodsWithoutReturns","MethodsWithoutReturns");
		Qnamemap.put("org.opalj.hermes.queries.DebugInformation","DebugInformation");
		Qnamemap.put("org.opalj.hermes.queries.FanInFanOut","FanInFanOut");
		Qnamemap.put("org.opalj.hermes.queries.GUIAPIUsage","GUIAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.ClassLoaderAPIUsage","ClassLoaderAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.JavaCryptoArchitectureUsage","JavaCryptoArchitectureUsage");
		Qnamemap.put("org.opalj.hermes.queries.MethodTypes","MethodTypes");
		Qnamemap.put("org.opalj.hermes.queries.ReflectionAPIUsage","ReflectionAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.SystemAPIUsage","SystemAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.ThreadAPIUsage","ThreadAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.UnsafeAPIUsage","UnsafeAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.JDBCAPIUsage","JDBCAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.BytecodeInstrumentationAPIUsage","BytecodeInstrumentationAPIUsage");
		Qnamemap.put("org.opalj.hermes.queries.SizeOfInheritanceTree","SizeOfInheritanceTree");
		Qnamemap.put("org.opalj.hermes.queries.ClassFileVersion","ClassFileVersion");
	}
	
	private void UpdateFilterconf(List<FilterAppDTO> filterset)
	{
		
	}
	
	private void createFilter(List<FilterAppDTO> filterset)
	{
		
	}
	
    @Override
    public List<RepositoryPropertyDTO> analyze(RepositoryDTO repo, File directory) {
        // TODO Auto-generated method stub
        return null;
    }

}
