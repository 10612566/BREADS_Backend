package com.breads.minds.repository;

import com.breads.minds.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findByDistrictId(Long districtId);
    List<School> findByDistrictIdAndIsActive(Long districtId, Boolean isActive);
    List<School> findByIsActive(Boolean isActive);
    long countByDistrictId(Long districtId);
}
