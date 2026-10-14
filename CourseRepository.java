
package com.example.onlinecoursesystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.onlinecoursesystem.entity.course;

public interface CourseRepository extends JpaRepository<course, Long> {
	List<course> findByCourseNameContainingIgnoreCase(String courseName);

}