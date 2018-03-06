package eurosport.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "filters")

public class Filter implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

	@Column(name = "lectures")
	private int lectures;

	@Column(name = "practices")
	private int practices;

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private Teacher teacher;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getLectures() {
		return lectures;
	}

	public void setLectures(int lectures) {
		this.lectures = lectures;
	}

	public int getPractices() {
		return practices;
	}

	public void setPractices(int practices) {
		this.practices = practices;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
}
