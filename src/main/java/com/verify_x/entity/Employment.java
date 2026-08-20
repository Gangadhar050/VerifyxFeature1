package com.verify_x.entity;

import com.verify_x.enums.EmploymentStatus;
import com.verify_x.enums.OfferLetterStatus;
import com.verify_x.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employment_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;

    private String previousCompanyName;

    private String previousDesignation;

    private Double totalExperience;

    private Double lastCTC;

    private LocalDate lastWorkingDay;

    @Column(length = 12)
    private String uanNumber;

    @Builder.Default
    private Boolean uanVerified = false;

    private String uanVerifiedBy;

    private LocalDateTime uanVerifiedAt;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    private String currentCompany;

    private String currentDesignation;

    private Double currentCTC;

    private Integer noticePeriod;

    @Enumerated(EnumType.STRING)
    private OfferLetterStatus offerLetterStatus;
//
//    private String offerCompanyName;
//
//    private Double offeredCTC;
//
//    private LocalDate joiningDate;
//
//    private String offerReferenceNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus uanVerificationStatus = VerificationStatus.PENDING;

}
