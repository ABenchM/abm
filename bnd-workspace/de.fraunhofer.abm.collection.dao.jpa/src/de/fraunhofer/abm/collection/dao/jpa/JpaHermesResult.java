package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


import de.fraunhofer.abm.domain.HermesResultDTO;

@Entity(name="hermes_result")
public class JpaHermesResult {

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
    
    public static JpaHermesResult fromDTO(HermesResultDTO dto){

    	JpaHermesResult jpa = new JpaHermesResult();
    	jpa.id = dto.id;
    	jpa.date = dto.date;
        jpa.dir = dto.dir;
        jpa.status = dto.status;
        jpa.error = dto.error;
        jpa.stackTrace = dto.stackTrace;
        jpa.versionId = dto.versionId;
        
		return jpa;
    }
    
    public HermesResultDTO toDTO() {
    	HermesResultDTO dto = new HermesResultDTO();
        dto.id = this.id;
        dto.date = this.date;
        dto.dir = this.dir;
        dto.status = this.status;
        dto.error = this.error;
        dto.stackTrace = this.stackTrace;
        dto.versionId = this.versionId;
        return dto;
    }
	
}
