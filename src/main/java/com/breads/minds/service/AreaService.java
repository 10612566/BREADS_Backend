package com.breads.minds.service;

import com.breads.minds.entity.Area;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    public Area getAreaById(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found: " + id));
    }

    @Transactional
    public Area createArea(String name) {
        if (areaRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Area already exists: " + name);
        }
        return areaRepository.save(Area.builder().name(name).build());
    }

    @Transactional
    public Area updateArea(Long id, String name) {
        Area area = getAreaById(id);
        area.setName(name);
        return areaRepository.save(area);
    }

    @Transactional
    public void deleteArea(Long id) {
        areaRepository.delete(getAreaById(id));
    }
}
