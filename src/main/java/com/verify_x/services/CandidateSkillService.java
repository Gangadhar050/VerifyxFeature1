package com.verify_x.services;

import com.verify_x.dto.TechnicalSkillResponseDto;
import com.verify_x.dto.UpdateTechnicalSkillRequestDto;
import com.verify_x.enums.TechnicalSkill;

import java.util.List;

public interface CandidateSkillService {

    //Returns the technical skill catalogue for the
     // authenticated candidate.
    TechnicalSkillResponseDto getTechnicalSkills();


     // Replaces the authenticated candidate's selected skills
    TechnicalSkillResponseDto updateTechnicalSkills(
            UpdateTechnicalSkillRequestDto request
    );

     // Searches the complete technical skill catalogue
    List<TechnicalSkill> searchTechnicalSkills(
            String keyword
    );

     // Returns role-recommended skills.
    TechnicalSkillResponseDto getRecommendedSkills();


    TechnicalSkillResponseDto removeTechnicalSkill(
            TechnicalSkill skill
    );
    TechnicalSkillResponseDto addTechnicalSkills(
            UpdateTechnicalSkillRequestDto request
    );
}