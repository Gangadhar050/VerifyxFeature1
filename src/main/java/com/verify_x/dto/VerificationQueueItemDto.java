package com.verify_x.dto;

import com.verify_x.enums.ApplicationStatus;
import com.verify_x.enums.CandidateType;
import com.verify_x.enums.TechnicalSkill;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationQueueItemDto {

    private Long candidateId;
//    private String candidateName;
//    private String email;
//    private CandidateType candidateType;
//    private ApplicationStatus applicationStatus;
//
//    private long pendingDocumentsCount;
//    private long rejectedDocumentsCount;
//    private long verifiedDocumentsCount;
//
//    private boolean uanRequired;
//    private boolean uanVerified;
//
//    // true when there is nothing left for HR to check (all docs verified + UAN ok if required)
//    private boolean readyForDecision;
//    private Long id;
    private String candidateName;
    private String email;
//    private String phoneNumber;
    private CandidateType candidateType;
//    private List<TechnicalSkill> skills;
    private long pendingDocumentsCount;
    private String uanNumber;
    private boolean uanVerified;
    private ApplicationStatus applicationStatus;
}
