package com.breads.minds.repository;

import com.breads.minds.entity.SchoolMonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolMonthlyReportRepository extends JpaRepository<SchoolMonthlyReport, Long> {
    List<SchoolMonthlyReport> findByDistrictId(Long districtId);
    List<SchoolMonthlyReport> findBySchoolId(Long schoolId);
    List<SchoolMonthlyReport> findByDistrictIdAndSchoolId(Long districtId, Long schoolId);
}
