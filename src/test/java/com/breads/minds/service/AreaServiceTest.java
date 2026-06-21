package com.breads.minds.service;

import com.breads.minds.entity.Area;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock private AreaRepository areaRepository;
    @InjectMocks private AreaService areaService;

    private Area buildArea(Long id, String name) {
        return Area.builder().id(id).name(name).build();
    }

    @Test
    void getAllAreas_returnsList() {
        when(areaRepository.findAll()).thenReturn(List.of(buildArea(1L, "A1")));
        assertThat(areaService.getAllAreas()).hasSize(1);
    }

    @Test
    void getAreaById_found_returnsArea() {
        Area area = buildArea(1L, "A1");
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        assertThat(areaService.getAreaById(1L)).isEqualTo(area);
    }

    @Test
    void getAreaById_notFound_throwsResourceNotFoundException() {
        when(areaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> areaService.getAreaById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createArea_newName_savesAndReturns() {
        when(areaRepository.existsByNameIgnoreCase("A1")).thenReturn(false);
        Area saved = buildArea(1L, "A1");
        when(areaRepository.save(any(Area.class))).thenReturn(saved);
        assertThat(areaService.createArea("A1")).isEqualTo(saved);
    }

    @Test
    void createArea_existingName_throwsIllegalArgumentException() {
        when(areaRepository.existsByNameIgnoreCase("Existing")).thenReturn(true);
        assertThatThrownBy(() -> areaService.createArea("Existing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Area already exists");
    }

    @Test
    void updateArea_updatesNameAndSaves() {
        Area area = buildArea(1L, "Old");
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaRepository.save(area)).thenReturn(area);
        areaService.updateArea(1L, "New");
        assertThat(area.getName()).isEqualTo("New");
    }

    @Test
    void deleteArea_deletesArea() {
        Area area = buildArea(1L, "A");
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area));
        areaService.deleteArea(1L);
        verify(areaRepository).delete(area);
    }
}
