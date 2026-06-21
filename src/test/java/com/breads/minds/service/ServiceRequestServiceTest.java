package com.breads.minds.service;

import com.breads.minds.dto.request.SchoolProposalRequest;
import com.breads.minds.dto.request.ServiceRequestCreateRequest;
import com.breads.minds.entity.District;
import com.breads.minds.entity.SchoolProposal;
import com.breads.minds.entity.ServiceRequest;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.entity.enums.ServiceRequestCategory;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.ServiceRequestRepository;
import com.breads.minds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private DistrictRepository districtRepository;
    @Mock private UserRepository userRepository;
    @Mock private SystemLogService systemLogService;
    @InjectMocks private ServiceRequestService serviceRequestService;

    private District district() {
        return District.builder().id(1L).name("D1").build();
    }

    private User user() {
        return User.builder().id(1L).username("u1").role(UserRole.DISTRICT_COORDINATOR)
                .status(UserStatus.ACTIVE).build();
    }

    private ServiceRequest buildSR(Long id) {
        return ServiceRequest.builder().id(id).requestNumber("SR-001")
                .district(district()).status(ProposalStatus.PENDING)
                .category(ServiceRequestCategory.INFRASTRUCTURE_SUPPORT)
                .requestedBy(user()).build();
    }

    private ServiceRequestCreateRequest request(ServiceRequestCategory cat, SchoolProposalRequest proposal) {
        ServiceRequestCreateRequest r = new ServiceRequestCreateRequest();
        r.setDistrictId(1L); r.setCategory(cat);
        r.setDescription("Test description");
        r.setSchoolSelectionData(proposal);
        return r;
    }

    @Test
    void getAllRequests_returnsList() {
        when(serviceRequestRepository.findAll()).thenReturn(List.of(buildSR(1L)));
        assertThat(serviceRequestService.getAllRequests()).hasSize(1);
    }

    @Test
    void getRequestsByDistrict_returnsList() {
        when(serviceRequestRepository.findByDistrictId(1L)).thenReturn(List.of(buildSR(1L)));
        assertThat(serviceRequestService.getRequestsByDistrict(1L)).hasSize(1);
    }

    @Test
    void getPendingRequests_returnsList() {
        when(serviceRequestRepository.findByStatus(ProposalStatus.PENDING)).thenReturn(List.of(buildSR(1L)));
        assertThat(serviceRequestService.getPendingRequests()).hasSize(1);
    }

    @Test
    void getRequestById_found_returnsRequest() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(buildSR(1L)));
        assertThat(serviceRequestService.getRequestById(1L)).isNotNull();
    }

    @Test
    void getRequestById_notFound_throwsResourceNotFoundException() {
        when(serviceRequestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serviceRequestService.getRequestById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createRequest_withoutSchoolProposal_savesServiceRequest() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.findMaxRequestSequence()).thenReturn(0);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(buildSR(1L));

        ServiceRequest result = serviceRequestService.createRequest(
                request(ServiceRequestCategory.INFRASTRUCTURE_SUPPORT, null), "u1");
        assertThat(result.getRequestNumber()).isEqualTo("SR-001");
    }

    @Test
    void createRequest_withSchoolProposalAndAmenitiesList_setsProposal() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.findMaxRequestSequence()).thenReturn(5);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(buildSR(1L));

        SchoolProposalRequest proposal = new SchoolProposalRequest();
        proposal.setSchoolName("Test School"); proposal.setTaluka("T");
        proposal.setGramPanchayat("GP"); proposal.setVillageName("V");
        proposal.setAmenitiesList(List.of("Toilet", "Water"));

        serviceRequestService.createRequest(
                request(ServiceRequestCategory.SCHOOL_SELECTION, proposal), "u1");
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void createRequest_withSchoolProposalAndNullAmenitiesList_setsProposalNullAmenities() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.findMaxRequestSequence()).thenReturn(2);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(buildSR(1L));

        SchoolProposalRequest proposal = new SchoolProposalRequest();
        proposal.setSchoolName("Test School"); proposal.setAmenitiesList(null);

        serviceRequestService.createRequest(
                request(ServiceRequestCategory.SCHOOL_SELECTION, proposal), "u1");
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void createRequest_withSchoolSelectionCategoryButNullProposal_noProposalSet() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.findMaxRequestSequence()).thenReturn(0);
        when(serviceRequestRepository.save(any(ServiceRequest.class))).thenReturn(buildSR(1L));

        serviceRequestService.createRequest(
                request(ServiceRequestCategory.SCHOOL_SELECTION, null), "u1");
        verify(serviceRequestRepository).save(any(ServiceRequest.class));
    }

    @Test
    void createRequest_districtNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serviceRequestService.createRequest(
                request(ServiceRequestCategory.INFRASTRUCTURE_SUPPORT, null), "u1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createRequest_userNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serviceRequestService.createRequest(
                request(ServiceRequestCategory.INFRASTRUCTURE_SUPPORT, null), "u1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reviewRequest_withSchoolProposal_updatesProposalStatus() {
        ServiceRequest sr = buildSR(1L);
        SchoolProposal proposal = SchoolProposal.builder().status(ProposalStatus.PENDING).build();
        sr.setSchoolProposal(proposal);
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.save(sr)).thenReturn(sr);

        serviceRequestService.reviewRequest(1L, ProposalStatus.APPROVED, "Looks good", "admin");
        assertThat(sr.getStatus()).isEqualTo(ProposalStatus.APPROVED);
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.APPROVED);
        assertThat(proposal.getRemarks()).isEqualTo("Looks good");
    }

    @Test
    void reviewRequest_withoutSchoolProposal_updatesStatusOnly() {
        ServiceRequest sr = buildSR(1L);
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(sr));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user()));
        when(serviceRequestRepository.save(sr)).thenReturn(sr);

        serviceRequestService.reviewRequest(1L, ProposalStatus.REJECTED, "Not suitable", "admin");
        assertThat(sr.getStatus()).isEqualTo(ProposalStatus.REJECTED);
        assertThat(sr.getRemarks()).isEqualTo("Not suitable");
    }

    @Test
    void reviewRequest_reviewerNotFound_throwsResourceNotFoundException() {
        when(serviceRequestRepository.findById(1L)).thenReturn(Optional.of(buildSR(1L)));
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> serviceRequestService.reviewRequest(
                1L, ProposalStatus.APPROVED, "", "ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
