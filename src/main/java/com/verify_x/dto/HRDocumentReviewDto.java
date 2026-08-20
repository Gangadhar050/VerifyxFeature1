package com.verify_x.dto;

import com.verify_x.enums.ApplicationStatus;
import com.verify_x.enums.CandidateType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRDocumentReviewDto {

    private Long candidateId;

    private String candidateName;

    private String email;


//    private CandidateType candidateType;

    private ApplicationStatus applicationStatus;

    private String panNumber;

    private String uanNumber;

    private String holdingOfferLetter;

    private boolean uanVerified;

//    private long totalDocuments;

    private long verifiedDocuments;

    private long pendingDocuments;

    private long rejectedDocuments;

    private UanVerificationDto uanVerification;

    private List<CriticalDocumentDto> criticalDocuments;

    private List<DocumentReviewItemDto> documents;

}