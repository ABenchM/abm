package de.fraunhofer.abm.collection.dao.jpa;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.FilterStatusDTO;

@Entity(name="filter_status")
public class JpaFilterStatus {
	
	
	@Id
	@Column
	public String id;
	
	@Column
	public String filtername;
	
	@Column
	public boolean activate;
	
	@Column
    public	String versionid;

	public static JpaFilterStatus fromDTO(FilterStatusDTO dto) {
        JpaFilterStatus jpa = new JpaFilterStatus();
        jpa.id = dto.id;
        jpa.filtername = dto.filtername;
        jpa.activate = dto.activate;
        jpa.versionid = dto.versionid;
        return jpa;
    }
	
	public FilterStatusDTO toDTO()
	{
		FilterStatusDTO dto = new FilterStatusDTO();
		dto.id = this.id;
		dto.filtername = this.filtername;
		dto.activate = this.activate;
		dto.versionid = this.versionid;
		
		return dto;
	}
	
}
