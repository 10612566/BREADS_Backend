package com.breads.minds.repository;

import com.breads.minds.entity.BeneficiaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryReportRepository extends JpaRepository<BeneficiaryReport, Long> {

    Optional<BeneficiaryReport> findByDistrictIdAndMonth(Long districtId, String month);
    List<BeneficiaryReport> findByDistrictId(Long districtId);
    List<BeneficiaryReport> findByYear(Integer year);
    List<BeneficiaryReport> findByDistrictIdAndYear(Long districtId, Integer year);
    List<BeneficiaryReport> findByMonthOrderByDistrictIdAsc(String month);

    @Query("""
        SELECT COALESCE(SUM(r.childrenReached), 0) FROM BeneficiaryReport r
        WHERE r.year = :year
    """)
    long sumChildrenReachedByYear(@Param("year") Integer year);

    @Query("""
        SELECT COALESCE(SUM(r.childrenReached + r.parentsReached + r.professionalsReached
                          + r.teachersReached + r.volunteersReached), 0)
        FROM BeneficiaryReport r WHERE r.year = :year
    """)
    long sumTotalBeneficiariesByYear(@Param("year") Integer year);

    boolean existsByDistrictIdAndMonth(Long districtId, String month);

    List<BeneficiaryReport> findByMonthBetween(String fromMonth, String toMonth);
    List<BeneficiaryReport> findByDistrictIdAndMonthBetween(Long districtId, String fromMonth, String toMonth);
}
