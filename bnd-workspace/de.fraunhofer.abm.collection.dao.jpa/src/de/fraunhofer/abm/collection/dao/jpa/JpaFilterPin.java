package de.fraunhofer.abm.collection.dao.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.FilterPinDTO;

@Entity(name="filterPin")
public class JpaFilterPin implements Serializable {
	
	private static final long serialVersionUID = -2246847209888585945L;

	@Id
	@Column
	public String user;
	
	@Id
	@Column
	public String id;
	
	public static JpaFilterPin fromDTO(FilterPinDTO dto){
		JpaFilterPin pin = new JpaFilterPin();
		pin.user = dto.user;
		pin.id = dto.id;
		return pin;
	}
	
	public FilterPinDTO toDTO(){
		FilterPinDTO pin = new FilterPinDTO();
		pin.user = this.user;
		pin.id = this.id;
		return pin;
	}
	
	@Override
	public boolean equals(Object other){
		if (!(other instanceof FilterPinDTO)){return false;}
		FilterPinDTO pin = (FilterPinDTO) other;
		return (this.id.equals(pin.id) && this.user.equals(pin.user));
	}
	
	@Override
	public int hashCode(){
		String str = user + id;
		return str.hashCode();
	}
}
