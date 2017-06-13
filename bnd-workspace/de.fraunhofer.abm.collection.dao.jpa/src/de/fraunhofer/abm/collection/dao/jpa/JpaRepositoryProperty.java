package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

@Entity(name="repository_property")
public class JpaRepositoryProperty {

    @Id
    @Column
    public String id;

    @Column(nullable=false)
    public String name;

    @Column(nullable=false)
    public String value;

    @Column(nullable=true)
    public String description;

    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="repository_id", nullable=false)
    public JpaRepository repository;

    public static JpaRepositoryProperty fromDTO(RepositoryPropertyDTO dto) {
        JpaRepositoryProperty jpa = new JpaRepositoryProperty();
        jpa.id = dto.id;
        jpa.name = dto.name;
        jpa.value = dto.value;
        jpa.description = dto.description;
        return jpa;
    }

    public RepositoryPropertyDTO toDTO() {
        RepositoryPropertyDTO dto = new RepositoryPropertyDTO();
        dto.id = this.id;
        dto.repositoryId = this.repository.id;
        dto.name = this.name;
        dto.value = this.value;
        dto.description = this.description;
        return dto;
    }
}
