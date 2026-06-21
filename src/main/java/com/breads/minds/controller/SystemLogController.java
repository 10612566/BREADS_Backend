package com.breads.minds.controller;

import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.SystemLog;
import com.breads.minds.service.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
@Tag(name = "System Logs", description = "Audit log of all significant system actions — accessible to BREADS_COORDINATOR and SUPER_ADMIN only")
@SecurityRequirement(name = "Bearer Authentication")
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    @Operation(summary = "List system logs", description = "Returns a paginated audit log. Filter by `username` to see actions by a specific user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Log page returned"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<SystemLog>>> getLogs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Filter by username") @RequestParam(required = false) String username) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemLog> logs = username != null
                ? systemLogService.getLogsByUser(username, pageable)
                : systemLogService.getLogs(pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }
}
