package de.fraunhofer.abm.collection.dao.jpa;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.fraunhofer.abm.domain.ProjectBuildDTO;

@Entity(name="project_build")
public class JpaProjectBuild {

    @Id
    @Column
    public String id;

    @OneToMany(fetch=LAZY, mappedBy="projectBuild", cascade=CascadeType.ALL)
    @OrderBy("idx ASC")
    public List<JpaBuildStep> buildSteps = new ArrayList<>();

    @Column
    public String repository;

    @ManyToOne
    public JpaBuildResult buildResult;

    public static JpaProjectBuild fromDTO(ProjectBuildDTO dto) {
        JpaProjectBuild jpa = new JpaProjectBuild();
        jpa.id = dto.id;
        jpa.repository = dto.repository.id;
        jpa.buildSteps = dto.buildSteps.stream().map(JpaBuildStep::fromDTO)
                .map(step -> {
                    step.projectBuild = jpa;
                    return step;
                }).collect(Collectors.toList());
        return jpa;
    }

    public ProjectBuildDTO toDTO() {
        ProjectBuildDTO dto = new ProjectBuildDTO();
        dto.id = this.id;
        dto.repositoryId = repository;
        dto.buildSteps = this.buildSteps.stream().map(JpaBuildStep::toDTO)
                .map(step -> {
                    step.projectBuildId = dto.id;
                    return step;
                }).collect(Collectors.toList());
        return dto;
    }
}
