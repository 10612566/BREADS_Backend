package com.breads.minds.service;

import com.breads.minds.entity.SystemLog;
import com.breads.minds.repository.SystemLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemLogServiceTest {

    @Mock private SystemLogRepository systemLogRepository;
    @InjectMocks private SystemLogService systemLogService;

    @Test
    void log_savesSystemLog() {
        when(systemLogRepository.save(any(SystemLog.class))).thenReturn(new SystemLog());
        systemLogService.log("admin", "ACTION", "details");
        verify(systemLogRepository).save(any(SystemLog.class));
    }

    @Test
    void getLogs_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SystemLog> page = new PageImpl<>(List.of(new SystemLog()));
        when(systemLogRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(page);
        assertThat(systemLogService.getLogs(pageable).getTotalElements()).isEqualTo(1);
    }

    @Test
    void getLogsByUser_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SystemLog> page = new PageImpl<>(List.of(new SystemLog()));
        when(systemLogRepository.findByPerformedByOrderByTimestampDesc("admin", pageable)).thenReturn(page);
        assertThat(systemLogService.getLogsByUser("admin", pageable).getTotalElements()).isEqualTo(1);
    }
}
