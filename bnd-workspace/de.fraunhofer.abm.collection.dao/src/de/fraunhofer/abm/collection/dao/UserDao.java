package de.fraunhofer.abm.collection.dao;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token);

	public String approveToken(String name, String token);
}
