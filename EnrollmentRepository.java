

package com.example.onlinecoursesystem.repository;

import com.example.onlinecoursesystem.entity.Enrollment;
import com.example.onlinecoursesystem.dto.EnrolledStudentDTO;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    
    
    @Query("""
    	    SELECT new com.example.onlinecoursesystem.dto.EnrolledStudentDTO(
    	        e.studentId,
    	        s.studentName,
    	        s.studentEmail,
    	        c.courseName
    	    )
    	    FROM Enrollment e
    	    JOIN Student s ON s.id = e.studentId
    	    JOIN course c ON c.id = e.courseId
    	""")
    	List<EnrolledStudentDTO> findAllEnrolledStudents();
}
