package eurosport.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subjects")

public class Subject implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
		
	@Column(name = "name")
	private String name;

	@Column(name = "need_computer")
	private int needComputer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNeedComputer() {
		return needComputer;
	}

	public void setNeedComputer(int needComputer) {
		this.needComputer = needComputer;
	}

	@Override
	public String toString() {
		return name;
	}
}
