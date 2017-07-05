package de.fraunhofer.abm.collection.dao.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.fraunhofer.abm.domain.CollectionPinDTO;

@Entity(name="collectionPin")
public class JpaCollectionPin implements Serializable {
	
	private static final long serialVersionUID = 9096421964344121197L;

	@Id
	@Column
	public String user;
	
	@Id
	@Column
	public String id;
	
	public static JpaCollectionPin fromDTO(CollectionPinDTO dto){
		JpaCollectionPin pin = new JpaCollectionPin();
		pin.user = dto.user;
		pin.id = dto.id;
		return pin;
	}
	
	public CollectionPinDTO toDTO(){
		CollectionPinDTO pin = new CollectionPinDTO();
		pin.user = this.user;
		pin.id = this.id;
		return pin;
	}
	
	@Override
	public boolean equals(Object other){
		if (!(other instanceof CollectionPinDTO)){return false;}
		CollectionPinDTO pin = (CollectionPinDTO) other;
		return (this.id.equals(pin.id) && this.user.equals(pin.user));
	}
	
	@Override
	public int hashCode(){
		String str = user + id;
		return str.hashCode();
	}
}
