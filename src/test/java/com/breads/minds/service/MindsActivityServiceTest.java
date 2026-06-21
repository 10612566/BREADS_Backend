package com.breads.minds.service;

import com.breads.minds.dto.request.MindsActivityRequest;
import com.breads.minds.entity.District;
import com.breads.minds.entity.MindsActivityRecord;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.MindsActivityRecordRepository;
import com.breads.minds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MindsActivityServiceTest {

    @Mock private MindsActivityRecordRepository activityRepository;
    @Mock private DistrictRepository districtRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private MindsActivityService mindsActivityService;

    private District district() {
        return District.builder().id(1L).name("D").build();
    }

    private User user() {
        return User.builder().id(1L).username("u").role(UserRole.DISTRICT_COORDINATOR)
                .status(UserStatus.ACTIVE).build();
    }

    private MindsActivityRecord record() {
        return MindsActivityRecord.builder().id(1L).district(district())
                .year(2025).childName("Child A").age(12)
                .schoolName("School X").gender("Male").build();
    }

    private MindsActivityRequest request() {
        MindsActivityRequest r = new MindsActivityRequest();
        r.setDistrictId(1L); r.setYear(2025); r.setChildName("Child B");
        r.setAge(13); r.setClassName("7th"); r.setSchoolName("Y School");
        r.setInterventionType("Group"); r.setGender("Female");
        r.setLocation("Loc"); r.setTopicsDiscussed("Mental Health");
        r.setSession1Date(LocalDate.now()); r.setSession2Date(LocalDate.now().plusDays(7));
        r.setSession3Date(LocalDate.now().plusDays(14)); r.setOutcome("Good");
        r.setFollowUp("Monitor"); r.setRemarks("Positive");
        return r;
    }

    @Test
    void getByDistrict_returnsList() {
        when(activityRepository.findByDistrictId(1L)).thenReturn(List.of(record()));
        assertThat(mindsActivityService.getByDistrict(1L)).hasSize(1);
    }

    @Test
    void getByDistrictAndYear_returnsList() {
        when(activityRepository.findByDistrictIdAndYear(1L, 2025)).thenReturn(List.of(record()));
        assertThat(mindsActivityService.getByDistrictAndYear(1L, 2025)).hasSize(1);
    }

    @Test
    void getByYearRange_returnsList() {
        when(activityRepository.findByYearBetween(2024, 2025)).thenReturn(List.of(record()));
        assertThat(mindsActivityService.getByYearRange(2024, 2025)).hasSize(1);
    }

    @Test
    void getByDistrictAndYearRange_returnsList() {
        when(activityRepository.findByDistrictIdAndYearBetween(1L, 2024, 2025)).thenReturn(List.of(record()));
        assertThat(mindsActivityService.getByDistrictAndYearRange(1L, 2024, 2025)).hasSize(1);
    }

    @Test
    void getById_found_returnsRecord() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(record()));
        assertThat(mindsActivityService.getById(1L)).isNotNull();
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mindsActivityService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_success_savesRecord() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user()));
        when(activityRepository.save(any(MindsActivityRecord.class))).thenReturn(record());
        assertThat(mindsActivityService.create(request(), "u")).isNotNull();
    }

    @Test
    void create_districtNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mindsActivityService.create(request(), "u"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_userNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mindsActivityService.create(request(), "u"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_success_updatesAllFields() {
        MindsActivityRecord rec = record();
        when(activityRepository.findById(1L)).thenReturn(Optional.of(rec));
        when(activityRepository.save(rec)).thenReturn(rec);
        mindsActivityService.update(1L, request(), "u");
        assertThat(rec.getChildName()).isEqualTo("Child B");
        assertThat(rec.getAge()).isEqualTo(13);
        assertThat(rec.getGender()).isEqualTo("Female");
    }

    @Test
    void delete_success_deletesRecord() {
        MindsActivityRecord rec = record();
        when(activityRepository.findById(1L)).thenReturn(Optional.of(rec));
        mindsActivityService.delete(1L);
        verify(activityRepository).delete(rec);
    }
}
