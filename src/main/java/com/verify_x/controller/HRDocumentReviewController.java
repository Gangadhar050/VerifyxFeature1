package com.verify_x.controller;

import com.verify_x.dto.HRDocumentReviewDto;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.services.HRDocumentReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/document-review")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('HR','ADMIN')")
public class HRDocumentReviewController {

    private final HRDocumentReviewService hrDocumentReviewService;

    @GetMapping("/{candidateId}")
    public ResponseEntity<HRDocumentReviewDto> getCandidateDocuments(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(
                hrDocumentReviewService.getCandidateDocuments(candidateId)
        );
    }


    @GetMapping("/document/{documentId}")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long documentId) {

        CandidateDocument document =
                hrDocumentReviewService.getDocumentEntity(documentId);

        Resource resource =
                hrDocumentReviewService.viewDocument(documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .contentLength(document.getDocumentData().length)
                .body(resource);
    }


    @PutMapping("/verify/{documentId}")
    public ResponseEntity<String> verifyDocument(
            @PathVariable Long documentId) {

        hrDocumentReviewService.verifyDocument(documentId);

        return ResponseEntity.ok("Document verified successfully.");
    }

    @PutMapping("/reject/{documentId}")
    public ResponseEntity<String> rejectDocument(
            @PathVariable Long documentId,
            @RequestParam String reason) {

        hrDocumentReviewService.rejectDocument(documentId, reason);

        return ResponseEntity.ok("Document rejected successfully.");
    }

}