package com.breads.minds.repository;

import com.breads.minds.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<District> findByAreaId(Long areaId);
    Optional<District> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndAreaId(String name, Long areaId);
}
