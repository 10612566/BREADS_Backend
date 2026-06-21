package com.breads.minds.service;

import com.breads.minds.dto.request.SchoolProposalRequest;
import com.breads.minds.dto.request.ServiceRequestCreateRequest;
import com.breads.minds.entity.District;
import com.breads.minds.entity.SchoolProposal;
import com.breads.minds.entity.ServiceRequest;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.entity.enums.ServiceRequestCategory;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.ServiceRequestRepository;
import com.breads.minds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final DistrictRepository districtRepository;
    private final UserRepository userRepository;
    private final SystemLogService systemLogService;

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAll();
    }

    public List<ServiceRequest> getRequestsByDistrict(Long districtId) {
        return serviceRequestRepository.findByDistrictId(districtId);
    }

    public List<ServiceRequest> getPendingRequests() {
        return serviceRequestRepository.findByStatus(ProposalStatus.PENDING);
    }

    public ServiceRequest getRequestById(Long id) {
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found: " + id));
    }

    @Transactional
    public ServiceRequest createRequest(ServiceRequestCreateRequest req, String requestingUsername) {
        District district = districtRepository.findById(req.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found: " + req.getDistrictId()));

        User requestedBy = userRepository.findByUsername(requestingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestingUsername));

        String requestNumber = generateRequestNumber();

        ServiceRequest sr = ServiceRequest.builder()
                .requestNumber(requestNumber)
                .district(district)
                .category(req.getCategory())
                .requestedBy(requestedBy)
                .status(ProposalStatus.PENDING)
                .description(req.getDescription())
                .build();

        if (req.getCategory() == ServiceRequestCategory.SCHOOL_SELECTION
                && req.getSchoolSelectionData() != null) {
            sr.setSchoolProposal(buildProposal(req.getSchoolSelectionData(), district, requestedBy));
        }

        ServiceRequest saved = serviceRequestRepository.save(sr);
        systemLogService.log(requestingUsername, "CREATE_SERVICE_REQUEST",
                "SR created: " + saved.getRequestNumber() + " [" + req.getCategory() + "]");
        return saved;
    }

    @Transactional
    public ServiceRequest reviewRequest(Long id, ProposalStatus newStatus, String remarks, String reviewerUsername) {
        ServiceRequest sr = getRequestById(id);
        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUsername));

        sr.setStatus(newStatus);
        sr.setRemarks(remarks);
        sr.setReviewedBy(reviewer);
        sr.setReviewedAt(LocalDateTime.now());

        if (sr.getSchoolProposal() != null) {
            sr.getSchoolProposal().setStatus(newStatus);
            sr.getSchoolProposal().setRemarks(remarks);
        }

        systemLogService.log(reviewerUsername, "REVIEW_SERVICE_REQUEST",
                "SR " + sr.getRequestNumber() + " set to " + newStatus);
        return serviceRequestRepository.save(sr);
    }

    private String generateRequestNumber() {
        int nextSeq = serviceRequestRepository.findMaxRequestSequence() + 1;
        return String.format("SR-%03d", nextSeq);
    }

    private SchoolProposal buildProposal(SchoolProposalRequest r, District district, User proposedBy) {
        return SchoolProposal.builder()
                .district(district)
                .proposedBy(proposedBy)
                .status(ProposalStatus.PENDING)
                .schoolName(r.getSchoolName())
                .taluka(r.getTaluka())
                .gramPanchayat(r.getGramPanchayat())
                .villageName(r.getVillageName())
                .distanceFromCenter(r.getDistanceFromCenter())
                .schoolCategory(r.getSchoolCategory())
                .schoolStatus(r.getSchoolStatus())
                .completeAddress(r.getCompleteAddress())
                .justification(r.getJustification())
                .teachersMale(r.getTeachersMale())
                .teachersFemale(r.getTeachersFemale())
                .teachersTotal(r.getTeachersTotal())
                .class5Boys(r.getClass5Boys())   .class5Girls(r.getClass5Girls())
                .class6Boys(r.getClass6Boys())   .class6Girls(r.getClass6Girls())
                .class7Boys(r.getClass7Boys())   .class7Girls(r.getClass7Girls())
                .class8Boys(r.getClass8Boys())   .class8Girls(r.getClass8Girls())
                .class9Boys(r.getClass9Boys())   .class9Girls(r.getClass9Girls())
                .grandTotalBoys(r.getGrandTotalBoys())
                .grandTotalGirls(r.getGrandTotalGirls())
                .grandTotalTotal(r.getGrandTotalTotal())
                .groupsDivided(r.getGroupsDivided())
                .strengthPerGroup(r.getStrengthPerGroup())
                .totalEnrollment(r.getTotalEnrollment())
                .marginalizedPercentage(r.getMarginalizedPercentage())
                .sdqPercentage(r.getSdqPercentage())
                .academicPerformancePercentage(r.getAcademicPerformancePercentage())
                .dropoutRatePercentage(r.getDropoutRatePercentage())
                .studentsRequiringSupport(r.getStudentsRequiringSupport())
                .hasSchoolCounselor(r.getHasSchoolCounselor())
                .schoolCounselorName(r.getSchoolCounselorName())
                .hasSupportStaffSubstitution(r.getHasSupportStaffSubstitution())
                .hasProfessionalPartnership(r.getHasProfessionalPartnership())
                .teachersWillingPercentage(r.getTeachersWillingPercentage())
                .proactiveAdministration(r.getProactiveAdministration())
                .hasPhysicalSpace(r.getHasPhysicalSpace())
                .hasBasicAmenities(r.getHasBasicAmenities())
                .amenitiesList(r.getAmenitiesList() != null
                        ? String.join(",", r.getAmenitiesList()) : null)
                .isHighRiskRegion(r.getIsHighRiskRegion())
                .hasActiveSDMC(r.getHasActiveSDMC())
                .hasStaffInterest(r.getHasStaffInterest())
                .overallSuitability(r.getOverallSuitability())
                .selectionComments(r.getSelectionComments())
                .estimatedBeneficiaries(r.getEstimatedBeneficiaries())
                .build();
    }
}
