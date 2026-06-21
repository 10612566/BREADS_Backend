package com.breads.minds.controller;

import com.breads.minds.dto.request.BeneficiaryReportRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.service.BeneficiaryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Beneficiary monthly reports — submission, update, and lock workflow")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final BeneficiaryReportService reportService;

    @GetMapping
    @Operation(summary = "List reports",
            description = "Returns beneficiary reports with optional filtering. " +
                    "Supported combinations: `districtId` + `fromMonth` + `toMonth`, " +
                    "`fromMonth` + `toMonth`, `districtId` + `year`, `districtId`, `year`, or no filter (all reports). " +
                    "Month format: `YYYY-MM`.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report list returned")
    })
    public ResponseEntity<ApiResponse<List<BeneficiaryReport>>> getAll(
            @Parameter(description = "Filter by district ID") @RequestParam(required = false) Long districtId,
            @Parameter(description = "Filter by year (e.g. 2024)") @RequestParam(required = false) Integer year,
            @Parameter(description = "Start month inclusive, format YYYY-MM") @RequestParam(required = false) String fromMonth,
            @Parameter(description = "End month inclusive, format YYYY-MM") @RequestParam(required = false) String toMonth) {
        List<BeneficiaryReport> result;
        if (districtId != null && fromMonth != null && toMonth != null) {
            result = reportService.getReportsByDistrictAndMonthRange(districtId, fromMonth, toMonth);
        } else if (fromMonth != null && toMonth != null) {
            result = reportService.getReportsByMonthRange(fromMonth, toMonth);
        } else if (districtId != null && year != null) {
            result = reportService.getReportsByDistrictAndYear(districtId, year);
        } else if (districtId != null) {
            result = reportService.getReportsByDistrict(districtId);
        } else if (year != null) {
            result = reportService.getReportsByYear(year);
        } else {
            result = reportService.getAllReports();
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get report by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ApiResponse<BeneficiaryReport>> getById(
            @Parameter(description = "Report ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getReportById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRICT_COORDINATOR','BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Submit monthly report", description = "Requires DISTRICT_COORDINATOR, BREADS_COORDINATOR, or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Report submitted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BeneficiaryReport>> submit(
            @Valid @RequestBody BeneficiaryReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BeneficiaryReport report = reportService.submitReport(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Report submitted", report));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRICT_COORDINATOR','BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update report", description = "Requires DISTRICT_COORDINATOR, BREADS_COORDINATOR, or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ApiResponse<BeneficiaryReport>> update(
            @Parameter(description = "Report ID") @PathVariable Long id,
            @Valid @RequestBody BeneficiaryReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Report updated",
                reportService.updateReport(id, request, userDetails.getUsername())));
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Lock report", description = "Locks a report to prevent further edits. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report locked"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ApiResponse<BeneficiaryReport>> lock(
            @Parameter(description = "Report ID") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Report locked",
                reportService.lockReport(id, userDetails.getUsername())));
    }
}
