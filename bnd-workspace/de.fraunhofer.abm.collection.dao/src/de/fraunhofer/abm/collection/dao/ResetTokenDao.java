package de.fraunhofer.abm.collection.dao;

public interface ResetTokenDao {
	
	public void addToken(String name, String token);
	
	public void resetPassword(String name, String token, String password);

}
