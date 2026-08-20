package com.verify_x.controller;

import com.verify_x.dto.EmploymentDetailsDto;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.EmploymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employment")
@RequiredArgsConstructor
public class EmploymentController {

    private final EmploymentService employmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveEmploymentDetails(
            @Valid
            @ParameterObject
            @ModelAttribute EmploymentDetailsDto dto) {

        employmentService.saveEmploymentDetails(dto);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Employment details saved successfully.")
                        .data("Success")
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateEmploymentDetails(
            @RequestBody EmploymentDetailsDto dto) {

        employmentService.updateEmploymentDetails(dto);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Employment details updated successfully.")
                        .data("Success")
                        .build()
        );
    }
    @GetMapping("/{candidateId}")
    public ResponseEntity<ApiResponse<EmploymentDetailsDto>>
    getEmploymentByCandidateId(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(
                ApiResponse.<EmploymentDetailsDto>builder()
                        .success(true)
                        .message("Employment details fetched successfully.")
                        .data(
                                employmentService
                                        .getEmploymentDetailsByCandidateId(candidateId)
                        )
                        .build()
        );
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<EmploymentDetailsDto>>
    getEmploymentByEmail(
            @RequestParam String email) {

        return ResponseEntity.ok(
                ApiResponse.<EmploymentDetailsDto>builder()
                        .success(true)
                        .message("Employment details fetched successfully.")
                        .data(
                                employmentService
                                        .getEmploymentDetailsByEmail(email)
                        )
                        .build()
        );
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmploymentDetailsDto>>>
    searchEmployment(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                ApiResponse.<List<EmploymentDetailsDto>>builder()
                        .success(true)
                        .message("Employment details fetched successfully.")
                        .data(
                                employmentService
                                        .searchEmploymentDetails(keyword)
                        )
                        .build()
        );
    }

    @DeleteMapping("/{candidateId}")
    public ResponseEntity<ApiResponse<String>>
    deleteEmployment(
            @PathVariable Long candidateId) {

        employmentService.deleteEmploymentDetails(candidateId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Employment details deleted successfully.")
                        .data("Deleted Successfully")
                        .build()
        );
    }
}
