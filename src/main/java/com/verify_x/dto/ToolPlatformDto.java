package com.verify_x.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToolPlatformDto {

    private String value;

    private String name;

    private boolean selected;

    private boolean recommended;
}