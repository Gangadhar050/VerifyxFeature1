package com.verify_x.serviceTest;


import com.verify_x.dto.*;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.entity.Employment;
import com.verify_x.enums.*;
import com.verify_x.exception.BadRequestException;
import com.verify_x.exception.ResourceNotFoundException;
import com.verify_x.jwt.UserPrincipal;
import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.repository.CandidateRepository;

import com.verify_x.serviceImpl.CandidateDocumentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateDocumentServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CandidateDocumentRepository candidateDocumentRepository;

    @InjectMocks
    private CandidateDocumentServiceImpl documentService;

    private Candidate candidate;

    @BeforeEach
    void setUp() {

        candidate = Candidate.builder()
                .id(1L)
                .username("Test User")
                .email("test@gmail.com")
                .phoneNumber("9876543210")
                .candidateType(CandidateType.EXPERIENCED)
                .role(Role.CANDIDATE)
                .applicationStatus(
                        ApplicationStatus.PENDING_VERIFICATION
                )
                .build();

        setAuthentication(1L, "test@gmail.com", Role.CANDIDATE);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private void setAuthentication(
            Long userId,
            String email,
            Role role) {

        UserPrincipal principal =
                UserPrincipal.builder()
                        .userId(userId)
                        .email(email)
                        .role(role)
                        .build();

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null
                        )
                );
    }

    private MockMultipartFile validPdf() {

        return new MockMultipartFile(
                "resume",
                "resume.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );
    }

    private CandidateDocument createDocument(
            Long id,
            DocumentType type,
            DocumentStatus status) {

        return CandidateDocument.builder()
                .id(id)
                .candidate(candidate)
                .documentType(type)
                .fileName("resume.pdf")
                .contentType("application/pdf")
                .documentData("dummy data".getBytes())
                .status(status)
                .build();
    }


    // =========================================================
    // getMyDocuments()
    // =========================================================

    @Test
    void getMyDocuments_shouldReturnDocumentsSuccessfully() {

        CandidateDocument document =
                createDocument(
                        10L,
                        DocumentType.RESUME,
                        DocumentStatus.PENDING
                );

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateDocumentRepository.findByCandidate(candidate))
                .thenReturn(List.of(document));

        List<CandidateDocumentDto> result =
                documentService.getMyDocuments();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                10L,
                result.get(0).getId()
        );

        assertEquals(
                DocumentType.RESUME,
                result.get(0).getDocumentType()
        );

        assertEquals(
                DocumentStatus.PENDING,
                result.get(0).getStatus()
        );
    }


    // =========================================================
    // getMyDocuments() - candidate not found
    // =========================================================

    @Test
    void getMyDocuments_shouldThrowException_whenCandidateNotFound() {

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> documentService.getMyDocuments()
        );
    }


    // =========================================================
    // getDocumentsByCandidateId()
    // =========================================================

    @Test
    void getDocumentsByCandidateId_shouldReturnDocumentsSuccessfully() {

        CandidateDocument document =
                createDocument(
                        20L,
                        DocumentType.PAN_CARD,
                        DocumentStatus.VERIFIED
                );

        when(candidateRepository.findById(2L))
                .thenReturn(Optional.of(candidate));

        when(candidateDocumentRepository.findByCandidate(candidate))
                .thenReturn(List.of(document));

        List<CandidateDocumentDto> result =
                documentService.getDocumentsByCandidateId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                20L,
                result.get(0).getId()
        );
    }


    @Test
    void getDocumentsByCandidateId_shouldThrowException_whenCandidateNotFound() {

        when(candidateRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentService.getDocumentsByCandidateId(999L)
        );
    }


    // =========================================================
    // getDocument()
    // =========================================================

    @Test
    void getDocument_shouldReturnDocumentSuccessfully() {

        CandidateDocument document =
                createDocument(
                        10L,
                        DocumentType.RESUME,
                        DocumentStatus.PENDING
                );

        when(candidateDocumentRepository.findById(10L))
                .thenReturn(Optional.of(document));

        CandidateDocument result =
                documentService.getDocument(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(
                DocumentType.RESUME,
                result.getDocumentType()
        );
    }


    @Test
    void getDocument_shouldThrowException_whenDocumentNotFound() {

        when(candidateDocumentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> documentService.getDocument(999L)
        );
    }


    // =========================================================
    // uploadDocuments()
    // =========================================================

    @Test
    void uploadDocuments_shouldUploadResumeSuccessfully() {

        CandidateDocumentRequest request =
                new CandidateDocumentRequest();

        request.setResume(validPdf());

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        when(candidateDocumentRepository
                .findByCandidateAndDocumentType(
                        candidate,
                        DocumentType.RESUME
                ))
                .thenReturn(Optional.empty());

        when(candidateDocumentRepository
                .findByCandidate(candidate))
                .thenReturn(List.of());

        documentService.uploadDocuments(request);

        verify(candidateDocumentRepository)
                .save(any(CandidateDocument.class));

        verify(candidateRepository)
                .save(candidate);
    }


    @Test
    void uploadDocuments_shouldThrowException_whenRequestIsNull() {

        assertThrows(
                BadRequestException.class,
                () -> documentService.uploadDocuments(null)
        );

        verifyNoInteractions(candidateRepository);
        verifyNoInteractions(candidateDocumentRepository);
    }


//    @Test
//    void uploadDocuments_shouldThrowException_whenFileIsEmpty() {
//
//        CandidateDocumentRequest request =
//                new CandidateDocumentRequest();
//
//        MockMultipartFile emptyFile =
//                new MockMultipartFile(
//                        "resume",
//                        "resume.pdf",
//                        "application/pdf",
//                        new byte[0]
//                );
//
//        request.setResume(emptyFile);
//
//        when(candidateRepository.findById(1L))
//                .thenReturn(Optional.of(candidate));
//
//        assertThrows(
//                BadRequestException.class,
//                () -> documentService.uploadDocuments(request)
//        );
//    }


    @Test
    void uploadDocuments_shouldThrowException_whenFileSizeExceeds5MB() {

        CandidateDocumentRequest request =
                new CandidateDocumentRequest();

        byte[] largeFile =
                new byte[5 * 1024 * 1024 + 1];

        MockMultipartFile file =
                new MockMultipartFile(
                        "resume",
                        "resume.pdf",
                        "application/pdf",
                        largeFile
                );

        request.setResume(file);

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadRequestException.class,
                () -> documentService.uploadDocuments(request)
        );
    }


    @Test
    void uploadDocuments_shouldThrowException_whenFileTypeInvalid() {

        CandidateDocumentRequest request =
                new CandidateDocumentRequest();

        MockMultipartFile file =
                new MockMultipartFile(
                        "resume",
                        "resume.exe",
                        "application/octet-stream",
                        "test".getBytes()
                );

        request.setResume(file);

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadRequestException.class,
                () -> documentService.uploadDocuments(request)
        );
    }


    // =========================================================
    // FRESHER DOCUMENT VALIDATION
    // =========================================================

    @Test
    void uploadDocuments_shouldRejectSalarySlipForFresher() {

        candidate.setCandidateType(
                CandidateType.FRESHER
        );

        CandidateDocumentRequest request =
                new CandidateDocumentRequest();

        request.setSalarySlip(validPdf());

        when(candidateRepository.findById(1L))
                .thenReturn(Optional.of(candidate));

        assertThrows(
                BadRequestException.class,
                () -> documentService.uploadDocuments(request)
        );
    }
}
