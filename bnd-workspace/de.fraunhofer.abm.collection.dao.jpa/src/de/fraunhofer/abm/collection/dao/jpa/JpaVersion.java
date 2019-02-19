package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.VersionDTO;

@Entity(name="version")
public class JpaVersion {

    @Id
    @Column
    public String id;
    
    @Column
    public String name;
    
    @Column
    public String derivedFrom;

    @Column(name="creation_date")
    public Date creationDate;

    @Column
    public int number;

    @Column
    public String comment;

    @Column
    public boolean frozen;
    
    @Column
    public boolean privateStatus;
    
    @Column
    public boolean filtered;

    @ManyToOne
    public JpaCollection collection;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="version", cascade=CascadeType.ALL)
    public List<JpaCommit> commits;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="version", cascade=CascadeType.ALL)
    public List<JpaProject> projects;

    public static JpaVersion fromDTO(VersionDTO dto) {
        JpaVersion version = new JpaVersion();
        version.id = dto.id;
        version.creationDate = dto.creationDate;
        version.number = dto.number;
        version.name = dto.name;
        version.derivedFrom = dto.derivedFrom;
        version.comment = dto.comment;
        version.frozen = dto.frozen;
        version.privateStatus = dto.privateStatus;
        version.filtered = dto.filtered;
        version.projects = dto.projects.stream()
                .map(JpaProject::fromDTO)
                .map(project -> {
                    project.version= version;
                    return project;
                })
                .collect(Collectors.toList());
        return version;
    }

    public VersionDTO toDTO() {
        VersionDTO version = new VersionDTO();
        version.id = this.id;
        version.creationDate = this.creationDate;
        version.number = this.number;
        version.name = this.name;
        version.derivedFrom = this.derivedFrom;
        version.comment = this.comment;
        version.frozen = this.frozen;
        version.privateStatus = this.privateStatus;
        version.filtered = this.filtered;
        version.collectionId = this.collection.id;
        version.projects = this.projects.stream()
                .map(JpaProject::toDTO)
                .map(proj -> {
                	proj.version_id = version.id;
                    return proj;
                })
                .collect(Collectors.toList());
        //Collections.sort(version.projects, new RepositoryNameComparator());
        return version;
    }

    static class RepositoryNameComparator implements Comparator<CommitDTO> {
        @Override
        public int compare(CommitDTO o1, CommitDTO o2) {
            return o1.repository.name.compareTo(o2.repository.name);
        }

    }
}