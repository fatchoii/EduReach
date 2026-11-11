package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.SyncLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    List<SyncLog> findByUserId(Long userId);

    boolean existsByUserIdAndClientId(Long userId, String clientId);

    List<SyncLog> findAllByOrderBySyncedAtDesc();

}
