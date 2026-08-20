package com.verify_x.entity;

import com.verify_x.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, length = 10)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppliedRole appliedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateType candidateType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 500)
    private String address;

    @Column(unique = true, length = 10)
    private String panNumber;

    @Column(unique = true, length = 12)
    private String aadhaarNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

//Technical skills
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "candidate_technical_skills",
            joinColumns = @JoinColumn(name = "candidate_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_candidate_technical_skill",
                            columnNames = {
                                    "candidate_id",
                                    "technical_skill"
                            }
                    )
            }
    )
    @Column(name = "technical_skill", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<TechnicalSkill> technicalSkills = new HashSet<>();

    //Tools and Platform
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "candidate_tool_platforms",
            joinColumns = @JoinColumn(name = "candidate_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_candidate_tool_platform",
                            columnNames = {
                                    "candidate_id",
                                    "tool_platform"
                            }
                    )
            }
    )
    @Column(name = "tool_platform", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<ToolPlatform> toolPlatforms = new HashSet<>();

    //application status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus applicationStatus =
            ApplicationStatus.PENDING_VERIFICATION;

    @Column(length = 1000)
    private String remarks;

   //employment
    @OneToOne(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Employment employment;

  //documents
    @OneToMany(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<CandidateDocument> documents = new ArrayList<>();

    //offers letters
    @OneToMany(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OfferLetter> offerLetters = new ArrayList<>();

    // education
    @OneToOne(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private Education education;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean mobileVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    private String emailOtpHash;

    private String mobileOtpHash;

    private LocalDateTime otpExpiresAt;

}