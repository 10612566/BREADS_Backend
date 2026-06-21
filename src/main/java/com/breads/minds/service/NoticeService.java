package com.breads.minds.service;

import com.breads.minds.dto.request.NoticeRequest;
import com.breads.minds.entity.Notice;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> getAllActiveNotices() {
        return noticeRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    public List<Notice> getNoticesForRole(UserRole role) {
        return noticeRepository.findActiveNoticesForRole(role, LocalDate.now());
    }

    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found: " + id));
    }

    @Transactional
    public Notice createNotice(NoticeRequest request, String createdBy) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .priority(request.getPriority())
                .targetRoles(request.getTargetRoles())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createdBy(createdBy)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, NoticeRequest request) {
        Notice notice = getNoticeById(id);
        if (request.getTitle()       != null) notice.setTitle(request.getTitle());
        if (request.getMessage()     != null) notice.setMessage(request.getMessage());
        if (request.getPriority()    != null) notice.setPriority(request.getPriority());
        if (request.getTargetRoles() != null) notice.setTargetRoles(request.getTargetRoles());
        if (request.getIsActive()    != null) notice.setIsActive(request.getIsActive());
        if (request.getStartDate()   != null) notice.setStartDate(request.getStartDate());
        if (request.getEndDate()     != null) notice.setEndDate(request.getEndDate());
        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        noticeRepository.delete(getNoticeById(id));
    }
}
