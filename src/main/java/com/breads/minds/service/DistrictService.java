package com.breads.minds.service;

import com.breads.minds.entity.Area;
import com.breads.minds.entity.District;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AreaRepository;
import com.breads.minds.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final AreaRepository areaRepository;

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    public District getDistrictById(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District not found: " + id));
    }

    public List<District> getDistrictsByArea(Long areaId) {
        return districtRepository.findByAreaId(areaId);
    }

    @Transactional
    public District createDistrict(String name, Long areaId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found: " + areaId));
        return districtRepository.save(District.builder().name(name).area(area).build());
    }

    @Transactional
    public District updateDistrict(Long id, String name, Long areaId) {
        District district = getDistrictById(id);
        if (name   != null) district.setName(name);
        if (areaId != null) {
            Area area = areaRepository.findById(areaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Area not found: " + areaId));
            district.setArea(area);
        }
        return districtRepository.save(district);
    }

    @Transactional
    public void deleteDistrict(Long id) {
        districtRepository.delete(getDistrictById(id));
    }
}
