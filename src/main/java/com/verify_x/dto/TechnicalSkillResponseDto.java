package com.verify_x.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicalSkillResponseDto {

    private Long candidateId;

    private String appliedRole;

    private List<TechnicalSkillDto> skills;
}