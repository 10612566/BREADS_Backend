package com.breads.minds.repository;

import com.breads.minds.entity.SchoolProposal;
import com.breads.minds.entity.enums.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolProposalRepository extends JpaRepository<SchoolProposal, Long> {
    List<SchoolProposal> findByDistrictId(Long districtId);
    List<SchoolProposal> findByStatus(ProposalStatus status);
    List<SchoolProposal> findByDistrictIdAndStatus(Long districtId, ProposalStatus status);
    List<SchoolProposal> findByProposedById(Long userId);
}
