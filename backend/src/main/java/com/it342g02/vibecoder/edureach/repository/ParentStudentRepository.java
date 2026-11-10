package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.ParentStudent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {

    List<ParentStudent> findByParentId(Long parentId);

    List<ParentStudent> findByStudentId(Long studentId);

    List<ParentStudent> findByParentIdAndStudentId(Long parentId, Long studentId);

}
