package com.breads.minds.service;

import com.breads.minds.entity.District;
import com.breads.minds.entity.School;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final DistrictRepository districtRepository;

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public List<School> getSchoolsByDistrict(Long districtId) {
        return schoolRepository.findByDistrictId(districtId);
    }

    public List<School> getActiveSchools() {
        return schoolRepository.findByIsActive(true);
    }

    public School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found: " + id));
    }

    @Transactional
    public School createSchool(School school, Long districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District not found: " + districtId));
        school.setDistrict(district);
        if (school.getIsActive() == null) school.setIsActive(true);
        return schoolRepository.save(school);
    }

    @Transactional
    public School updateSchool(Long id, School updated) {
        School existing = getSchoolById(id);
        if (updated.getName()            != null) existing.setName(updated.getName());
        if (updated.getIsActive()        != null) existing.setIsActive(updated.getIsActive());
        if (updated.getTaluka()          != null) existing.setTaluka(updated.getTaluka());
        if (updated.getGramPanchayat()   != null) existing.setGramPanchayat(updated.getGramPanchayat());
        if (updated.getVillage()         != null) existing.setVillage(updated.getVillage());
        if (updated.getSchoolCategory()  != null) existing.setSchoolCategory(updated.getSchoolCategory());
        if (updated.getCompleteAddress() != null) existing.setCompleteAddress(updated.getCompleteAddress());
        if (updated.getTotalEnrollment() != null) existing.setTotalEnrollment(updated.getTotalEnrollment());
        return schoolRepository.save(existing);
    }

    @Transactional
    public void deleteSchool(Long id) {
        schoolRepository.delete(getSchoolById(id));
    }

    @Transactional
    public School toggleActive(Long id, boolean active) {
        School school = getSchoolById(id);
        school.setIsActive(active);
        return schoolRepository.save(school);
    }
}
