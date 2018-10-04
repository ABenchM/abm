package de.fraunhofer.abm.collection.dao;

import de.fraunhofer.abm.domain.UserDTO;

public interface UserDao {

	public boolean checkExists(String name);

	public boolean checkApproved(String name);

	public void addUser(String username, String firstname, String lastname, String email, String affiliation, String password, String token);

	//Method declaration to get the user details Added by mac24079 03-10-2018
	public UserDTO getUserDetails(String currentUser);
	public void update(UserDTO user);
	public void deleteUser(String currentUser);
	
	public String approveToken(String name, String token);
}
