package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.fraunhofer.abm.domain.HermesStepDTO;

@Entity(name="hermes_step")
public class JpaHermesStep {

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
	public JpaHermesBuild hermesBuild;
	
	public static JpaHermesStep fromDTO(HermesStepDTO dto)
	{
		JpaHermesStep jpa = new JpaHermesStep();
		jpa.id = dto.id;
		jpa.idx = dto.idx;
		jpa.name = dto.name;
		jpa.status = dto.status;
		jpa.stdout = dto.stdout;
		jpa.stderr = dto.stderr;
		return jpa;
		
	}
	
	public HermesStepDTO toDTO()
	{
		HermesStepDTO dto = new HermesStepDTO();
		dto.id = this.id;
		dto.idx = this.idx;
		dto.name = this.name;
		dto.status = this.status;
		dto.stdout = this.stdout;
		dto.stderr = this.stderr;
		dto.hermesBuildId = this.hermesBuild.id;
		return dto;
		
	}
	
    
}
