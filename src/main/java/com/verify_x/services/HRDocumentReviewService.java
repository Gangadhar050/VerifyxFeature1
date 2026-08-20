package com.verify_x.services;

import com.verify_x.dto.HRDocumentReviewDto;
import com.verify_x.entity.CandidateDocument;
import org.springframework.core.io.Resource;

public interface HRDocumentReviewService {

    // Get all documents of one candidate
    HRDocumentReviewDto getCandidateDocuments(Long candidateId);

    // View one document
    Resource viewDocument(Long documentId);

    // Verify one document
    void verifyDocument(Long documentId);

    // Reject one document
    void rejectDocument(Long documentId, String reason);

    CandidateDocument getDocumentEntity(Long documentId);
}