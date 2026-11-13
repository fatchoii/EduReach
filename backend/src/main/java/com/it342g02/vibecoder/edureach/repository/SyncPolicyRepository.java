package com.it342g02.vibecoder.edureach.repository;

import com.it342g02.vibecoder.edureach.entity.SyncPolicy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncPolicyRepository extends JpaRepository<SyncPolicy, Long> {

}
