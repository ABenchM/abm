package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.ResetTokenDTO;;

@Entity(name = "reset_token")
public class JpaResetToken {
	
	@Id
	@Column
	public String username;

	@Column
	public String token;
	
	@Column
	public Long expired_period;
	
	
	public static JpaResetToken fromDTO(ResetTokenDTO dto) {
		JpaResetToken jpa = new JpaResetToken();
		jpa.username = dto.username;
		jpa.token = dto.token;
		jpa.expired_period = dto.expired_period;
		return jpa;
	}

	public ResetTokenDTO toDTO() {
		ResetTokenDTO dto = new ResetTokenDTO();
		dto.username = this.username;
		dto.token = this.token;
		dto.expired_period = this.expired_period;
		return dto;
	}
}
