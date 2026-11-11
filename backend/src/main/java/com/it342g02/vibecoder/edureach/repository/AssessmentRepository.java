package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.Assessment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByModuleId(Long moduleId);

    List<Assessment> findByStatus(String status);

    List<Assessment> findByModuleIdAndStatus(Long moduleId, String status);

}
