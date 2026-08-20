package com.verify_x.services;

import com.verify_x.dto.HRDocumentDto;
import com.verify_x.dto.PagedResponse;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;

public interface HRDocumentService {

    // Powers the "Documents" sidebar screen: every document, across every candidate
    PagedResponse<HRDocumentDto> getAllDocuments(
            String keyword,
            DocumentStatus status,
            DocumentType documentType,
            int page,
            int size
    );
}
