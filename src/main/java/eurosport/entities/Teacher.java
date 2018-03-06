package eurosport.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "teachers")

public class Teacher implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
		
	@Column(name = "fio")
	private String fio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	@Override
	public String toString() {
		return fio;
	}
}
