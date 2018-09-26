package de.fraunhofer.abm.collection.dao;

import java.util.List;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token);

	public void deleteUsers(List<String> usernames);

	public String approveToken(String name, String token);
	
	public String getUsername(String usernameemail);
}