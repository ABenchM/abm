package de.fraunhofer.abm.collection.dao;

import java.util.List;

public interface FilterPinDao {
	
	public List<String> findPins(String user);
	public void addPin(String user, String id);
	public void dropPin(String user, String id);
}
