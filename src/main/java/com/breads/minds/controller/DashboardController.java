package com.breads.minds.controller;

import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.dto.response.DashboardResponse;
import com.breads.minds.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Summary statistics for the BREADS MINDS dashboard")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Get dashboard summary", description = "Returns aggregated statistics. Pass `year` to filter by academic year; defaults to the current year.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dashboard data returned")
    })
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @Parameter(description = "Academic year (e.g. 2024). Defaults to current year.") @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboard(year)));
    }
}
