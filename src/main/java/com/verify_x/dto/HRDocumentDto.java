package com.verify_x.dto;

import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HRDocumentDto {

    private Long documentId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private DocumentType documentType;
    private String fileName;
    private DocumentStatus status;
    private String rejectionReason;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}
