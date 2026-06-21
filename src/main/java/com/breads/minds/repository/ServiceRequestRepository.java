package com.breads.minds.repository;

import com.breads.minds.entity.ServiceRequest;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.entity.enums.ServiceRequestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    Optional<ServiceRequest> findByRequestNumber(String requestNumber);
    List<ServiceRequest> findByDistrictId(Long districtId);
    List<ServiceRequest> findByStatus(ProposalStatus status);
    List<ServiceRequest> findByCategory(ServiceRequestCategory category);
    List<ServiceRequest> findByDistrictIdAndStatus(Long districtId, ProposalStatus status);
    List<ServiceRequest> findByRequestedById(Long userId);

    long countByStatus(ProposalStatus status);

    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(request_number, 4) AS UNSIGNED)), 0) FROM service_requests",
           nativeQuery = true)
    int findMaxRequestSequence();
}
