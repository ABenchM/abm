package de.fraunhofer.abm.collection.dao;

public interface ResetTokenDao {
	
	public void addToken(String name, String token,Long time);
	
	public void resetPassword(String name, String token, String password);

	public void updateUserPassword(String name, String password);
	
	public boolean checkExists(String name);
	 
	public void updateToken(String name,String token,Long time);
	

}
