package de.fraunhofer.abm.collection.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

//import de.fraunhofer.abm.domain.FilterConfDTO;

@Entity(name="filter_conf")
public class JpaFilterConf {

	@Id
	@Column
	public int maxLocations;
	
}
