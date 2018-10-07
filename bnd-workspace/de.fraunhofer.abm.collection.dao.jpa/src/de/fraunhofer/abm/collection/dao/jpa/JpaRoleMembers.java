package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import de.fraunhofer.abm.domain.RoleMembersDTO;

@Entity(name = "role_members")
public class JpaRoleMembers {

	
	
	@Column
	public String member_parent;
	
	@Id
	@Column
	public String member_member;
	
	@Column
 	public Integer member_is_basic;
	
	
	
	public static JpaRoleMembers fromDTO(RoleMembersDTO dto) {
		JpaRoleMembers rolemember = new JpaRoleMembers();
		rolemember.member_parent = dto.memberparent;
 		rolemember.member_member= dto.member;
 		rolemember.member_is_basic = dto.isbasic;
        return rolemember;

}
	public RoleMembersDTO toDTO() {
		RoleMembersDTO rolemember = new RoleMembersDTO();
		rolemember.memberparent= this.member_parent;
 		rolemember.member = this.member_member;
 		rolemember.isbasic = this.member_is_basic;
 		return rolemember;
	}

}
