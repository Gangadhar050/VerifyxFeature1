package com.verify_x.dto;

import com.verify_x.enums.TechnicalSkill;
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
public class HRCandidateDashboardDto {

    private Long id;

    private String username;

    private String email;

    private String candidateType;

    private String status;

    private List<TechnicalSkill> technicalSkills;

    private String appliedRole;
}