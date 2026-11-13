package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.LiveSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {

    List<LiveSession> findByCourseId(Long courseId);

}
