package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.fraunhofer.abm.domain.ResetTokenDTO;;

@Entity(name = "reset_token")
public class JpaResetToken {
	
	@Column
	public String username;

	@Column
	public String token;
	
	public static JpaResetToken fromDTO(ResetTokenDTO dto) {
		JpaResetToken jpa = new JpaResetToken();
		jpa.username = dto.username;
		jpa.token = dto.token;
		return jpa;
	}

	public ResetTokenDTO toDTO() {
		ResetTokenDTO dto = new ResetTokenDTO();
		dto.username = this.username;
		dto.token = this.token;
		return dto;
	}
}
