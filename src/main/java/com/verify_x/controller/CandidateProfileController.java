package com.verify_x.controller;


import com.verify_x.dto.CandidateEducationDto;
import com.verify_x.dto.CandidateProfileDto;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateService candidateService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        CandidateProfileDto profile =
                candidateService.getCandidateProfile(principal.getUserId());

        return ResponseEntity.ok(
                ApiResponse.<CandidateProfileDto>builder()
                        .success(true)
                        .message("Candidate profile fetched successfully.")
                        .data(profile)
                        .build()
        );
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> saveProfile(
            @Valid @RequestBody CandidateProfileDto dto) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        CandidateProfileDto profile =
                candidateService.saveCandidateProfile(
                        principal.getUserId(),
                        dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<CandidateProfileDto>builder()
                                .success(true)
                                .message("Candidate profile created successfully.")
                                .data(profile)
                                .build()
                );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> updateProfile(
            @Valid @RequestBody CandidateProfileDto dto) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        CandidateProfileDto profile =
                candidateService.updateCandidateProfile(
                        principal.getUserId(),
                        dto);

        return ResponseEntity.ok(
                ApiResponse.<CandidateProfileDto>builder()
                        .success(true)
                        .message("Candidate profile updated successfully.")
                        .data(profile)
                        .build()
        );
    }

    //EducationDetails
//
//    @PostMapping("/education")
//    public ResponseEntity<ApiResponse<String>> saveEducation(
//            @RequestBody CandidateEducationDto dto) {
//
//        candidateService.saveEducation(dto);
//
//        return ResponseEntity.ok(
//                ApiResponse.<String>builder()
//                        .success(true)
//                        .message("Education Details Saved Successfully")
//                        .data("Success")
//                        .build()
//        );
//    }
//
//    @PutMapping("/education")
//    public ResponseEntity<ApiResponse<String>> updateEducation(
//            @RequestBody CandidateEducationDto dto) {
//
//        candidateService.updateEducation(dto);
//
//        return ResponseEntity.ok(
//                ApiResponse.<String>builder()
//                        .success(true)
//                        .message("Education details updated successfully.")
//                        .data("Success")
//                        .build()
//        );
//    }
//
//    @GetMapping("/education/{candidateId}")
//    public ResponseEntity<ApiResponse<CandidateEducationDto>> getEducationByCandidateId(
//            @PathVariable Long candidateId) {
//
//        return ResponseEntity.ok(
//                ApiResponse.<CandidateEducationDto>builder()
//                        .success(true)
//                        .message("Education details fetched successfully")
//                        .data(candidateService.getEducationByCandidateId(candidateId))
//                        .build()
//        );
//    }
//    @GetMapping("/education/email")
//    public ResponseEntity<ApiResponse<CandidateEducationDto>> getEducationByEmail(
//            @RequestParam String email) {
//
//        return ResponseEntity.ok(
//                ApiResponse.<CandidateEducationDto>builder()
//                        .success(true)
//                        .message("Education details fetched successfully")
//                        .data(candidateService.getEducationByEmail(email))
//                        .build()
//        );
//    }
//    @GetMapping("/education/search")
//    public ResponseEntity<ApiResponse<List<CandidateEducationDto>>> searchEducation(
//            @RequestParam String keyword) {
//
//        return ResponseEntity.ok(
//                ApiResponse.<List<CandidateEducationDto>>builder()
//                        .success(true)
//                        .message("Education details fetched successfully")
//                        .data(candidateService.searchEducation(keyword))
//                        .build()
//        );
//    }
//    @DeleteMapping("/education/{candidateId}")
//    public ResponseEntity<ApiResponse<String>> deleteEducation(
//            @PathVariable Long candidateId) {
//
//        candidateService.deleteEducation(candidateId);
//
//        return ResponseEntity.ok(
//                ApiResponse.<String>builder()
//                        .success(true)
//                        .message("Education deleted successfully")
//                        .data("Deleted")
//                        .build()
//        );
//    }
}
