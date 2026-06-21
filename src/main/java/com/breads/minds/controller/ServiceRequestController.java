package com.breads.minds.controller;

import com.breads.minds.dto.request.ServiceRequestCreateRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.ServiceRequest;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.service.ServiceRequestService;
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
import java.util.Map;

@RestController
@RequestMapping("/service-requests")
@RequiredArgsConstructor
@Tag(name = "Service Requests", description = "District service requests and their review workflow")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    @GetMapping
    @Operation(summary = "List service requests", description = "Returns all requests. Filter by `districtId` or pass `status=PENDING` to get only pending requests.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request list returned")
    })
    public ResponseEntity<ApiResponse<List<ServiceRequest>>> getAll(
            @Parameter(description = "Filter by district ID") @RequestParam(required = false) Long districtId,
            @Parameter(description = "Filter by status — pass PENDING to get pending requests") @RequestParam(required = false) String status) {
        List<ServiceRequest> result;
        if (districtId != null) {
            result = serviceRequestService.getRequestsByDistrict(districtId);
        } else if ("PENDING".equalsIgnoreCase(status)) {
            result = serviceRequestService.getPendingRequests();
        } else {
            result = serviceRequestService.getAllRequests();
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service request by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<ApiResponse<ServiceRequest>> getById(
            @Parameter(description = "Service request ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(serviceRequestService.getRequestById(id)));
    }

    @PostMapping
    @Operation(summary = "Submit service request", description = "Any authenticated user can submit a service request.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Request submitted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<ApiResponse<ServiceRequest>> create(
            @Valid @RequestBody ServiceRequestCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ServiceRequest created = serviceRequestService.createRequest(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Request submitted", created));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Review service request", description = "Approve or reject a service request. Body: `{\"status\": \"APPROVED\", \"remarks\": \"...\"}`. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request reviewed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<ApiResponse<ServiceRequest>> review(
            @Parameter(description = "Service request ID") @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProposalStatus status = ProposalStatus.valueOf(body.get("status"));
        String remarks = body.get("remarks");
        ServiceRequest updated = serviceRequestService.reviewRequest(id, status, remarks, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Request reviewed", updated));
    }
}
