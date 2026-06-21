package com.breads.minds.service;

import com.breads.minds.dto.request.MindsActivityRequest;
import com.breads.minds.entity.District;
import com.breads.minds.entity.MindsActivityRecord;
import com.breads.minds.entity.User;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.MindsActivityRecordRepository;
import com.breads.minds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MindsActivityService {

    private final MindsActivityRecordRepository activityRepository;
    private final DistrictRepository districtRepository;
    private final UserRepository userRepository;

    public List<MindsActivityRecord> getByDistrict(Long districtId) {
        return activityRepository.findByDistrictId(districtId);
    }

    public List<MindsActivityRecord> getByDistrictAndYear(Long districtId, Integer year) {
        return activityRepository.findByDistrictIdAndYear(districtId, year);
    }

    public List<MindsActivityRecord> getByYearRange(Integer fromYear, Integer toYear) {
        return activityRepository.findByYearBetween(fromYear, toYear);
    }

    public List<MindsActivityRecord> getByDistrictAndYearRange(Long districtId, Integer fromYear, Integer toYear) {
        return activityRepository.findByDistrictIdAndYearBetween(districtId, fromYear, toYear);
    }

    public MindsActivityRecord getById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity record not found: " + id));
    }

    @Transactional
    public MindsActivityRecord create(MindsActivityRequest req, String username) {
        District district = districtRepository.findById(req.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found: " + req.getDistrictId()));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        MindsActivityRecord record = MindsActivityRecord.builder()
                .district(district)
                .year(req.getYear())
                .childName(req.getChildName())
                .age(req.getAge())
                .className(req.getClassName())
                .schoolName(req.getSchoolName())
                .interventionType(req.getInterventionType())
                .gender(req.getGender())
                .location(req.getLocation())
                .topicsDiscussed(req.getTopicsDiscussed())
                .session1Date(req.getSession1Date())
                .session2Date(req.getSession2Date())
                .session3Date(req.getSession3Date())
                .outcome(req.getOutcome())
                .followUp(req.getFollowUp())
                .remarks(req.getRemarks())
                .submittedBy(user)
                .build();

        return activityRepository.save(record);
    }

    @Transactional
    public MindsActivityRecord update(Long id, MindsActivityRequest req, String username) {
        MindsActivityRecord record = getById(id);

        record.setChildName(req.getChildName());
        record.setAge(req.getAge());
        record.setClassName(req.getClassName());
        record.setSchoolName(req.getSchoolName());
        record.setInterventionType(req.getInterventionType());
        record.setGender(req.getGender());
        record.setLocation(req.getLocation());
        record.setTopicsDiscussed(req.getTopicsDiscussed());
        record.setSession1Date(req.getSession1Date());
        record.setSession2Date(req.getSession2Date());
        record.setSession3Date(req.getSession3Date());
        record.setOutcome(req.getOutcome());
        record.setFollowUp(req.getFollowUp());
        record.setRemarks(req.getRemarks());

        return activityRepository.save(record);
    }

    @Transactional
    public void delete(Long id) {
        activityRepository.delete(getById(id));
    }
}
