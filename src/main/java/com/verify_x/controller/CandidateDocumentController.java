package com.verify_x.controller;

import com.verify_x.dto.CandidateDocumentDto;
import com.verify_x.dto.CandidateDocumentRequest;
import com.verify_x.dto.CandidateProfileDto;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.enums.DocumentType;
import com.verify_x.payload.ApiResponse;
import com.verify_x.services.CandidateDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class CandidateDocumentController {

    private final CandidateDocumentService candidateDocumentService;

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<String>> uploadDocuments(

            @ModelAttribute CandidateDocumentRequest request) {

        candidateDocumentService.uploadDocuments(request);

        return ResponseEntity.ok(

                ApiResponse.success(
                        "Documents uploaded successfully.",
                        null));
    }

    @PutMapping(value="/re-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<String>> reUploadDocuments(

            @ModelAttribute CandidateDocumentRequest request)
    {
        candidateDocumentService.reUploadDocuments(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Documents re-uploaded successfully.",
                        null));
    }


    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/my-documents")
    public ResponseEntity<List<CandidateDocumentDto>> getMyDocuments() {

        return ResponseEntity.ok(
                candidateDocumentService.getMyDocuments());

    }

    @PreAuthorize("hasAnyRole('CANDIDATE','HR','ADMIN')")
    @GetMapping("/view/{documentId}")
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long documentId) {
        CandidateDocument document = candidateDocumentService.getDocument(documentId);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(document.getContentType());
        } catch (Exception ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName().replace("\"", "") + "\"")
                .contentType(mediaType)
                .body(document.getDocumentData());
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<String>> deleteDocument(

            @PathVariable Long documentId

    ) {

        candidateDocumentService.deleteDocument(documentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Document deleted successfully.",
                        (CandidateProfileDto) null
                )
        );
    }

}