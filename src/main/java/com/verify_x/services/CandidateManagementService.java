package com.verify_x.services;

import com.verify_x.dto.*;
import com.verify_x.enums.VerificationStatus;

import java.util.List;

public interface CandidateManagementService {



List<CandidateSummaryDto> getAllCandidates();

    List<CandidateSummaryDto> searchCandidates(String keyword);

    CandidateDetailsDto getCandidateDetails(Long candidateId);

    // HR-side "+ Add Candidate"
    CandidateSummaryDto createCandidate(UserRegistrationDto dto);

    void deleteCandidate(Long candidateId);

    void verifyUan(
            Long candidateId, VerificationStatus status,
            String verifiedBy
    );

    void updateApplicationStatus(Long candidateId,
                                 ApplicationStatusUpdateDto dto,
                                 String reviewedBy);
}
