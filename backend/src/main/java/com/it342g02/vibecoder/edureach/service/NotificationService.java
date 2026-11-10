package com.it342g02.vibecoder.edureach.service;

import com.it342g02.vibecoder.edureach.dto.NotificationDto;
import com.it342g02.vibecoder.edureach.entity.Notification;
import com.it342g02.vibecoder.edureach.entity.User;
import com.it342g02.vibecoder.edureach.exception.ResourceNotFoundException;
import com.it342g02.vibecoder.edureach.repository.NotificationRepository;
import com.it342g02.vibecoder.edureach.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationDto> findAll(Long userId) {
        List<Notification> list;
        if (userId != null) {
            list = notificationRepository.findByUserId(userId);
        } else {
            list = notificationRepository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    public NotificationDto findById(Long id) {
        return toDto(get(id));
    }

    @Transactional
    public NotificationDto create(NotificationDto dto) {
        Notification entity = new Notification();
        apply(entity, dto);
        return toDto(notificationRepository.save(entity));
    }

    @Transactional
    public NotificationDto update(Long id, NotificationDto dto) {
        Notification entity = get(id);
        apply(entity, dto);
        return toDto(notificationRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with id " + id);
        }
        notificationRepository.deleteById(id);
    }

    private Notification get(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));
    }

    private void apply(Notification e, NotificationDto d) {
        if (d.userId() != null) {
            e.setUser(userRepository.findById(d.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + d.userId())));
        }
        if (d.title() != null) {
            e.setTitle(d.title());
        }
        if (d.message() != null) {
            e.setMessage(d.message());
        }
        if (d.read() != null) {
            e.setRead(d.read());
        }
    }

    private NotificationDto toDto(Notification e) {
        return new NotificationDto(
                e.getId(),
                e.getUser().getId(),
                e.getTitle(),
                e.getMessage(),
                e.getRead(),
                e.getCreatedAt());
    }
}
