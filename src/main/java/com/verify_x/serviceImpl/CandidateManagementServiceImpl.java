package com.verify_x.serviceImpl;

import com.verify_x.dto.ApplicationStatusUpdateDto;
import com.verify_x.dto.CandidateDetailsDto;
import com.verify_x.dto.CandidateDocumentDto;
import com.verify_x.dto.CandidateProfileDto;
import com.verify_x.dto.CandidateSummaryDto;
import com.verify_x.dto.EducationResponse;
import com.verify_x.dto.EmploymentDetailsDto;
import com.verify_x.dto.UserRegistrationDto;

import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.entity.Education;
import com.verify_x.entity.Employment;

import com.verify_x.enums.ApplicationStatus;
import com.verify_x.enums.CandidateType;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import com.verify_x.enums.Role;
import com.verify_x.enums.VerificationStatus;

import com.verify_x.exception.BadRequestException;
import com.verify_x.exception.ResourceNotFoundException;

import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.repository.EducationRepository;
import com.verify_x.repository.EmploymentRepository;

import com.verify_x.services.CandidateManagementService;
import com.verify_x.services.EmailService;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CandidateManagementServiceImpl
        implements CandidateManagementService {

    private final CandidateRepository candidateRepository;

    private final EmploymentRepository employmentRepository;

    private final CandidateDocumentRepository candidateDocumentRepository;

    private final EducationRepository educationRepository;

    private final EmailService emailService;


    //map candidate entity to summary dto
    private CandidateSummaryDto mapToSummary(
            Candidate candidate) {

        Employment employment =
                employmentRepository
                        .findByCandidate(candidate)
                        .orElse(null);

        return CandidateSummaryDto.builder()

                .id(candidate.getId())

                .fullName(candidate.getUsername())

                .email(candidate.getEmail())

                .phoneNumber(candidate.getPhoneNumber())

                .candidateType(candidate.getCandidateType())

                // Technical skills are kept for candidate
                // profile/management, NOT registration.
                .skills(
                        candidate.getTechnicalSkills() != null
                                ? new ArrayList<>(candidate.getTechnicalSkills())
                                : Collections.emptyList()
                )

                .uanNumber(
                        employment != null
                                ? employment.getUanNumber()
                                : null
                )

                .uanVerified(
                        employment != null
                                && Boolean.TRUE.equals(
                                        employment.getUanVerified()
                                )
                )

                .applicationStatus(
                        candidate.getApplicationStatus()
                )

                .build();
    }


   //map candidate document entity to dto
    private CandidateDocumentDto mapDocumentToDto(
            CandidateDocument document) {

        return CandidateDocumentDto.builder()

                .id(document.getId())

                .documentType(document.getDocumentType())

                .fileName(document.getFileName())

                .contentType(document.getContentType())

                .status(document.getStatus())

                .rejectionReason(document.getRejectionReason())

                .uploadedAt(document.getUploadedAt())

                .updatedAt(document.getUpdatedAt())

                .build();
    }


    //get all candidates
    @Override
    public List<CandidateSummaryDto> getAllCandidates() {

        return candidateRepository
                .findAll()
                .stream()
                .map(this::mapToSummary)
                .toList();
    }


   //search candidates by keyword
    @Override
    public List<CandidateSummaryDto> searchCandidates(
            String keyword) {

        if (keyword == null ||
                keyword.trim().isEmpty()) {

            return getAllCandidates();
        }

        String searchKeyword =
                keyword.trim();

        return candidateRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        searchKeyword,
                        searchKeyword
                )
                .stream()
                .map(this::mapToSummary)
                .toList();
    }


   //get candidate details by id
    @Override
    public CandidateDetailsDto getCandidateDetails(
            Long candidateId) {

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Candidate",
                                        candidateId
                                )
                        );

//employment details for the candidate
        Employment employment =
                employmentRepository
                        .findByCandidate(candidate)
                        .orElse(null);


      //documents for the candidate
        List<CandidateDocumentDto> documents =
                candidateDocumentRepository
                        .findByCandidate(candidate)
                        .stream()
                        .map(this::mapDocumentToDto)
                        .toList();

//profile details for the candidate
        CandidateProfileDto profile =
                CandidateProfileDto.builder()
                        .username(candidate.getUsername())
                        .email(candidate.getEmail())
                        .phoneNumber(candidate.getPhoneNumber())
                        .address(candidate.getAddress())
                        .panNumber(candidate.getPanNumber())
                        .aadhaarNumber(candidate.getAadhaarNumber())
                        .candidateType(candidate.getCandidateType())

                        .build();

        //education details for the candidate
        Education educationEntity =
                educationRepository
                        .findByCandidate(candidate)
                        .orElse(null);

        EducationResponse education = null;

        if (educationEntity != null) {

            education =
                    EducationResponse.builder()

                            .id(educationEntity.getId())

                            .tenthSchoolName(educationEntity.getTenthSchoolName())

                            .tenthBoard(educationEntity.getTenthBoard())

                            .tenthSchoolLocation(educationEntity.getTenthSchoolLocation())

                            .tenthRollNumber(educationEntity.getTenthRollNumber())

                            .tenthPassingYear(educationEntity.getTenthPassingYear())

                            .tenthPercentage(educationEntity.getTenthPercentage())

                            .tenthMarksCardName(educationEntity.getTenthMarksCardName())

                            .twelfthInstitutionName(educationEntity.getTwelfthInstitutionName())

                            .twelfthBoardUniversity(educationEntity.getTwelfthBoardUniversity())

                            .twelfthRegistrationNumber(educationEntity.getTwelfthRegistrationNumber())

                            .twelfthPassingYear(educationEntity.getTwelfthPassingYear())

                            .twelfthPercentage(educationEntity.getTwelfthPercentage())

                            .twelfthMarksCardName(educationEntity.getTwelfthMarksCardName())


                            .degreeName(educationEntity.getDegreeName())

                            .specialization(educationEntity.getSpecialization())

                            .collegeName(educationEntity.getCollegeName())

                            .universityName(educationEntity.getUniversityName())

                            .usnNumber(educationEntity.getUsnNumber())

                            .degreeStartYear(educationEntity.getDegreeStartYear())

                            .degreeEndYear(educationEntity.getDegreeEndYear())

                            .degreePercentage(educationEntity.getDegreePercentage())
                            .degreeCertificateName(educationEntity.getDegreeCertificateName())

                            .mastersDegree(educationEntity.getMastersDegree())

                            .mastersSpecialization(educationEntity.getMastersSpecialization())

                            .mastersCollege(educationEntity.getMastersCollege())

                            .mastersUniversity(educationEntity.getMastersUniversity())

                            .mastersRegistrationNumber(educationEntity.getMastersRegistrationNumber())

                            .mastersStartYear(educationEntity.getMastersStartYear())

                            .mastersEndYear(educationEntity.getMastersEndYear())

                            .mastersPercentage(educationEntity.getMastersPercentage())

                            .mastersMarksCardName(educationEntity.getMastersMarksCardName())

                            .build();
        }


        //employment details for the candidate
        EmploymentDetailsDto employmentDto = null;

        if (employment != null) {

            employmentDto =
                    EmploymentDetailsDto.builder()

                            .previousCompanyName(employment.getPreviousCompanyName())

                            .previousDesignation(employment.getPreviousDesignation())

                            .totalExperience(employment.getTotalExperience())

                            .lastCTC(employment.getLastCTC())

                            .lastWorkingDay(employment.getLastWorkingDay())

                            .uanNumber(employment.getUanNumber())

                            .employmentStatus(employment.getEmploymentStatus())

                            .currentCompany(employment.getCurrentCompany())

                            .currentDesignation(employment.getCurrentDesignation())

                            .currentCTC(employment.getCurrentCTC())

                            .noticePeriod(employment.getNoticePeriod())

                            .offerLetterStatus(employment.getOfferLetterStatus())

//                            .offerCompanyName(employment.getOfferCompanyName())
//
//                            .offeredCTC(employment.getOfferedCTC())
//
//                            .joiningDate(employment.getJoiningDate())
//
//                            .offerReferenceNumber(employment.getOfferReferenceNumber())

                            .build();
        }


        //final response dto containing all candidate details
        return CandidateDetailsDto.builder()

                .profile(profile)

                .education(education)

                .employment(employmentDto)

                .documents(documents)

                .applicationStatus(
                        candidate.getApplicationStatus())

                .remarks(candidate.getRemarks())

                .uanVerified(
                        employment != null
                                && Boolean.TRUE.equals(
                                        employment.getUanVerified()))

                .uanVerifiedBy(
                        employment != null
                                ? employment.getUanVerifiedBy() : null)

                .build();
    }

    //create a new candidate
    @Override
    public CandidateSummaryDto createCandidate(
            UserRegistrationDto dto) {

      //check if email already exists
        if (candidateRepository
                .existsByEmail(dto.getEmail())) {

            throw new BadRequestException(
                    "Email already exists."
            );
        }


        //check if phone number already exists
        if (candidateRepository
                .existsByPhoneNumber(dto.getPhoneNumber())) {

            throw new BadRequestException(
                    "Phone number already exists."
            );
        }

        //check if username already exists
        Candidate candidate =
                Candidate.builder()
                        .username(dto.getUsername())
                        .email(dto.getEmail())
                        .phoneNumber(dto.getPhoneNumber())
                        .password(dto.getPassword())
                        .appliedRole(dto.getAppliedRole())
                        .candidateType(dto.getCandidateType())
                        .role(Role.CANDIDATE)
                        .applicationStatus(
                                ApplicationStatus.PENDING_VERIFICATION)
                        .build();

        candidateRepository.save(candidate);

        log.info("Candidate {} created successfully.",
                candidate.getEmail());

        return mapToSummary(candidate);
    }


    @Override
    public void deleteCandidate(
            Long candidateId) {

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Candidate",
                                        candidateId ));


        candidateDocumentRepository
                .findByCandidate(candidate)
                .forEach(
                        candidateDocumentRepository::delete);


        employmentRepository
                .findByCandidate(candidate)
                .ifPresent(
                        employmentRepository::delete);

        candidateRepository.delete(candidate);


        log.info(
                "Candidate {} deleted by HR.",
                candidateId
        );
    }


  //verify uan
    @Override
    public void verifyUan(
            Long candidateId,
            VerificationStatus status,
            String verifiedBy) {

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Candidate",
                                        candidateId));


        Employment employment =
                employmentRepository
                        .findByCandidate(candidate)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Employment details not found."));


        String uan = employment.getUanNumber();


        if (uan == null ||
                !uan.matches("^\\d{12}$")) {

            throw new BadRequestException(
                    "UAN number must contain exactly 12 digits before verification.");
        }


        if (status == null) {

            throw new BadRequestException(
                    "Verification status is required.");
        }


        switch (status) {

            case VERIFIED -> {

                employment.setUanVerified(true);

                employment.setUanVerificationStatus(
                        VerificationStatus.VERIFIED);

                employment.setUanVerifiedBy(
                        verifiedBy != null
                                ? verifiedBy
                                : "HR");

                employment.setUanVerifiedAt(
                        LocalDateTime.now());
            }


            case REJECTED -> {

                employment.setUanVerified(false);

                employment.setUanVerificationStatus(
                        VerificationStatus.REJECTED);

                employment.setUanVerifiedBy(
                        verifiedBy != null
                                ? verifiedBy
                                : "HR");

                employment.setUanVerifiedAt(
                        LocalDateTime.now());
            }

            case PENDING -> {

                employment.setUanVerified(false);

                employment.setUanVerificationStatus(
                        VerificationStatus.PENDING
                );

                employment.setUanVerifiedBy(null);

                employment.setUanVerifiedAt(null);
            }
        }


        employmentRepository.save(employment);


        log.info(
                "UAN status for candidate {} set to {} by {}",
                candidateId,
                status,
                verifiedBy
        );
    }


   //update application status
    @Override
    public void updateApplicationStatus(
            Long candidateId,
            ApplicationStatusUpdateDto dto,
            String reviewedBy) {

        Candidate candidate =
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Candidate",
                                        candidateId)
                        );


        if (dto == null ||
                dto.getStatus() == null) {

            throw new BadRequestException(
                    "Application status is required.");
        }
        if (dto.getStatus() ==
                ApplicationStatus.APPROVED) {

            Employment employment =
                    employmentRepository
                            .findByCandidate(candidate)
                            .orElse(null);

            boolean uanOk =
                    candidate.getCandidateType()
                            != CandidateType.EXPERIENCED
                            ||
                            (employment != null
                                            &&
                                    Boolean.TRUE.equals(
                                            employment.getUanVerified())
                            );


            if (!uanOk) {

                throw new BadRequestException(
                        "Cannot approve. UAN must be verified.");
            }

            List<CandidateDocument> documents =
                    candidateDocumentRepository
                            .findByCandidate(candidate);

            Set<DocumentType> requiredDocuments;


            if (candidate.getCandidateType()
                    == CandidateType.FRESHER) {

                requiredDocuments =
                        Set.of(
                                DocumentType.RESUME,
                                DocumentType.PAN_CARD);

            } else {

                requiredDocuments =
                        Set.of(
                                DocumentType.RESUME,
                                DocumentType.PAN_CARD,
                                DocumentType.CURRENT_OFFERLETTER,
                                DocumentType.SALARY_SLIP,
                                DocumentType.UAN_PROOF
                        );
            }
            //rejacted docs
            boolean hasRejectedDocs =
                    documents.stream()
                            .anyMatch(
                                    doc ->
                                            doc.getStatus()
                                                    == DocumentStatus.REJECTED);


            if (hasRejectedDocs) {

                throw new BadRequestException(
                        "Cannot approve. Candidate has rejected documents.");
            }
            //pending docs
            boolean hasPendingDocs =
                    documents.stream()
                            .anyMatch(
                                    doc ->
                                            doc.getStatus()
                                                    == DocumentStatus.PENDING);


            if (hasPendingDocs) {

                throw new BadRequestException(
                        "Cannot approve. Some documents are still pending verification.");
            }


          //missing req docs
            boolean missingRequiredDocuments =
                    requiredDocuments
                            .stream()
                            .anyMatch(
                                    requiredType ->
                                            documents.stream()
                                                    .noneMatch(
                                                            doc ->
                                                                    doc.getDocumentType()
                                                                            == requiredType));


            if (missingRequiredDocuments) {

                throw new BadRequestException(
                        "Cannot approve. Required documents are missing.");
            }
        }

//update status
        candidate.setApplicationStatus(
                dto.getStatus()
        );

        candidate.setRemarks(
                dto.getRemarks()
        );

        candidateRepository.save(candidate);
        //send mail
        try {

            switch (dto.getStatus()) {

                case APPROVED ->

                        emailService
                                .sendApplicationApprovedEmail(
                                        candidate.getEmail(),
                                        candidate.getUsername(),
                                        dto.getRemarks());


                case REJECTED ->

                        emailService
                                .sendApplicationRejectedEmail(
                                        candidate.getEmail(),
                                        candidate.getUsername(),
                                        dto.getRemarks());


                case RE_UPLOAD_REQUIRED ->

                        emailService
                                .sendReUploadRequestEmail(
                                        candidate.getEmail(),
                                        candidate.getUsername(),
                                        dto.getRemarks());


                default -> {
                    // No email required.
                }
            }

        } catch (Exception mailException) {

            log.warn(
                    "Application status updated for candidate {}, but email notification failed: {}",
                    candidateId,
                    mailException.getMessage()
            );
        }


        log.info(
                "Application status for candidate {} set to {} by {}",
                candidateId,
                dto.getStatus(),
                reviewedBy
        );
    }


   //internal application ststua update
    private void updateApplicationStatus(
            Candidate candidate) {

        List<CandidateDocument> documents =
                candidateDocumentRepository
                        .findByCandidate(candidate);

        boolean rejected =
                documents.stream()
                        .anyMatch(
                                document ->
                                        document.getStatus()
                                                == DocumentStatus.REJECTED);
        boolean pending =
                documents.stream()
                        .anyMatch(
                                document ->
                                        document.getStatus()
                                                == DocumentStatus.PENDING);

        if (rejected) {

            candidate.setApplicationStatus(
                    ApplicationStatus.RE_UPLOAD_REQUIRED);

        } else if (pending) {

            candidate.setApplicationStatus(
                    ApplicationStatus.PENDING_VERIFICATION);

        } else {

            candidate.setApplicationStatus(
                    ApplicationStatus.DOCUMENTS_VERIFIED);
        }
        candidateRepository.save(candidate);
    }
}