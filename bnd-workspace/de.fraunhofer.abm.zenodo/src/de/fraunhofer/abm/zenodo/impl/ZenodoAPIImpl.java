package de.fraunhofer.abm.zenodo.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.zenodo.Deposition;
import de.fraunhofer.abm.zenodo.DepositionFile;
import de.fraunhofer.abm.zenodo.FileMetadata;
import de.fraunhofer.abm.zenodo.Metadata;
import de.fraunhofer.abm.zenodo.ZenodoAPI;

@Component
public class ZenodoAPIImpl implements ZenodoAPI {

	
	static Map<String, String> header = new HashMap<>();
	
		
	@Override
	public boolean test() {
		
		
		return false;
	}


	@Override
	public Deposition getDeposition(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Deposition> getDepositions() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Deposition updateDeposition(Deposition deposition) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteDeposition(Integer id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Deposition createDeposition(Metadata m) throws UnsupportedOperationException, IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<DepositionFile> getFiles(Integer depositionId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DepositionFile uploadFile(FileMetadata f, Integer depositionId)
			throws UnsupportedOperationException, IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean discard(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	

}
