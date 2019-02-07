package de.fraunhofer.abm.collection.dao.jpa;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.fraunhofer.abm.domain.ProjectObjectDTO;

@Entity(name="project")
public class JpaProject {
	
	    @Id
	    @Column
	    public String id;

	    @Column
	    public String project_id;

	    @Column
	    public String source;
	    
	    /*@Column
	    public String version_id;*/
	    
	    @ManyToOne
	    public JpaVersion version;

	    public static JpaProject fromDTO(ProjectObjectDTO dto) {
	        JpaProject jpa = new JpaProject();
	        jpa.id = dto.id;
	        jpa.project_id = dto.project_id;
	        jpa.source = dto.source;
	        //jpa.version_id = dto.version_id;
	        return jpa;
	    }

	    public ProjectObjectDTO toDTO() {
	        ProjectObjectDTO dto = new ProjectObjectDTO();
	        dto.id = this.id;
	        dto.project_id = this.project_id;
	        //dto.version_id = this.version_id;
	        dto.source = this.source;
	        return dto;
	    }

}
