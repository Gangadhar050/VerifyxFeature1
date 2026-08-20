package com.verify_x.controller;

import com.verify_x.dto.EducationRequest;
import com.verify_x.dto.EducationResponse;
import com.verify_x.enums.EducationDocumentType;
import com.verify_x.enums.TechnicalSkill;
import com.verify_x.payload.ApiResponse;
//import com.verify_x.serviceImpl.EducationOcrService;
//import com.verify_x.serviceImpl.EducationOcrService;
import com.verify_x.serviceImpl.EducationOcrService;
import com.verify_x.services.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EducationOcrService educationOcrService;


    @PostMapping(value = "/auto-fill/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<EducationResponse>> autoFillFromDocument(
            @PathVariable EducationDocumentType documentType,
            @RequestParam("file") MultipartFile file,
            @ModelAttribute EducationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Details extracted and saved successfully.")
                        .data(educationService.autoFillFromDocument(file, documentType, request))
                        .build()
        );
    }

    @PostMapping(value = "/extract-all", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Auto-extract fields from all uploaded marksheets/certificates together for review before saving")
    public ResponseEntity<ApiResponse<Map<String, Object>>> extractAllDocuments(
            @RequestParam(value = "tenthMarksCard", required = false) MultipartFile tenthMarksCard,
            @RequestParam(value = "twelfthMarksCard", required = false) MultipartFile twelfthMarksCard,
            @RequestParam(value = "degreeCertificate", required = false) MultipartFile degreeCertificate,
            @RequestParam(value = "mastersMarksCard", required = false) MultipartFile mastersMarksCard
//            @RequestParam(value = "mastersDegreeCertificate", required = false) MultipartFile mastersDegreeCertificate

    ) {

        Map<String, Object> combined = educationOcrService.extractAll(
                tenthMarksCard,
                twelfthMarksCard,
                degreeCertificate,
                mastersMarksCard
        );

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Extraction complete. Please review all sections before saving.")
                        .data(combined)
                        .build()
        );
    }
    @PostMapping(value = "/extract-all-save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Extract fields from all uploaded documents and save directly to the database")
    public ResponseEntity<ApiResponse<EducationResponse>> extractAllAndSave(
            @RequestParam(value = "tenthMarksCard", required = false) MultipartFile tenthMarksCard,
            @RequestParam(value = "twelfthMarksCard", required = false) MultipartFile twelfthMarksCard,
            @RequestParam(value = "degreeCertificate", required = false) MultipartFile degreeCertificate,
            @RequestParam(value = "mastersMarksCard", required = false) MultipartFile mastersMarksCard) {

        EducationResponse response = educationService.extractAllAndSave(
                tenthMarksCard, twelfthMarksCard, degreeCertificate, mastersMarksCard
        );

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Details extracted and saved successfully.")
                        .data(response)
                        .build()
        );
    }
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Update existing education details")
    public ResponseEntity<ApiResponse<EducationResponse>> updateEducation(
            @ParameterObject
            @ModelAttribute EducationRequest request,@RequestParam(value = "tenthMarksCard", required = false) MultipartFile tenthMarksCard,
            @RequestParam(value = "twelfthMarksCard", required = false) MultipartFile twelfthMarksCard,
            @RequestParam(value = "degreeCertificate", required = false) MultipartFile degreeCertificate,
            @RequestParam(value = "mastersMarksCard", required = false) MultipartFile mastersMarksCard) {

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Education details updated successfully.")
                        .data(educationService.updateEducation(request, tenthMarksCard, twelfthMarksCard, degreeCertificate, mastersMarksCard))
                        .build()
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get logged-in candidate's education details")
    public ResponseEntity<ApiResponse<EducationResponse>> getMyEducation() {

        return ResponseEntity.ok(
                ApiResponse.<EducationResponse>builder()
                        .success(true)
                        .message("Education details fetched successfully.")
                        .data(educationService.getMyEducation())
                        .build()
        );
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Delete logged-in candidate's education details")
    public ResponseEntity<ApiResponse<String>> deleteEducation() {

        educationService.deleteEducation();

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Education details deleted successfully.")
                        .data("Deleted")
                        .build()
        );
    }
    /**
     * Save Education
     */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('CANDIDATE')")
//    public ResponseEntity<ApiResponse<EducationResponse>> saveEducation(
//            @ModelAttribute EducationRequest request) {
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<EducationResponse>builder()
//                        .success(true)
//                        .message("Education details saved successfully.")
//                        .data(educationService.saveEducation(request))
//                        .build()
//        );
//    }
//
//    /**
//     * Update Education
//     */
//    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('CANDIDATE')")
//    public ResponseEntity<ApiResponse<EducationResponse>> updateEducation(
//            @ModelAttribute EducationRequest request) {
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<EducationResponse>builder()
//                        .success(true)
//                        .message("Education details updated successfully.")
//                        .data(educationService.updateEducation(request))
//                        .build()
//        );
//    }
//
//    /**
//     * Logged-in Candidate Education
//     */
//    @GetMapping("/me")
//    @PreAuthorize("hasRole('CANDIDATE')")
//    public ResponseEntity<ApiResponse<EducationResponse>> getMyEducation() {
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<EducationResponse>builder()
//                        .success(true)
//                        .message("Education details fetched successfully.")
//                        .data(educationService.getMyEducation())
//                        .build()
//        );
//    }
//
//    /**
//     * HR/Admin
//     */
//    @GetMapping("hr/{candidateId}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<ApiResponse<EducationResponse>>
//    getEducationByCandidateId(
//            @PathVariable Long candidateId) {
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<EducationResponse>builder()
//                        .success(true)
//                        .message("Education details fetched successfully.")
//                        .data(educationService.getEducationByCandidateId(candidateId))
//                        .build()
//        );
//    }
//
//    /**
//     * View Uploaded Document
//     */
//    @GetMapping("/hr/{educationId}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<Resource> viewDocument(
//
//            @PathVariable Long educationId,
//
//            @RequestParam EducationDocumentType documentType) {
//
//        Resource resource =
//                educationService.viewDocument(
//                        educationId,
//                        documentType);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "inline")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

//    /**
//     * Get document form data for logged-in candidate (auto-fill)
//     */
//    @GetMapping("/me/document-form")
//    @PreAuthorize("hasRole('CANDIDATE')")
//    public ResponseEntity<ApiResponse<com.verify_x.dto.DocumentFormDto>> getMyDocumentForm(
//            @RequestParam EducationDocumentType documentType) {
//
//        Long educationId = educationService.getMyEducation().getId();
//
//        com.verify_x.dto.DocumentFormDto form =
//                educationService.getDocumentForm(educationId, documentType);
//
//        return ResponseEntity.ok(
//                ApiResponse.<com.verify_x.dto.DocumentFormDto>builder()
//                        .success(true)
//                        .message("Document form fetched successfully.")
//                        .data(form)
//                        .build());
//    }
//
//    /**
//     * HR/Admin: Get document form data by education id (auto-fill)
//     */
//    @GetMapping("/hr/{educationId}/document-form")
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<ApiResponse<com.verify_x.dto.DocumentFormDto>> getDocumentFormByEducationId(
//            @PathVariable Long educationId,
//            @RequestParam EducationDocumentType documentType) {
//
//        com.verify_x.dto.DocumentFormDto form =
//                educationService.getDocumentForm(educationId, documentType);
//
//        return ResponseEntity.ok(
//                ApiResponse.<com.verify_x.dto.DocumentFormDto>builder()
//                        .success(true)
//                        .message("Document form fetched successfully.")
//                        .data(form)
//                        .build());
//    }

    /**
     * Delete Education
     */
//    @DeleteMapping("hr/delete")
//    @PreAuthorize("hasRole('Admin')")
//    public ResponseEntity<ApiResponse<String>> deleteEducation() {
//
//        educationService.deleteEducation();
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<String>builder()
//                        .success(true)
//                        .message("Education deleted successfully.")
//                        .data("Deleted")
//                        .build()
//        );
//    }

//    /**
//     * Get all available technical skills
//     */
//    @GetMapping("/technical-skills")
//    @PreAuthorize("hasRole('CANDIDATE')")
//    public ResponseEntity<ApiResponse<List<TechnicalSkill>>> getTechnicalSkills() {
//
//        return ResponseEntity.ok(
//
//                ApiResponse.<List<TechnicalSkill>>builder()
//                        .success(true)
//                        .message("Technical skills fetched successfully.")
//                        .data(Arrays.asList(TechnicalSkill.values()))
//                        .build()
//        );
//    }
//    @GetMapping("/technical-skills")
//    @PreAuthorize("hasRole('CANDIDATE') or hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<List<TechnicalSkill>>> getTechnicalSkills() {
//
//        return ResponseEntity.ok(
//                ApiResponse.<List<TechnicalSkill>>builder()
//                        .success(true)
//                        .message("Candidate technical skills fetched successfully.")
//                        .data(educationService.getMyTechnicalSkills())
//                        .build());
//    }
//    @PostMapping(value = "/extract/{documentType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<EducationOcrExtractResponse> extractFromDocument(
//            @PathVariable EducationDocumentType documentType,
//            @RequestParam("file") MultipartFile file) {
//
//        EducationOcrExtractResponse response = educationOcrService.extract(file, documentType);
//        return ResponseEntity.ok(response);
//    }

}