package com.verify_x.controller;

import com.verify_x.dto.TechnicalSkillResponseDto;
import com.verify_x.dto.UpdateTechnicalSkillRequestDto;
import com.verify_x.enums.TechnicalSkill;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.CandidateSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate/technical-skills")
@RequiredArgsConstructor
public class CandidateSkillController {

    private final CandidateSkillService candidateSkillService;


    @GetMapping
    public ResponseEntity<ApiResponse<TechnicalSkillResponseDto>>
    getTechnicalSkills() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technical skills fetched successfully.",
                        candidateSkillService.getTechnicalSkills()));
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TechnicalSkill>>>
    searchTechnicalSkills(
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technical skills search completed successfully.",
                        candidateSkillService.searchTechnicalSkills(keyword)
                )
        );
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<TechnicalSkillResponseDto>>
    getRecommendedSkills() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Recommended technical skills fetched successfully.",
                        candidateSkillService.getRecommendedSkills()
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TechnicalSkillResponseDto>>
    addTechnicalSkills(
            @Valid
            @ParameterObject
            @ModelAttribute
            UpdateTechnicalSkillRequestDto request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technical skills added successfully.",
                        candidateSkillService.addTechnicalSkills(request)
                )
        );
    }


    @PutMapping
    public ResponseEntity<ApiResponse<TechnicalSkillResponseDto>>
    updateTechnicalSkills(
            @Valid
            @ParameterObject
            @ModelAttribute
            UpdateTechnicalSkillRequestDto request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technical skills updated successfully.",
                        candidateSkillService.updateTechnicalSkills(request)));
    }

    @DeleteMapping("/{skill}")
    public ResponseEntity<ApiResponse<TechnicalSkillResponseDto>>
    removeTechnicalSkill(
            @PathVariable TechnicalSkill skill) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Technical skill removed successfully.",
                        candidateSkillService.removeTechnicalSkill(skill)));
    }
}