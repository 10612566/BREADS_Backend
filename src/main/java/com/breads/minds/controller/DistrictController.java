package com.breads.minds.controller;

import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.District;
import com.breads.minds.service.DistrictService;
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
@RequestMapping("/districts")
@RequiredArgsConstructor
@Tag(name = "Districts", description = "District management within areas")
@SecurityRequirement(name = "Bearer Authentication")
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping
    @Operation(summary = "List districts", description = "Returns all districts. Filter by area using the optional `areaId` query parameter.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District list returned")
    })
    public ResponseEntity<ApiResponse<List<District>>> getAll(
            @Parameter(description = "Filter by area ID") @RequestParam(required = false) Long areaId) {
        List<District> result = areaId != null
                ? districtService.getDistrictsByArea(areaId)
                : districtService.getAllDistricts();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get district by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<District>> getById(
            @Parameter(description = "District ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(districtService.getDistrictById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Create district", description = "Body: `{\"name\": \"District Name\", \"areaId\": 1}`. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "District created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<District>> create(@RequestBody Map<String, Object> body) {
        String name   = (String)  body.get("name");
        Long   areaId = ((Number) body.get("areaId")).longValue();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("District created", districtService.createDistrict(name, areaId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update district", description = "Body: `{\"name\": \"New Name\", \"areaId\": 2}` (areaId optional). Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<District>> update(
            @Parameter(description = "District ID") @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String name   = (String)  body.get("name");
        Long   areaId = body.get("areaId") != null ? ((Number) body.get("areaId")).longValue() : null;
        return ResponseEntity.ok(ApiResponse.ok("District updated",
                districtService.updateDistrict(id, name, areaId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete district", description = "Requires SUPER_ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "District deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "District not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "District ID") @PathVariable Long id) {
        districtService.deleteDistrict(id);
        return ResponseEntity.ok(ApiResponse.ok("District deleted", null));
    }
}
