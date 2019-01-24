package de.fraunhofer.abm.collection.dao.jpa;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.fraunhofer.abm.domain.ProjectObjectDTO;

@Entity(name="project")
public class JpaProject {
	
	    @Id
	    @Column
	    public String id;

	    @Column(name = "version_id")
	    public String version_id;

	    @Column(name = "project_id")
	    public String project_id;

	    @Column(name = "source")
	    public String source;


	    public static JpaProject fromDTO(ProjectObjectDTO dto) {
	        JpaProject jpa = new JpaProject();
	        jpa.id = dto.id;
	        jpa.project_id = dto.project_id;
	        jpa.version_id = dto.version_id;
	        jpa.source = dto.source;
	        return jpa;
	    }

	    public ProjectObjectDTO toDTO() {
	        ProjectObjectDTO dto = new ProjectObjectDTO();
	        dto.id = this.id;
	        dto.project_id = this.project_id;
	        dto.version_id = this.version_id;
	        dto.source = this.source;
	        return dto;
	    }

}
