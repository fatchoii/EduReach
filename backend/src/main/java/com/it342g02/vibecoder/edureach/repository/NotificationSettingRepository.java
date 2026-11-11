package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.NotificationSetting;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    List<NotificationSetting> findByUserId(Long userId);

    Optional<NotificationSetting> findFirstByUserId(Long userId);

}
