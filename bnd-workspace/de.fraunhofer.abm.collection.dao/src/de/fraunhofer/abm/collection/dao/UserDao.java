package de.fraunhofer.abm.collection.dao;

import java.util.List;

import de.fraunhofer.abm.domain.UserDTO;

public interface UserDao {

	public boolean checkExists(String name);
	
	public boolean checkEmailExists(String email);

	public boolean checkApproved(String name);

	public boolean checkLocked(String name);

	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token);

	public void updateUser(String username, String firstname, String lastname, String email, String affiliation);

	public void updateUserPassword(String username, String saltHashPassword);

	public void deleteUser(String username);

	public String approveToken(String name, String token);
	
	public List<UserDTO> getAllUsers(int isApproved, String adminuser);
	
	public UserDTO getUserInfo(String username);
	
	public void lockunlockUser(String username,String isLock);

	public void updateRole(String username, String role);
	
	public String getEmailId(String username);
	
	public String getUserToken(String name);
	
	public void deleteUserResetToken(String username);
	
}