package dev.udris.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.udris.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByName(String name);
	List<Tag> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT t FROM Tag t ORDER BY t.name ASC")
	List<Tag> findAllOrderByName();
	
	@Query("SELECT t FROM Tag t JOIN t.posts p WHERE p.status = 'PUBLISHED' GROUP BY t ORDER BY COUNT(p) DESC")
	List<Tag> findMostUsedTags();
} 