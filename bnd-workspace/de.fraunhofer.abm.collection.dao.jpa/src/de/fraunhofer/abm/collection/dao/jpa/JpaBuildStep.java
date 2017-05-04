package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.fraunhofer.abm.domain.BuildStepDTO;

@Entity(name="build_step")
public class JpaBuildStep {

    @Id
    @Column
    public String id;

    @Column
    public long idx;

    @Column
    public String name;

    @Column
    public String status;

    @Column(length=1024000)
    public String stdout;

    @Column(length=1024000)
    public String stderr;

    @ManyToOne
    public JpaProjectBuild projectBuild;

    public static JpaBuildStep fromDTO(BuildStepDTO dto) {
        JpaBuildStep jpa = new JpaBuildStep();
        jpa.id = dto.id;
        jpa.idx = dto.idx;
        jpa.name = dto.name;
        jpa.status = dto.status;
        jpa.stdout = dto.stdout;
        jpa.stderr = dto.stderr;
        return jpa;
    }

    public BuildStepDTO toDTO() {
        BuildStepDTO dto = new BuildStepDTO();
        dto.id = this.id;
        dto.idx = this.idx;
        dto.name = this.name;
        dto.status = this.status;
        dto.stdout = this.stdout;
        dto.stderr = this.stderr;
        dto.projectBuildId = this.projectBuild.id;
        return dto;
    }
}
