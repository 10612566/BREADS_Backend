package com.breads.minds.controller;

import com.breads.minds.dto.request.AdditionalSessionRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.AdditionalSession;
import com.breads.minds.entity.enums.AdditionalSessionType;
import com.breads.minds.service.AdditionalSessionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/additional-sessions")
@RequiredArgsConstructor
@Tag(name = "Additional Sessions", description = "Extra activity sessions (beyond standard training) linked to monthly reports")
@SecurityRequirement(name = "Bearer Authentication")
public class AdditionalSessionController {

    private final AdditionalSessionService sessionService;

    @GetMapping("/report/{reportId}")
    @Operation(summary = "Get additional sessions by report")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session list returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ApiResponse<List<AdditionalSession>>> getByReport(
            @Parameter(description = "Monthly report ID") @PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getByReport(reportId)));
    }

    @GetMapping("/report/{reportId}/type/{type}")
    @Operation(summary = "Get additional sessions by report and type")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session list returned")
    })
    public ResponseEntity<ApiResponse<List<AdditionalSession>>> getByReportAndType(
            @Parameter(description = "Monthly report ID") @PathVariable Long reportId,
            @Parameter(description = "Additional session type enum value") @PathVariable AdditionalSessionType type) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getByReportAndType(reportId, type)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get additional session by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<AdditionalSession>> getById(
            @Parameter(description = "Session ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Add additional session", description = "Any authenticated user can add an additional session.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Session added"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<AdditionalSession>> add(
            @Valid @RequestBody AdditionalSessionRequest request) {
        AdditionalSession session = sessionService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Additional session added", session));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update additional session")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<AdditionalSession>> update(
            @Parameter(description = "Session ID") @PathVariable Long id,
            @Valid @RequestBody AdditionalSessionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Additional session updated", sessionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Delete additional session", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Session ID") @PathVariable Long id) {
        sessionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Additional session deleted", null));
    }
}
