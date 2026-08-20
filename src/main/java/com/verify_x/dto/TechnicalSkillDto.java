package com.verify_x.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicalSkillDto {

    private String value;

    private String name;

    private boolean selected;

    private boolean recommended;
}