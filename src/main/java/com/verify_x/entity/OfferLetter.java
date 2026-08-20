package com.verify_x.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "candidate_offer_letters",
        indexes = {
                @Index(
                        name = "idx_offer_candidate_id",
                        columnList = "candidate_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "candidate_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_offer_letter_candidate")
    )
    private Candidate candidate;

    @Column(nullable = false, length = 200)
    private String companyName;

    @Column(length = 150)
    private String designation;

    //ctc in LPA with precision and scale 7.50 ==7.5 LPA
    @Column(precision = 12, scale = 2)
    private BigDecimal ctc;

    @Column(nullable = false)
    private LocalDate offerDate;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Column(length = 100)
    private String referenceNumber;

    //document upload fields
    @Column(length = 255)
    private String documentFileName;

    @Column(length = 100)
    private String documentContentType;

    @Column
    private Long documentSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(
            name = "document_data",
            columnDefinition = "LONGBLOB"
    )
    private byte[] documentData;

 //audting fields
    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}