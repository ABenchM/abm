package de.fraunhofer.abm.collection.dao.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.RoleMembersDTO;

@Entity(name = "role_members")
public class JpaRoleMembers implements Serializable {

	private static final long serialVersionUID = -2246847209888585945L;
	
	@Id
	@Column(name="member_parent")
	public String role;
	
	@Id
	@Column(name="member_member")
	public String username;

	@Column(name="member_is_basic")
	public int isBasic;
	
	public static JpaRoleMembers fromDTO(RoleMembersDTO dto) {
		JpaRoleMembers userRole = new JpaRoleMembers();
		userRole.role = dto.role;
		userRole.username = dto.username;
		userRole.isBasic = ((dto.isBasic) ? 1 : 0);
		return userRole;
	}

	public RoleMembersDTO toDTO() {
		RoleMembersDTO userRoleDTO = new RoleMembersDTO();
		userRoleDTO.role = this.username;
		userRoleDTO.username = this.username;
		userRoleDTO.isBasic = (this.isBasic == 1);
		return userRoleDTO;
	}
}
