package com.verify_x.services;

import com.verify_x.dto.*;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.enums.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CandidateDocumentService {

  // Candidate uploads a document.
  void uploadDocuments(CandidateDocumentRequest request);

    // Candidate re-uploads a rejected document.
    void reUploadDocuments(CandidateDocumentRequest request);

    // Get logged-in candidate documents.
    List<CandidateDocumentDto> getMyDocuments();

    // HR/Admin view candidate documents.
    List<CandidateDocumentDto> getDocumentsByCandidateId(Long candidateId);

    // Download candidate document.
//    CandidateDocumentDto getDocument(Long documentId);
  CandidateDocument getDocument(Long documentId);
    // Candidate deletes a document.
    void deleteDocument(Long documentId);

    // HR verifies document.
    void verifyDocument(Long documentId);

    /**
     * HR rejects document.
     */
    void rejectDocument(
            Long documentId,
            String rejectionReason
    );

  //verify uan number
  void verifyUan(
          Long candidateId);

  // HR Verification Requests
  List<HrVerificationRequestDto> getAllVerificationRequests();

  // Download candidate document.
  Resource downloadDocument(Long documentId);

  // ===============================
  // HR Dashboard Statistics
  // ===============================
  DashboardStatisticsDto getDashboardStatistics();

  // HR Dashboard
  List<CandidateDashboardDto> getCandidateDashboard();

  // Get all pending documents.
  List<CandidateDocumentDto> getPendingDocuments();



}