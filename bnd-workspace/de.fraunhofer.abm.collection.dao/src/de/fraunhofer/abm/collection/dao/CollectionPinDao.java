package de.fraunhofer.abm.collection.dao;

import java.util.List;

public interface CollectionPinDao {
	
	public List<String> findPins(String user);
	public boolean checkExists(String user, String id);
	public void addPin(String user, String id);
	public void dropPin(String user, String id);
}
