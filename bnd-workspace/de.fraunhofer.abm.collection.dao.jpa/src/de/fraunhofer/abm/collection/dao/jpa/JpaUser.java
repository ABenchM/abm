package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.UserDTO;

@Entity(name = "user")
public class JpaUser {

	@Id
	@Column
	public String name;

	@Column
	public String firstname;

	@Column
	public String lastname;

	@Column
	public String email;

	@Column
	public String affiliation;

	@Column
	public String password;

	@Column
	public int approved;

	@Column
	public int locked;

	@Column(name="approval_token")
	public String token;

	public static JpaUser fromDTO(UserDTO dto) {
		JpaUser user = new JpaUser();
		user.name = dto.username;
		user.firstname = dto.firstname;
		user.lastname = dto.lastname;
		user.email = dto.email;
		user.affiliation = dto.affiliation;
		user.password = dto.password;
		user.approved = ((dto.approved) ? 1 : 0);
		user.locked = ((dto.locked) ? 1 : 0);
		user.token = dto.token;
		return user;
	}

	public UserDTO toDTO() {
		UserDTO user = new UserDTO();
		user.username = this.name;
		user.firstname = this.firstname;
		user.lastname = this.lastname;
		user.email = this.email;
		user.affiliation = this.affiliation;
		user.password = this.password;
		user.approved = (this.approved == 1);
		user.locked = (this.locked == 0);
		user.token = this.token;
		return user;
	}
}
