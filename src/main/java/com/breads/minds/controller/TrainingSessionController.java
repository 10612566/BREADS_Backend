package com.breads.minds.controller;

import com.breads.minds.dto.request.TrainingSessionRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.TrainingSession;
import com.breads.minds.entity.enums.TrainingType;
import com.breads.minds.service.TrainingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/training-sessions")
@RequiredArgsConstructor
@Tag(name = "Training Sessions", description = "Training session records linked to monthly reports")
@SecurityRequirement(name = "Bearer Authentication")
public class TrainingSessionController {

    private final TrainingSessionService sessionService;

    @GetMapping("/report/{reportId}")
    @Operation(summary = "Get sessions by report", description = "Returns all training sessions for a given monthly report ID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session list returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<ApiResponse<List<TrainingSession>>> getByReport(
            @Parameter(description = "Monthly report ID") @PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getSessionsByReport(reportId)));
    }

    @GetMapping("/report/{reportId}/type/{trainingType}")
    @Operation(summary = "Get sessions by report and training type")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session list returned")
    })
    public ResponseEntity<ApiResponse<List<TrainingSession>>> getByReportAndType(
            @Parameter(description = "Monthly report ID") @PathVariable Long reportId,
            @Parameter(description = "Training type enum value") @PathVariable TrainingType trainingType) {
        return ResponseEntity.ok(ApiResponse.ok(
                sessionService.getSessionsByReportAndType(reportId, trainingType)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get training session by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<TrainingSession>> getById(
            @Parameter(description = "Training session ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.getSessionById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DISTRICT_COORDINATOR','BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Add training session", description = "Creates a new training session linked to a report. Requires DISTRICT_COORDINATOR, BREADS_COORDINATOR, or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<TrainingSession>> addSession(
            @Valid @RequestBody TrainingSessionRequest req,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.addSession(req, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISTRICT_COORDINATOR','BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update training session", description = "Requires DISTRICT_COORDINATOR, BREADS_COORDINATOR, or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<TrainingSession>> updateSession(
            @Parameter(description = "Training session ID") @PathVariable Long id,
            @Valid @RequestBody TrainingSessionRequest req,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(sessionService.updateSession(id, req, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Delete training session", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteSession(
            @Parameter(description = "Training session ID") @PathVariable Long id,
            Authentication auth) {
        sessionService.deleteSession(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Training session deleted successfully"));
    }
}
