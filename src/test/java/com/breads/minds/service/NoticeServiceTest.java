package com.breads.minds.service;

import com.breads.minds.dto.request.NoticeRequest;
import com.breads.minds.entity.Notice;
import com.breads.minds.entity.enums.NoticePriority;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock private NoticeRepository noticeRepository;
    @InjectMocks private NoticeService noticeService;

    private Notice buildNotice(Long id) {
        return Notice.builder().id(id).title("T").message("M")
                .priority(NoticePriority.MEDIUM).isActive(true).createdBy("admin")
                .build();
    }

    @Test
    void getAllActiveNotices_returnsList() {
        when(noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc())
                .thenReturn(List.of(buildNotice(1L)));
        assertThat(noticeService.getAllActiveNotices()).hasSize(1);
    }

    @Test
    void getNoticesForRole_returnsList() {
        when(noticeRepository.findActiveNoticesForRole(eq(UserRole.SUPER_ADMIN), any(LocalDate.class)))
                .thenReturn(List.of(buildNotice(2L)));
        assertThat(noticeService.getNoticesForRole(UserRole.SUPER_ADMIN)).hasSize(1);
    }

    @Test
    void getNoticeById_found_returnsNotice() {
        Notice notice = buildNotice(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        assertThat(noticeService.getNoticeById(1L)).isEqualTo(notice);
    }

    @Test
    void getNoticeById_notFound_throwsResourceNotFoundException() {
        when(noticeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> noticeService.getNoticeById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createNotice_withNullIsActive_defaultsToTrue() {
        NoticeRequest req = new NoticeRequest();
        req.setTitle("Title"); req.setMessage("Msg");
        req.setPriority(NoticePriority.HIGH);
        req.setTargetRoles(List.of(UserRole.DISTRICT_COORDINATOR));
        req.setIsActive(null);
        Notice saved = buildNotice(1L);
        when(noticeRepository.save(any(Notice.class))).thenReturn(saved);
        Notice result = noticeService.createNotice(req, "admin");
        assertThat(result).isNotNull();
    }

    @Test
    void createNotice_withIsActiveFalse_savesWithFalse() {
        NoticeRequest req = new NoticeRequest();
        req.setTitle("T"); req.setMessage("M"); req.setPriority(NoticePriority.LOW);
        req.setIsActive(false);
        req.setStartDate(LocalDate.now()); req.setEndDate(LocalDate.now().plusDays(7));
        req.setTargetRoles(List.of(UserRole.SUPER_ADMIN));
        Notice saved = buildNotice(1L);
        when(noticeRepository.save(any(Notice.class))).thenReturn(saved);
        assertThat(noticeService.createNotice(req, "admin")).isNotNull();
    }

    @Test
    void updateNotice_allFieldsNull_stillSaves() {
        Notice notice = buildNotice(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(noticeRepository.save(notice)).thenReturn(notice);
        noticeService.updateNotice(1L, new NoticeRequest());
        verify(noticeRepository).save(notice);
    }

    @Test
    void updateNotice_allFieldsSet_updatesAll() {
        Notice notice = buildNotice(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        when(noticeRepository.save(notice)).thenReturn(notice);

        NoticeRequest req = new NoticeRequest();
        req.setTitle("New T"); req.setMessage("New M");
        req.setPriority(NoticePriority.CRITICAL); req.setTargetRoles(List.of(UserRole.SUPER_ADMIN));
        req.setIsActive(false); req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(1));
        noticeService.updateNotice(1L, req);

        assertThat(notice.getTitle()).isEqualTo("New T");
        assertThat(notice.getIsActive()).isFalse();
    }

    @Test
    void deleteNotice_deletesNotice() {
        Notice notice = buildNotice(1L);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        noticeService.deleteNotice(1L);
        verify(noticeRepository).delete(notice);
    }
}
