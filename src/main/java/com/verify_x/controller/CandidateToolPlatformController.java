package com.verify_x.controller;

import com.verify_x.dto.ToolPlatformResponseDto;
import com.verify_x.dto.UpdateToolPlatformRequestDto;
import com.verify_x.enums.ToolPlatform;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.CandidateToolPlatformService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate/tool-platforms")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class CandidateToolPlatformController {

    private final CandidateToolPlatformService candidateToolPlatformService;

    @GetMapping
    public ResponseEntity<ApiResponse<ToolPlatformResponseDto>>
    getToolPlatforms() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tools and platforms fetched successfully.",
                        candidateToolPlatformService.getToolPlatforms()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ToolPlatform>>>
    searchToolPlatforms(
            @RequestParam(required = false)
            String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tools and platforms search completed successfully.",
                        candidateToolPlatformService
                                .searchToolPlatforms(keyword)
                )
        );
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<ToolPlatformResponseDto>>
    getRecommendedToolPlatforms() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Recommended tools and platforms fetched successfully.",
                        candidateToolPlatformService
                                .getRecommendedToolPlatforms()
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ToolPlatformResponseDto>>
    addToolPlatforms(
            @Valid
            @ParameterObject
            @ModelAttribute
            UpdateToolPlatformRequestDto request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tools and platforms added successfully.",
                        candidateToolPlatformService
                                .addToolPlatforms(request)
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ToolPlatformResponseDto>>
    updateToolPlatforms(
            @Valid
            @ParameterObject
            @ModelAttribute
            UpdateToolPlatformRequestDto request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tools and platforms updated successfully.",
                        candidateToolPlatformService
                                .updateToolPlatforms(request)
                )
        );
    }


    @DeleteMapping("/{toolPlatform}")
    public ResponseEntity<ApiResponse<ToolPlatformResponseDto>>
    removeToolPlatform(
            @PathVariable ToolPlatform toolPlatform) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tool/platform removed successfully.",
                        candidateToolPlatformService
                                .removeToolPlatform(toolPlatform)
                )
        );
    }
}