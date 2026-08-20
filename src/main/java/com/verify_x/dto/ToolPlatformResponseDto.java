package com.verify_x.dto;

import com.verify_x.enums.AppliedRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolPlatformResponseDto {

    private Long candidateId;

    private AppliedRole appliedRole;

    private List<ToolPlatformDto> tools;
}