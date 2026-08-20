package com.verify_x.dto;

import com.verify_x.enums.CandidateType;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import lombok.*;
import com.verify_x.enums.VerificationStatus;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrVerificationRequestDto {

    private Long candidateId;
    private String candidateName;
    private String email;
    private CandidateType candidateType;

    private Integer documentCount;
    private VerificationStatus uanVerificationStatus;
    private DocumentStatus status;
}