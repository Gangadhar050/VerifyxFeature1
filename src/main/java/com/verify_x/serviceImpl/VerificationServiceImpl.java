package com.verify_x.serviceImpl;

import com.verify_x.dto.CandidateDocumentDto;
import com.verify_x.dto.CandidateSummaryDto;
import com.verify_x.dto.VerificationQueueItemDto;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.entity.Employment;
import com.verify_x.enums.ApplicationStatus;
import com.verify_x.enums.CandidateType;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.repository.EducationRepository;
import com.verify_x.repository.EmploymentRepository;
import com.verify_x.services.VerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationServiceImpl implements VerificationService {

private final CandidateRepository candidateRepository;
    private final EmploymentRepository employmentRepository;
    private final CandidateDocumentRepository candidateDocumentRepository;
    private final EducationRepository educationRepository;

    private VerificationQueueItemDto mapToSummary(Candidate candidate) {
        Employment employment = employmentRepository.findByCandidate(candidate).orElse(null);

        List<CandidateDocument> documents =
                candidateDocumentRepository.findByCandidate(candidate);

        long pending = documents.stream()
                .filter(d -> d.getStatus() == DocumentStatus.PENDING)
                .count();

        return VerificationQueueItemDto.builder()
                .candidateId(candidate.getId())
                .candidateName(candidate.getUsername())
                .email(candidate.getEmail())
                .candidateType(candidate.getCandidateType())
                .pendingDocumentsCount(pending)
                .uanNumber(employment != null ? employment.getUanNumber() : null)
                .uanVerified(employment != null &&
                        Boolean.TRUE.equals(employment.getUanVerified()))
                .applicationStatus(candidate.getApplicationStatus())
                .build();
    }
//    private VerificationQueueItemDto mapDocumentToDto(CandidateDocument document) {
//        return VerificationQueueItemDto.builder()
//                .candidateId(document.getId())
//                .documentType(document.getDocumentType())
//                .fileName(document.getFileName())
//                .contentType(document.getContentType())
//                .status(document.getStatus())
//                .rejectionReason(document.getRejectionReason())
//                .uploadedAt(document.getUploadedAt())
//                .updatedAt(document.getUpdatedAt())
//                .build();
//    }
@Override
public List<VerificationQueueItemDto> getVerificationQueue() {

    return candidateRepository.findAll()
            .stream()
            .filter(candidate ->
                    candidate.getApplicationStatus() != ApplicationStatus.APPROVED
                            && candidate.getApplicationStatus() != ApplicationStatus.REJECTED
            )
            .map(this::mapToSummary)
            .toList();
}


}






//    private final CandidateRepository candidateRepository;
//    private final CandidateDocumentRepository candidateDocumentRepository;
//    private final EmploymentRepository employmentRepository;
//
//    @Override
//    public List<VerificationQueueItemDto> getVerificationQueue() {
//
//        return candidateRepository.findAll()
//                .stream()
//                .filter(candidate -> candidate.getApplicationStatus() != ApplicationStatus.APPROVED
//                        && candidate.getApplicationStatus() != ApplicationStatus.REJECTED)
//                .map(this::toQueueItem)
//                .toList();
//    }
//
//    private VerificationQueueItemDto toQueueItem(Candidate candidate) {
//
//        List<CandidateDocument> documents = candidateDocumentRepository.findByCandidate(candidate);
//
//        long pending = documents.stream().filter(d -> d.getStatus() == DocumentStatus.PENDING).count();
//        long rejected = documents.stream().filter(d -> d.getStatus() == DocumentStatus.REJECTED).count();
//        long verified = documents.stream().filter(d -> d.getStatus() == DocumentStatus.VERIFIED).count();
//
//        Employment employment = employmentRepository.findByCandidate(candidate).orElse(null);
//
//        boolean uanRequired = candidate.getCandidateType() == CandidateType.EXPERIENCED;
//        boolean uanVerified = employment != null && Boolean.TRUE.equals(employment.getUanVerified());
//
//        boolean readyForDecision = pending == 0 && rejected == 0
//                && (!uanRequired || uanVerified)
//                && !documents.isEmpty();
//
//        return VerificationQueueItemDto.builder()
//
//                .candidateId(candidate.getId())
//                .candidateName(candidate.getUsername())
//                .email(candidate.getEmail())
//                .candidateType(candidate.getCandidateType())
//                .applicationStatus(candidate.getApplicationStatus())
//                .pendingDocumentsCount(pending)
////                .rejectedDocumentsCount(rejected)
////                .verifiedDocumentsCount(verified)
////                .uanRequired(uanRequired)
//                .uanVerified(uanVerified)
////                .readyForDecision(readyForDecision)
//                .build();
//    }