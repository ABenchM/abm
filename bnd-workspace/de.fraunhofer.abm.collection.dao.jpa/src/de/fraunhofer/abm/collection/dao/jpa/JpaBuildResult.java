package de.fraunhofer.abm.collection.dao.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.fraunhofer.abm.domain.BuildResultDTO;

@Entity(name="build_result")
public class JpaBuildResult {

    @Id
    @Column
    public String id;

    @Column
    public Date date;

    @Column
    public String dir;

    @Column
    public String status;

    @Column
    public String error;

    @Column(length=1024000)
    public String stackTrace;

    @Column
    public String versionId;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="buildResult", cascade=CascadeType.ALL)
    public List<JpaProjectBuild> projectBuilds = new ArrayList<>();

    public static JpaBuildResult fromDTO(BuildResultDTO dto) {
        JpaBuildResult jpa = new JpaBuildResult();
        jpa.id = dto.id;
        jpa.date = dto.date;
        jpa.dir = dto.dir;
        jpa.status = dto.status;
        jpa.error = dto.error;
        jpa.stackTrace = dto.stackTrace;
        jpa.versionId = dto.versionId;
        jpa.projectBuilds = dto.projectBuilds.stream()
                .map(JpaProjectBuild::fromDTO)
                .map(projectBuild -> {
                    projectBuild.buildResult = jpa;
                    return projectBuild;
                })
                .collect(Collectors.toList());
        return jpa;
    }

    public BuildResultDTO toDTO() {
        BuildResultDTO dto = new BuildResultDTO();
        dto.id = this.id;
        dto.date = this.date;
        dto.dir = this.dir;
        dto.status = this.status;
        dto.error = this.error;
        dto.stackTrace = this.stackTrace;
        dto.versionId = this.versionId;
        dto.projectBuilds = this.projectBuilds.stream()
                .map(JpaProjectBuild::toDTO)
                .map(projectBuild -> {
                    projectBuild.buildResultId = dto.id;
                    return projectBuild;
                })
                .collect(Collectors.toList());
        return dto;
    }
}
