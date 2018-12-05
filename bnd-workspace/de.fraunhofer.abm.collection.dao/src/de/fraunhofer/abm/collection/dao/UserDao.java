package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.UserDTO;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token);

	public void updateUser(String username, String firstname, String lastname, String email, String affiliation, String saltHashPassword);

	public String approveToken(String name, String token);
	
	public List<UserDTO> getAllUsers(int isApproved);
	
	public UserDTO getUserInfo(String username);
	
	public void lockunlockUser(String username,String isLock);
	
}