package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.UserDTO;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String name, String password, String token);

	public String approveToken(String name, String token);
	
	public List<UserDTO> getAllUsers(int isApproved);
	
}