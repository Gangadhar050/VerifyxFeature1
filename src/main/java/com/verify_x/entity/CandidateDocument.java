package com.verify_x.entity;

import com.verify_x.enums.DocumentStatus;
import com.verify_x.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Candidate who uploaded this document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // Resume / PAN / Offer Letter...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    // Original uploaded file name
    @Column(nullable = false)
    private String fileName;

    private String contentType;

    @Lob
    @Column(name = "document_data", columnDefinition = "LONGBLOB")
    private byte[] documentData;

    // Verification Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PENDING;

    // HR comments if rejected
    @Column(length = 500)
    private String rejectionReason;

    // Upload timestamp
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // Last update timestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}