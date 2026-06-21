package com.breads.minds.controller;

import com.breads.minds.dto.request.NoticeRequest;
import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.Notice;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.service.NoticeService;
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
@RequestMapping("/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "Notice board — announcements targeted by user role")
@SecurityRequirement(name = "Bearer Authentication")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @Operation(summary = "List notices", description = "Returns all active notices. Filter by target role with the optional `role` query parameter (e.g. `DISTRICT_COORDINATOR`).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notice list returned")
    })
    public ResponseEntity<ApiResponse<List<Notice>>> getAll(
            @Parameter(description = "UserRole enum value to filter notices (e.g. DISTRICT_COORDINATOR)") @RequestParam(required = false) String role) {
        List<Notice> notices = role != null
                ? noticeService.getNoticesForRole(UserRole.valueOf(role))
                : noticeService.getAllActiveNotices();
        return ResponseEntity.ok(ApiResponse.ok(notices));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notice by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notice found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notice not found")
    })
    public ResponseEntity<ApiResponse<Notice>> getById(
            @Parameter(description = "Notice ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(noticeService.getNoticeById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Create notice", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Notice created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Notice>> create(
            @Valid @RequestBody NoticeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Notice notice = noticeService.createNotice(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Notice created", notice));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update notice", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notice updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notice not found")
    })
    public ResponseEntity<ApiResponse<Notice>> update(
            @Parameter(description = "Notice ID") @PathVariable Long id,
            @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Notice updated", noticeService.updateNotice(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Delete notice", description = "Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notice deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notice not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Notice ID") @PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiResponse.ok("Notice deleted", null));
    }
}
