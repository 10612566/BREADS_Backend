package com.breads.minds.controller;

import com.breads.minds.dto.response.ApiResponse;
import com.breads.minds.entity.Area;
import com.breads.minds.service.AreaService;
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
@RequestMapping("/areas")
@RequiredArgsConstructor
@Tag(name = "Areas", description = "Geographic area management")
@SecurityRequirement(name = "Bearer Authentication")
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    @Operation(summary = "List all areas")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Area list returned")
    })
    public ResponseEntity<ApiResponse<List<Area>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(areaService.getAllAreas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get area by ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Area found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Area not found")
    })
    public ResponseEntity<ApiResponse<Area>> getById(
            @Parameter(description = "Area ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(areaService.getAreaById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Create area", description = "Body: `{\"name\": \"Area Name\"}`. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Area created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Area>> create(@RequestBody Map<String, String> body) {
        Area area = areaService.createArea(body.get("name"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Area created", area));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BREADS_COORDINATOR','SUPER_ADMIN')")
    @Operation(summary = "Update area name", description = "Body: `{\"name\": \"New Name\"}`. Requires BREADS_COORDINATOR or SUPER_ADMIN.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Area updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Area not found")
    })
    public ResponseEntity<ApiResponse<Area>> update(
            @Parameter(description = "Area ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Area updated", areaService.updateArea(id, body.get("name"))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete area", description = "Requires SUPER_ADMIN role.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Area deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Area not found")
    })
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Area ID") @PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.ok(ApiResponse.ok("Area deleted", null));
    }
}
