package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Date;
import static javax.persistence.FetchType.LAZY;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.fraunhofer.abm.domain.CollectionDTO;

@Entity(name="collection")
public class JpaCollection {

    @Id
    @Column
    public String id;

    @Column
    public String user;

    @Column
    public String name;

    @Column
    public String description;
    
    @Column
    public int privateStatus;
    
    @Column
    public int isActive;
    
    @Column
    public Date creation_date;
    
    

    @OneToMany(fetch=LAZY, mappedBy="collection", cascade=CascadeType.ALL)
    @OrderBy("number")
    public List<JpaVersion> versions;

    public static JpaCollection fromDTO(CollectionDTO dto) {
        JpaCollection collection = new JpaCollection();
        collection.id = dto.id;
        collection.user = dto.user;
        collection.name = dto.name;
        collection.description = dto.description;
        collection.privateStatus = ((dto.privateStatus)? 1: 0);
        collection.isActive = ((dto.isActive) ? 1 : 0);
        collection.creation_date = dto.creation_date;
        collection.versions = dto.versions.stream()
                .map(JpaVersion::fromDTO)
                .map(version -> {
                    version.collection = collection;
                    return version;
                })
                .collect(Collectors.toList());
        return collection;
    }

    public CollectionDTO toDTO() {
        CollectionDTO collection = new CollectionDTO();
        collection.id = this.id;
        collection.user = this.user;
        collection.name = this.name;
        collection.description = this.description;
        collection.privateStatus = (this.privateStatus == 1);
        collection.isActive = (this.isActive == 1);
        collection.creation_date = this.creation_date;
        collection.versions = this.versions.stream()
                .map(JpaVersion::toDTO)
                .map(version -> {
                    version.collectionId = collection.id;
                    return version;
                })
                .collect(Collectors.toList());
        return collection;
    }
}
