package dev.udris.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 50)
	private String name;
	
	@ManyToMany(mappedBy = "tags")
	private Set<Post> posts = new HashSet<>();
	
	public Tag() {
	}
	
	public Tag(String name) {
		this.name = name;
	}
	
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
	
	public Set<Post> getPosts() {
		return posts;
	}
	
	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}
	
	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Tag tag = (Tag) obj;
		return id != null && id.equals(tag.getId());
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
} 