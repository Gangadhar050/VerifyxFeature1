package com.verify_x.dto;

import com.verify_x.enums.TechnicalSkill;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTechnicalSkillRequestDto {

    @NotNull(message = "Technical skills are required")
    private List<TechnicalSkill> technicalSkills;
}