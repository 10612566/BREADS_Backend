package com.breads.minds.repository;

import com.breads.minds.entity.MonthlyPlanItem;
import com.breads.minds.entity.enums.PlanItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonthlyPlanItemRepository extends JpaRepository<MonthlyPlanItem, Long> {
    List<MonthlyPlanItem> findByDistrictId(Long districtId);
    List<MonthlyPlanItem> findByDistrictIdAndMonth(Long districtId, String month);
    List<MonthlyPlanItem> findByDistrictIdAndStatus(Long districtId, PlanItemStatus status);
}
