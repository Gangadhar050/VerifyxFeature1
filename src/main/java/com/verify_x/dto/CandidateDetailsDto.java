package com.verify_x.dto;

import com.verify_x.enums.ApplicationStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDetailsDto {

    private CandidateProfileDto profile;

    // Changed from CandidateEducationDto
    private EducationResponse education;

    private EmploymentDetailsDto employment;

    private List<CandidateDocumentDto> documents;

    private ApplicationStatus applicationStatus;

    private String remarks;

    private boolean uanVerified;

    private String uanVerifiedBy;
}