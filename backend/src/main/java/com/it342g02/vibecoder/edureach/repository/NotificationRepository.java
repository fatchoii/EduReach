package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    boolean existsByUserIdAndRefKey(Long userId, String refKey);

}
