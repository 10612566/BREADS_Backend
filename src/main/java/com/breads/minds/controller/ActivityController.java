package com.breads.minds.controller;

import com.breads.minds.dto.request.MindsActivityRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.MindsActivityRecord;
import com.breads.minds.service.MindsActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final MindsActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MindsActivityRecord>>> getAll(
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear) {
        List<MindsActivityRecord> result;
        if (districtId != null && fromYear != null && toYear != null) {
            result = activityService.getByDistrictAndYearRange(districtId, fromYear, toYear);
        } else if (fromYear != null && toYear != null) {
            result = activityService.getByYearRange(fromYear, toYear);
        } else if (districtId != null && year != null) {
            result = activityService.getByDistrictAndYear(districtId, year);
        } else if (districtId != null) {
            result = activityService.getByDistrict(districtId);
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("At least districtId is required"));
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MindsActivityRecord>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(activityService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MindsActivityRecord>> create(
            @Valid @RequestBody MindsActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MindsActivityRecord record = activityService.create(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Activity recorded", record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MindsActivityRecord>> update(
            @PathVariable Long id,
            @Valid @RequestBody MindsActivityRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Activity updated",
                activityService.update(id, request, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        activityService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Activity deleted", null));
    }
}
