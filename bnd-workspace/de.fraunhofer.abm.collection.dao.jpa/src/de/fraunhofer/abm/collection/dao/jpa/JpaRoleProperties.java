package de.fraunhofer.abm.collection.dao.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.RolePropertiesDTO;

@Entity(name = "role_properties")
public class JpaRoleProperties implements Serializable {

	private static final long serialVersionUID = -2246847209888585945L;
	
	@Id
	@Column(name="property_role")
	public String username;
	
	@Id
	@Column(name="property_name")
	public String property_name;

	@Column(name="property_value")
	public String property_value;
	
	public static JpaRoleProperties fromDTO(RolePropertiesDTO dto) {
		JpaRoleProperties userRoleProp = new JpaRoleProperties();
		userRoleProp.username = dto.username;
		userRoleProp.property_name = dto.property_name;
		userRoleProp.property_value = dto.property_value;
		return userRoleProp;
	}

	public RolePropertiesDTO toDTO() {
		RolePropertiesDTO rolePropDTO = new RolePropertiesDTO();
		rolePropDTO.username = this.username;
		rolePropDTO.property_name = this.username;
		rolePropDTO.property_value = this.property_value;
		return rolePropDTO;
	}
}