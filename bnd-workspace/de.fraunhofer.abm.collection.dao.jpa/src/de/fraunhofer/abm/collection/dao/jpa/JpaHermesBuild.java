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

import de.fraunhofer.abm.domain.HermesBuildDTO;

@Entity(name="hermes_build")
public class JpaHermesBuild {

	@Id
	@Column
	public String id;
	
	 @OneToMany(fetch=LAZY, mappedBy="hermesBuild", cascade=CascadeType.ALL)
	 @OrderBy("idx ASC")
	 public List<JpaHermesStep> hermesSteps = new ArrayList<>();
	
	 @ManyToOne
	 public JpaHermesResult hermesResult;
	 
	 public static JpaHermesBuild fromDTO(HermesBuildDTO dto)
	 {
		 JpaHermesBuild jpa = new JpaHermesBuild();
		 jpa.id = dto.id;
		 jpa.hermesSteps = dto.hermesSteps.stream().map(JpaHermesStep::fromDTO).
				 map(step-> {step.hermesBuild =jpa; return step;}).collect(Collectors.toList());
		 return jpa;
	 }
	 
	 public HermesBuildDTO toDTO()
	 {
		 HermesBuildDTO dto = new HermesBuildDTO();
		 dto.id = this.id;
		 dto.hermesSteps = this.hermesSteps.stream().map(JpaHermesStep::toDTO)
	                .map(step -> {
	                    step.hermesBuildId = dto.id;
	                    return step;
	                }).collect(Collectors.toList());
		 return dto;
	 }
	 
	
	 
	 
	
}
