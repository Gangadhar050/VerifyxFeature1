package com.verify_x.services;

import com.verify_x.dto.ToolPlatformResponseDto;
import com.verify_x.dto.UpdateToolPlatformRequestDto;
import com.verify_x.enums.ToolPlatform;

import java.util.List;

public interface CandidateToolPlatformService {

    ToolPlatformResponseDto getToolPlatforms();

    ToolPlatformResponseDto addToolPlatforms(
            UpdateToolPlatformRequestDto request);

    ToolPlatformResponseDto updateToolPlatforms(
            UpdateToolPlatformRequestDto request
    );

    List<ToolPlatform> searchToolPlatforms(
            String keyword
    );

    ToolPlatformResponseDto getRecommendedToolPlatforms();

    ToolPlatformResponseDto removeToolPlatform(
            ToolPlatform toolPlatform
    );
}