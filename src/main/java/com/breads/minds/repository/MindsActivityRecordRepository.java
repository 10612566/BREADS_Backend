package com.breads.minds.repository;

import com.breads.minds.entity.MindsActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindsActivityRecordRepository extends JpaRepository<MindsActivityRecord, Long> {
    List<MindsActivityRecord> findByDistrictId(Long districtId);
    List<MindsActivityRecord> findByDistrictIdAndYear(Long districtId, Integer year);
    List<MindsActivityRecord> findByYear(Integer year);
    long countByDistrictIdAndYear(Long districtId, Integer year);
    List<MindsActivityRecord> findByYearBetween(Integer fromYear, Integer toYear);
    List<MindsActivityRecord> findByDistrictIdAndYearBetween(Long districtId, Integer fromYear, Integer toYear);
}
