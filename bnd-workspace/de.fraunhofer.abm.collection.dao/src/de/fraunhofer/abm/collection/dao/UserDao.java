package de.fraunhofer.abm.collection.dao;

import java.util.List;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String name, String password, String token);

	public String approveToken(String name, String token);
	
	public void updateRole(String username, String role);
    public void deleteUser(String username);
	
	public void deleteUsers(List<String> usernames);
	
	public void lockunlockUser(String username,String isLock);
}