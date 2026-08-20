package com.verify_x.serviceImpl;

import com.verify_x.dto.CriticalDocumentDto;
import com.verify_x.dto.DocumentReviewItemDto;
import com.verify_x.dto.HRDocumentReviewDto;
import com.verify_x.dto.UanVerificationDto;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.entity.Employment;
import com.verify_x.enums.*;
import com.verify_x.exception.BadRequestException;
import com.verify_x.exception.ResourceNotFoundException;
import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.repository.EmploymentRepository;
import com.verify_x.services.HRDocumentReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HRDocumentReviewServiceImpl implements HRDocumentReviewService {

    private final CandidateRepository candidateRepository;
    private final CandidateDocumentRepository candidateDocumentRepository;
    private final EmploymentRepository employmentRepository;

    @Override
    public HRDocumentReviewDto getCandidateDocuments(Long candidateId) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Candidate", candidateId));

        Employment employment =
                employmentRepository.findByCandidate(candidate).orElse(null);

        UanVerificationDto uanVerification = null;

        if (employment != null) {

            uanVerification = UanVerificationDto.builder()
                    .uanNumber(employment.getUanNumber())
                    .status(Boolean.TRUE.equals(employment.getUanVerified())
                            ? "Verified by HR"
                            : "Pending HR Validation")
                    .verifiedMessage(Boolean.TRUE.equals(employment.getUanVerified())
                            ? "Verified by "
                              + employment.getUanVerifiedBy()
                              + " on "
                              + employment.getUanVerifiedAt()
                            : null)
                    .build();
        }

        String holdingOfferLetter = "No";

        if (employment != null
                && employment.getOfferLetterStatus() == OfferLetterStatus.HOLDING_OFFER_LETTER) {

            holdingOfferLetter = "Yes";
        }

        List<CandidateDocument> candidateDocuments =
                candidateDocumentRepository.findByCandidate(candidate);
        List<CriticalDocumentDto> criticalDocuments =
                candidateDocuments.stream()

                        .filter(document ->

                                document.getDocumentType() == DocumentType.PAN_CARD ||

                                        document.getDocumentType() == DocumentType.UAN_PROOF ||

                                        document.getDocumentType() == DocumentType.CURRENT_OFFERLETTER)

                        .map(document -> {

                            String message;

                            switch (document.getStatus()) {

                                case VERIFIED:
                                    message = "Validated by HR";
                                    break;

                                case PENDING:
                                    message = "Pending HR Validation";
                                    break;

                                default:
                                    message = "Rejected by HR";
                            }

                            return CriticalDocumentDto.builder()

                                    .documentType(document.getDocumentType())

                                    .status(document.getStatus())

                                    .statusMessage(message)

                                    .build();
                        })

                        .toList();

        long total = candidateDocuments.size();

        long verified = candidateDocuments.stream()
                .filter(d -> d.getStatus() == DocumentStatus.VERIFIED)
                .count();

        long pending = candidateDocuments.stream()
                .filter(d -> d.getStatus() == DocumentStatus.PENDING)
                .count();

        long rejected = candidateDocuments.stream()
                .filter(d -> d.getStatus() == DocumentStatus.REJECTED)
                .count();

        List<DocumentReviewItemDto> documents =
                candidateDocuments.stream()
                        .map(document -> DocumentReviewItemDto.builder()
                                .documentId(document.getId())
                                .documentType(document.getDocumentType())
                                .fileName(document.getFileName())
                                .contentType(document.getContentType())
                                .status(document.getStatus())
                                .rejectionReason(document.getRejectionReason())
                                .updatedAt(document.getUpdatedAt())
                                .build())
                        .toList();

        return HRDocumentReviewDto.builder()

                .candidateId(candidate.getId())

                .candidateName(candidate.getUsername())

                .email(candidate.getEmail())

                .applicationStatus(candidate.getApplicationStatus())

                .panNumber(candidate.getPanNumber())

                .uanNumber(employment != null ? employment.getUanNumber() : null)

                .holdingOfferLetter(holdingOfferLetter)

                .uanVerified(employment != null &&
                        Boolean.TRUE.equals(employment.getUanVerified()))

                .uanVerification(uanVerification)

                .verifiedDocuments(verified)

                .pendingDocuments(pending)

                .rejectedDocuments(rejected)

                .documents(documents)

                .criticalDocuments(criticalDocuments)

                .build();
    }
    @Override
    public Resource viewDocument(Long documentId) {

        CandidateDocument document = candidateDocumentRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", documentId));

        if (document.getDocumentData() == null || document.getDocumentData().length == 0) {
            throw new ResourceNotFoundException("Document file not found.");
        }

        return new ByteArrayResource(document.getDocumentData());
    }

    public CandidateDocument getDocumentEntity(Long documentId) {

        return candidateDocumentRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", documentId));
    }


    @Override
    public void verifyDocument(Long documentId) {

        CandidateDocument document =
                candidateDocumentRepository.findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Document", documentId));

        document.setStatus(DocumentStatus.VERIFIED);
        document.setRejectionReason(null);

        candidateDocumentRepository.save(document);

        updateCandidateStatus(document.getCandidate());
    }

    @Override
    public void rejectDocument(Long documentId,
                               String reason) {

        CandidateDocument document =
                candidateDocumentRepository.findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Document", documentId));

        if (reason == null || reason.isBlank()) {
            throw new BadRequestException("Rejection reason is required.");
        }

        document.setStatus(DocumentStatus.REJECTED);
        document.setRejectionReason(reason);

        candidateDocumentRepository.save(document);

        updateCandidateStatus(document.getCandidate());
    }

    /**
     * Automatically update candidate application status.
     */
    private void updateCandidateStatus(Candidate candidate) {

        List<CandidateDocument> documents =
                candidateDocumentRepository.findByCandidate(candidate);

        boolean rejected = documents.stream()
                .anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED);

        boolean pending = documents.stream()
                .anyMatch(d -> d.getStatus() == DocumentStatus.PENDING);

        if (rejected) {

            candidate.setApplicationStatus(
                    ApplicationStatus.RE_UPLOAD_REQUIRED);

        } else if (!pending) {

            if (candidate.getCandidateType() ==
                    CandidateType.EXPERIENCED) {

                Employment employment =
                        employmentRepository.findByCandidate(candidate)
                                .orElse(null);

                if (employment != null &&
                        Boolean.TRUE.equals(employment.getUanVerified())) {

                    candidate.setApplicationStatus(
                            ApplicationStatus.DOCUMENTS_VERIFIED);

                } else {

                    candidate.setApplicationStatus(
                            ApplicationStatus.PENDING_VERIFICATION);
                }

            } else {
                candidate.setApplicationStatus(
                        ApplicationStatus.DOCUMENTS_VERIFIED);
            }

        } else {

            candidate.setApplicationStatus(
                    ApplicationStatus.PENDING_VERIFICATION);
        }

        candidateRepository.save(candidate);
    }

}