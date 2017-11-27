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
	public String password;

	@Column
	public int approved;

	@Column
	private String token;

	public static JpaUser fromDTO(UserDTO dto) {
		JpaUser user = new JpaUser();
		user.name = dto.name;
		user.password = dto.password;
		user.approved = ((dto.approved) ? 1 : 0);
		user.token = dto.token;
		return user;
	}

	public UserDTO toDTO() {
		UserDTO user = new UserDTO();
		user.name = this.name;
		user.password = this.password;
		user.approved = (this.approved == 1);
		user.token = this.token;
		return user;
	}
}
