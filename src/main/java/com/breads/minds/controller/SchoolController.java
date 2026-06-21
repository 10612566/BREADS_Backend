package com.breads.minds.controller;

import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.School;
import com.breads.minds.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
@Tag(name = "Schools", description = "School management")
@SecurityRequirement(name = "Bearer Authentication")
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    @Operation(summary = "List schools", description = "Returns all schools. Filter by `districtId` or pass `active=true` to get only active schools.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School list returned")
    })
    public ResponseEntity<ApiResponse<List<School>>> getAll(
            @Parameter(description = "Filter by district ID") @RequestParam(required = false) Long districtId,
            @Parameter(description = "Pass true to return only active schools") @RequestParam(required = false) Boolean active) {
        List<School> schools;
        if (districtId != null) {
            schools = schoolService.getSchoolsByDistrict(districtId);
        } else if (Boolean.TRUE.equals(active)) {
            schools = schoolService.getActiveSchools();
        } else {
            schools = schoolService.getAllSchools();
        }
        return ResponseEntity.ok(ApiResponse.ok(schools));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get school by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<School>> getById(
            @Parameter(description = "School ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.getSchoolById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Create school", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN. Pass `districtId` as a query parameter.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "School created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<School>> create(
            @RequestBody School school,
            @Parameter(description = "District to assign the school to", required = true) @RequestParam Long districtId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("School created", schoolService.createSchool(school, districtId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update school", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<School>> update(
            @Parameter(description = "School ID") @PathVariable Long id,
            @RequestBody School school) {
        return ResponseEntity.ok(ApiResponse.ok("School updated", schoolService.updateSchool(id, school)));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Toggle school active status", description = "Body: `{\"active\": true}`. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status toggled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<School>> toggleActive(
            @Parameter(description = "School ID") @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(ApiResponse.ok(schoolService.toggleActive(id, body.get("active"))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete school", description = "Requires SUPER_ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "School deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "School ID") @PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.ok("School deleted", null));
    }
}
