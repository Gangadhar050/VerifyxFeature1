package com.verify_x.serviceImpl;

import com.verify_x.dto.OfferLetterRequestDTO;

import com.verify_x.dto.OfferLetterResponseDTO;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.OfferLetter;
import com.verify_x.exception.ResourceNotFoundException;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.repository.OfferLetterRepository;
import com.verify_x.services.OfferLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferLetterServiceImpl
        implements OfferLetterService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
            );

    private final OfferLetterRepository offerLetterRepository;

    private final CandidateRepository candidateRepository;


    @Override
    public OfferLetterResponseDTO createOfferLetter(
            Long candidateId,
            OfferLetterRequestDTO request,
            MultipartFile file
    ) {

        Candidate candidate =
                candidateRepository.findById(candidateId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Candidate not found with id: "
                                                + candidateId
                                )
                        );

        validateDates(request);

        validateFile(file);

        OfferLetter offerLetter =
                OfferLetter.builder()
                        .candidate(candidate)
                        .companyName(
                                request.getCompanyName().trim()
                        )
                        .designation(
                                cleanValue(request.getDesignation())
                        )
                        .ctc(request.getCtc())
                        .offerDate(request.getOfferDate())
                        .joiningDate(request.getJoiningDate())
                        .referenceNumber(
                                cleanValue(
                                        request.getReferenceNumber()
                                )
                        )
                        .build();

        if (file != null && !file.isEmpty()) {
            storeFile(offerLetter, file);
        }

        OfferLetter saved =
                offerLetterRepository.save(offerLetter);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferLetterResponseDTO> getOfferLetters(
            Long candidateId
    ) {

        validateCandidateExists(candidateId);

        return offerLetterRepository
                .findByCandidateIdOrderByCreatedAtDesc(candidateId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OfferLetterResponseDTO getOfferLetter(
            Long candidateId,
            Long offerLetterId
    ) {

        OfferLetter offerLetter =
                getOfferLetterEntity(
                        candidateId,
                        offerLetterId
                );

        return mapToResponse(offerLetter);
    }

    @Override
    public OfferLetterResponseDTO updateOfferLetter(
            Long candidateId,
            Long offerLetterId,
            OfferLetterRequestDTO request,
            MultipartFile file
    ) {

        OfferLetter offerLetter =
                getOfferLetterEntity(
                        candidateId,
                        offerLetterId
                );

        validateDates(request);

        offerLetter.setCompanyName(
                request.getCompanyName().trim()
        );

        offerLetter.setDesignation(
                cleanValue(request.getDesignation())
        );

        offerLetter.setCtc(request.getCtc());

        offerLetter.setOfferDate(
                request.getOfferDate()
        );

        offerLetter.setJoiningDate(
                request.getJoiningDate()
        );

        offerLetter.setReferenceNumber(
                cleanValue(
                        request.getReferenceNumber()
                )
        );

        // File is optional during update
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            storeFile(offerLetter, file);
        }

        OfferLetter updated =
                offerLetterRepository.save(offerLetter);

        return mapToResponse(updated);
    }

    @Override
    public void deleteOfferLetter(
            Long candidateId,
            Long offerLetterId
    ) {

        OfferLetter offerLetter =
                getOfferLetterEntity(
                        candidateId,
                        offerLetterId
                );

        offerLetterRepository.delete(offerLetter);
    }


    @Override
    @Transactional(readOnly = true)
    public DocumentDownload getDocument(
            Long candidateId,
            Long offerLetterId
    ) {

        OfferLetter offerLetter =
                getOfferLetterEntity(
                        candidateId,
                        offerLetterId
                );

        if (offerLetter.getDocumentData() == null
                || offerLetter.getDocumentData().length == 0) {

            throw new ResourceNotFoundException(
                    "No document uploaded for offer letter id: "
                            + offerLetterId
            );
        }

        return new DocumentDownload(
                offerLetter.getDocumentData(),
                offerLetter.getDocumentFileName(),
                offerLetter.getDocumentContentType()
        );
    }

    private OfferLetter getOfferLetterEntity(
            Long candidateId,
            Long offerLetterId
    ) {

        return offerLetterRepository
                .findByIdAndCandidateId(
                        offerLetterId,
                        candidateId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer letter not found with id: "
                                        + offerLetterId
                                        + " for candidate: "
                                        + candidateId
                        )
                );
    }

    private void validateCandidateExists(
            Long candidateId
    ) {

        if (!candidateRepository.existsById(candidateId)) {

            throw new ResourceNotFoundException(
                    "Candidate not found with id: "
                            + candidateId
            );
        }
    }


    private void validateDates(
            OfferLetterRequestDTO request
    ) {

        if (request.getOfferDate() != null
                && request.getJoiningDate() != null
                && request.getJoiningDate()
                .isBefore(request.getOfferDate())) {

            throw new IllegalArgumentException(
                    "Joining date cannot be before offer date"
            );
        }
    }

    private void validateFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Offer letter document is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size cannot exceed 5 MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(
                contentType.toLowerCase()
        )) {

            throw new IllegalArgumentException(
                    "Only PDF, JPG and PNG files are allowed"
            );
        }
    }

    private void storeFile(
            OfferLetter offerLetter,
            MultipartFile file
    ) {

        try {

            offerLetter.setDocumentData(
                    file.getBytes()
            );

            offerLetter.setDocumentFileName(
                    sanitizeFileName(
                            file.getOriginalFilename()
                    )
            );

            offerLetter.setDocumentContentType(
                    file.getContentType()
            );

            offerLetter.setDocumentSize(
                    file.getSize()
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to read offer letter document",
                    e
            );
        }
    }


    private OfferLetterResponseDTO mapToResponse(
            OfferLetter offerLetter
    ) {

        return OfferLetterResponseDTO.builder()
                .id(offerLetter.getId())
                .candidateId(
                        offerLetter.getCandidate().getId()
                )
                .companyName(
                        offerLetter.getCompanyName()
                )
                .designation(
                        offerLetter.getDesignation()
                )
                .ctc(offerLetter.getCtc())
                .offerDate(
                        offerLetter.getOfferDate()
                )
                .joiningDate(
                        offerLetter.getJoiningDate()
                )
                .referenceNumber(
                        offerLetter.getReferenceNumber()
                )
                .documentFileName(
                        offerLetter.getDocumentFileName()
                )
                .documentContentType(
                        offerLetter.getDocumentContentType()
                )
                .documentSize(
                        offerLetter.getDocumentSize()
                )
                .documentAvailable(
                        offerLetter.getDocumentData() != null
                                && offerLetter.getDocumentData().length > 0
                )
                .createdAt(
                        offerLetter.getCreatedAt()
                )
                .updatedAt(
                        offerLetter.getUpdatedAt()
                )
                .build();
    }


    private String cleanValue(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String sanitizeFileName(
            String fileName
    ) {

        if (fileName == null || fileName.isBlank()) {
            return "offer-letter";
        }

        return fileName
                .replace("\\", "_")
                .replace("/", "_")
                .replace("..", "_");
    }
}