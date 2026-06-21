package com.breads.minds.service;

import com.breads.minds.entity.District;
import com.breads.minds.entity.School;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.SchoolRepository;
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
class SchoolServiceTest {

    @Mock private SchoolRepository schoolRepository;
    @Mock private DistrictRepository districtRepository;
    @InjectMocks private SchoolService schoolService;

    private School buildSchool(Long id, String name) {
        return School.builder().id(id).name(name).isActive(true).build();
    }

    @Test
    void getAllSchools_returnsList() {
        when(schoolRepository.findAll()).thenReturn(List.of(buildSchool(1L, "S1")));
        assertThat(schoolService.getAllSchools()).hasSize(1);
    }

    @Test
    void getSchoolsByDistrict_returnsList() {
        when(schoolRepository.findByDistrictId(1L)).thenReturn(List.of(buildSchool(1L, "S1")));
        assertThat(schoolService.getSchoolsByDistrict(1L)).hasSize(1);
    }

    @Test
    void getActiveSchools_returnsList() {
        when(schoolRepository.findByIsActive(true)).thenReturn(List.of(buildSchool(1L, "S1")));
        assertThat(schoolService.getActiveSchools()).hasSize(1);
    }

    @Test
    void getSchoolById_found_returnsSchool() {
        School school = buildSchool(1L, "S1");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        assertThat(schoolService.getSchoolById(1L)).isEqualTo(school);
    }

    @Test
    void getSchoolById_notFound_throwsResourceNotFoundException() {
        when(schoolRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> schoolService.getSchoolById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createSchool_withNullIsActive_defaultsToTrue() {
        School school = School.builder().name("S1").build();
        District d = District.builder().id(1L).name("D1").build();
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        when(schoolRepository.save(school)).thenReturn(school);
        School result = schoolService.createSchool(school, 1L);
        assertThat(result.getIsActive()).isTrue();
    }

    @Test
    void createSchool_withIsActiveAlreadyTrue_keepsTrue() {
        School school = School.builder().name("S2").isActive(true).build();
        District d = District.builder().id(1L).name("D1").build();
        when(districtRepository.findById(1L)).thenReturn(Optional.of(d));
        when(schoolRepository.save(school)).thenReturn(school);
        assertThat(schoolService.createSchool(school, 1L).getIsActive()).isTrue();
    }

    @Test
    void createSchool_districtNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> schoolService.createSchool(new School(), 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSchool_allFieldsSet_updatesAll() {
        School existing = buildSchool(1L, "Old");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(schoolRepository.save(existing)).thenReturn(existing);

        School updated = School.builder()
                .name("New").isActive(false).taluka("Taluka1")
                .gramPanchayat("GP1").village("Village1")
                .schoolCategory("Primary").completeAddress("Addr1")
                .totalEnrollment(100).build();
        schoolService.updateSchool(1L, updated);

        assertThat(existing.getName()).isEqualTo("New");
        assertThat(existing.getIsActive()).isFalse();
        assertThat(existing.getTaluka()).isEqualTo("Taluka1");
        assertThat(existing.getGramPanchayat()).isEqualTo("GP1");
        assertThat(existing.getVillage()).isEqualTo("Village1");
        assertThat(existing.getTotalEnrollment()).isEqualTo(100);
    }

    @Test
    void updateSchool_allFieldsNull_noUpdates() {
        School existing = buildSchool(1L, "Old");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(schoolRepository.save(existing)).thenReturn(existing);
        schoolService.updateSchool(1L, new School());
        assertThat(existing.getName()).isEqualTo("Old");
    }

    @Test
    void deleteSchool_deletesSchool() {
        School school = buildSchool(1L, "S1");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        schoolService.deleteSchool(1L);
        verify(schoolRepository).delete(school);
    }

    @Test
    void toggleActive_setsFalseAndSaves() {
        School school = buildSchool(1L, "S1");
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolRepository.save(school)).thenReturn(school);
        School result = schoolService.toggleActive(1L, false);
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    void toggleActive_setsTrueAndSaves() {
        School school = School.builder().id(1L).name("S1").isActive(false).build();
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolRepository.save(school)).thenReturn(school);
        School result = schoolService.toggleActive(1L, true);
        assertThat(result.getIsActive()).isTrue();
    }
}
