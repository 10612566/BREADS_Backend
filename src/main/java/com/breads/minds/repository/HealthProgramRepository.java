package com.breads.minds.repository;

import com.breads.minds.entity.HealthProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthProgramRepository extends JpaRepository<HealthProgram, Long> {
    List<HealthProgram> findByIsActive(Boolean isActive);
    List<HealthProgram> findByDedicatedForIgnoreCase(String dedicatedFor);
}
