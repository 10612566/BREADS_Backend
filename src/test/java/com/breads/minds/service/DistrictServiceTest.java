package com.breads.minds.service;

import com.breads.minds.entity.Area;
import com.breads.minds.entity.District;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AreaRepository;
import com.breads.minds.repository.DistrictRepository;
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
class DistrictServiceTest {

    @Mock private DistrictRepository districtRepository;
    @Mock private AreaRepository areaRepository;
    @InjectMocks private DistrictService districtService;

    private Area area(Long id) {
        return Area.builder().id(id).name("Area" + id).build();
    }

    private District district(Long id, String name) {
        return District.builder().id(id).name(name).area(area(1L)).build();
    }

    @Test
    void getAllDistricts_returnsList() {
        when(districtRepository.findAll()).thenReturn(List.of(district(1L, "D1")));
        assertThat(districtService.getAllDistricts()).hasSize(1);
    }

    @Test
    void getDistrictById_found_returnsDistrict() {
        District d = district(1L, "D1");
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        assertThat(districtService.getDistrictById(1L)).isEqualTo(d);
    }

    @Test
    void getDistrictById_notFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> districtService.getDistrictById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDistrictsByArea_returnsList() {
        when(districtRepository.findByAreaId(1L)).thenReturn(List.of(district(1L, "D")));
        assertThat(districtService.getDistrictsByArea(1L)).hasSize(1);
    }

    @Test
    void createDistrict_areaFound_savesDistrict() {
        Area a = area(1L);
        when(areaRepository.findById(1L)).thenReturn(Optional.of(a));
        District d = district(1L, "D1");
        when(districtRepository.save(any(District.class))).thenReturn(d);
        assertThat(districtService.createDistrict("D1", 1L)).isEqualTo(d);
    }

    @Test
    void createDistrict_areaNotFound_throwsResourceNotFoundException() {
        when(areaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> districtService.createDistrict("D", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateDistrict_allFieldsSet_updatesAll() {
        District d = district(1L, "OldName");
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        Area newArea = area(2L);
        when(areaRepository.findById(2L)).thenReturn(Optional.of(newArea));
        when(districtRepository.save(d)).thenReturn(d);

        districtService.updateDistrict(1L, "NewName", 2L);
        assertThat(d.getName()).isEqualTo("NewName");
        assertThat(d.getArea()).isEqualTo(newArea);
    }

    @Test
    void updateDistrict_nullNameAndArea_noUpdates() {
        District d = district(1L, "Name");
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        when(districtRepository.save(d)).thenReturn(d);

        districtService.updateDistrict(1L, null, null);
        verify(areaRepository, never()).findById(anyLong());
        assertThat(d.getName()).isEqualTo("Name");
    }

    @Test
    void updateDistrict_areaNotFound_throwsResourceNotFoundException() {
        District d = district(1L, "D");
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        when(areaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> districtService.updateDistrict(1L, "X", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteDistrict_deletesDistrict() {
        District d = district(1L, "D");
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        districtService.deleteDistrict(1L);
        verify(districtRepository).delete(d);
    }
}
