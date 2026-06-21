package com.breads.minds.service;

import com.breads.minds.dto.request.BeneficiaryReportRequest;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.District;
import com.breads.minds.entity.User;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.BeneficiaryReportRepository;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryReportService {

    private final BeneficiaryReportRepository reportRepository;
    private final DistrictRepository districtRepository;
    private final UserRepository userRepository;
    private final SystemLogService systemLogService;

    public List<BeneficiaryReport> getAllReports() {
        return reportRepository.findAll();
    }

    public List<BeneficiaryReport> getReportsByDistrict(Long districtId) {
        return reportRepository.findByDistrictId(districtId);
    }

    public List<BeneficiaryReport> getReportsByYear(Integer year) {
        return reportRepository.findByYear(year);
    }

    public List<BeneficiaryReport> getReportsByDistrictAndYear(Long districtId, Integer year) {
        return reportRepository.findByDistrictIdAndYear(districtId, year);
    }

    public List<BeneficiaryReport> getReportsByMonthRange(String fromMonth, String toMonth) {
        return reportRepository.findByMonthBetween(fromMonth, toMonth);
    }

    public List<BeneficiaryReport> getReportsByDistrictAndMonthRange(Long districtId, String fromMonth, String toMonth) {
        return reportRepository.findByDistrictIdAndMonthBetween(districtId, fromMonth, toMonth);
    }

    public BeneficiaryReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + id));
    }

    @Transactional
    public BeneficiaryReport submitReport(BeneficiaryReportRequest req, String submittingUsername) {
        District district = districtRepository.findById(req.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found: " + req.getDistrictId()));

        User user = userRepository.findByUsername(submittingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + submittingUsername));

        if (reportRepository.existsByDistrictIdAndMonth(req.getDistrictId(), req.getMonth())) {
            throw new IllegalStateException("Report already submitted for " + req.getMonth()
                    + " in district " + district.getName());
        }

        BeneficiaryReport report = BeneficiaryReport.builder()
                .district(district)
                .month(req.getMonth())
                .year(req.getYear())
                .submittedBy(user)
                .childrenReached(req.getChildrenReached())
                .parentsReached(req.getParentsReached())
                .professionalsReached(req.getProfessionalsReached())
                .teachersReached(req.getTeachersReached())
                .volunteersReached(req.getVolunteersReached())
                .modules(req.getModules())
                .communityAwareness(req.getCommunityAwareness())
                .artTherapy(req.getArtTherapy())
                .counselling(req.getCounselling())
                .narrativeImpact(req.getNarrativeImpact())
                .isLocked(false)
                .build();

        BeneficiaryReport saved = reportRepository.save(report);
        systemLogService.log(submittingUsername, "SUBMIT_REPORT",
                "Beneficiary report submitted for " + district.getName() + " - " + req.getMonth());
        return saved;
    }

    @Transactional
    public BeneficiaryReport updateReport(Long id, BeneficiaryReportRequest req, String updatingUsername) {
        BeneficiaryReport report = getReportById(id);
        if (Boolean.TRUE.equals(report.getIsLocked())) {
            throw new IllegalStateException("This report is locked and cannot be modified");
        }

        report.setChildrenReached(req.getChildrenReached());
        report.setParentsReached(req.getParentsReached());
        report.setProfessionalsReached(req.getProfessionalsReached());
        report.setTeachersReached(req.getTeachersReached());
        report.setVolunteersReached(req.getVolunteersReached());
        report.setModules(req.getModules());
        report.setCommunityAwareness(req.getCommunityAwareness());
        report.setArtTherapy(req.getArtTherapy());
        report.setCounselling(req.getCounselling());
        report.setNarrativeImpact(req.getNarrativeImpact());

        systemLogService.log(updatingUsername, "UPDATE_REPORT", "Report updated: ID " + id);
        return reportRepository.save(report);
    }

    @Transactional
    public BeneficiaryReport lockReport(Long id, String lockedBy) {
        BeneficiaryReport report = getReportById(id);
        report.setIsLocked(true);
        systemLogService.log(lockedBy, "LOCK_REPORT", "Report locked: ID " + id);
        return reportRepository.save(report);
    }
}
