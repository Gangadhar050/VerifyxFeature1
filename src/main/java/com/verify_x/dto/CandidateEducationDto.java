package com.verify_x.dto;

//import com.verify_x.enums.Language;
//import com.verify_x.enums.SoftSkill;
import com.verify_x.enums.TechnicalSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateEducationDto {

    private String highestEducation;

    private String college;

    private Integer passingYear;

    private Double percentage;

    private List<TechnicalSkill> technicalSkills;
}
