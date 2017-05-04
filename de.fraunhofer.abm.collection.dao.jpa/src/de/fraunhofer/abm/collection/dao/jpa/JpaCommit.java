package de.fraunhofer.abm.collection.dao.jpa;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.fraunhofer.abm.domain.CommitDTO;

@Entity(name="commit")
public class JpaCommit {

    @Id
    @Column
    public String id;

    @Column
    public String commit;

    @Column
    public String message;

    @Column(name="creation_date")
    public Date creationDate;

    @ManyToOne
    public JpaVersion version;

    @ManyToOne(cascade= {DETACH,MERGE,PERSIST,REFRESH})
    public JpaRepository repository;

    public static JpaCommit fromDTO(CommitDTO dto) {
        JpaCommit commit = new JpaCommit();
        commit.id = dto.id;
        commit.commit = dto.commitId;
        commit.message = dto.message;
        commit.creationDate = dto.creationDate;
        commit.repository = JpaRepository.fromDTO(dto.repository);
        return commit;
    }

    public CommitDTO toDTO() {
        CommitDTO dto = new CommitDTO();
        dto.id = this.id;
        dto.commitId = this.commit;
        dto.message = this.message;
        dto.creationDate = this.creationDate;
        dto.repository = this.repository.toDTO();
        dto.versionId = this.version.id;
        return dto;
    }
}
