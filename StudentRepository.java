

package com.example.onlinecoursesystem.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.onlinecoursesystem.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	
	 Optional<Student> findByStudentEmail(String studentEmail);
}
