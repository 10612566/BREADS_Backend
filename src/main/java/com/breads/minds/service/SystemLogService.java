package com.breads.minds.service;

import com.breads.minds.entity.SystemLog;
import com.breads.minds.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String performedBy, String action, String details) {
        systemLogRepository.save(SystemLog.builder()
                .performedBy(performedBy)
                .action(action)
                .details(details)
                .build());
    }

    public Page<SystemLog> getLogs(Pageable pageable) {
        return systemLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    public Page<SystemLog> getLogsByUser(String username, Pageable pageable) {
        return systemLogRepository.findByPerformedByOrderByTimestampDesc(username, pageable);
    }
}
