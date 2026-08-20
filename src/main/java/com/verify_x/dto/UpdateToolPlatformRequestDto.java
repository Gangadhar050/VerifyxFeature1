package com.verify_x.dto;

import com.verify_x.enums.ToolPlatform;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateToolPlatformRequestDto {

    @NotNull(message = "Tools and platforms are required")
    private List<ToolPlatform> toolPlatforms;
}