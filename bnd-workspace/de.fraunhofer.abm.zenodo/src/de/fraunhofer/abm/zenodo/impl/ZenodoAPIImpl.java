package de.fraunhofer.abm.zenodo.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.http.client.Base64;
import de.fraunhofer.abm.http.client.HttpUtils;
import de.fraunhofer.abm.util.AbmApplicationConstants;
import de.fraunhofer.abm.zenodo.API;
import de.fraunhofer.abm.zenodo.Deposition;
import de.fraunhofer.abm.zenodo.DepositionFile;
import de.fraunhofer.abm.zenodo.FileMetadata;
import de.fraunhofer.abm.zenodo.Metadata;
import de.fraunhofer.abm.zenodo.ZenodoAPI;
import com.mashape.unirest.http.Unirest;


@Component
public class ZenodoAPIImpl implements ZenodoAPI {

	
	static Map<String, String> header = new HashMap<>();
	 
	
	 private static String url = "https://sandbox.zenodo.org/";
	 private static String token = "HWiH1QCdIj81fj0a9vB9knBzfH8puk55NXiEZqkumpILavP2BHgKnjgUEyc9";
			
		
	@Override
	public boolean test() {
		System.out.println("Token value" + token);
		return false;
	}


	@Override
	public Deposition getDeposition(Integer id)  {
		
		// TODO Auto-generated method stub
		try {
		header = new HashMap<>();
		header.put("Authorization", "Bearer "+ token);
		String resp = HttpUtils.get(url+ API.Deposit.Entity , header, "UTF-8");
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return null;
	}


	@Override
	public List<Deposition> getDepositions() {
		
		
		
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
