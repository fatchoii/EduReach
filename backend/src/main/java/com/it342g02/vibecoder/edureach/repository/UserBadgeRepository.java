package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.UserBadge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUserId(Long userId);

    List<UserBadge> findByCourseId(Long courseId);

    List<UserBadge> findByUserIdAndCourseId(Long userId, Long courseId);

}
