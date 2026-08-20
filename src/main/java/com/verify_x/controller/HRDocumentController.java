package com.verify_x.controller;

import com.verify_x.dto.HRDocumentDto;
import com.verify_x.dto.PagedResponse;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import com.verify_x.services.HRDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Powers the "Documents" sidebar screen — every document across every
 * candidate, filterable by status/type/keyword. Verify/Reject/View for an
 * individual document still go through the existing endpoints in
 * CandidateDocumentController (/upload/verify/{id}, /upload/reject/{id},
 * /upload/view/{id}) — this controller is read-only, just for listing.
 */
@RestController
@RequestMapping("/api/hr/documents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class HRDocumentController {

    private final HRDocumentService hrDocumentService;

    @GetMapping
    public ResponseEntity<PagedResponse<HRDocumentDto>> getAllDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) DocumentType documentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                hrDocumentService.getAllDocuments(keyword, status, documentType, page, size));
    }
}
