package com.verify_x.dto;

import com.verify_x.enums.AppliedRole;
import com.verify_x.enums.CandidateType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDashboardDto {


    private Long candidateId;

    private String candidateName;

    private String email;

    private String phoneNumber;

    private AppliedRole appliedRole;

    private CandidateType candidateType;

    // Document Statistics
    private long totalDocuments;

    private long verifiedDocuments;

    private long pendingDocuments;

    private long rejectedDocuments;
}