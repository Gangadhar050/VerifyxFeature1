package com.verify_x.entity;

import com.verify_x.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "education_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false, unique = true)
    private Candidate candidate;

//10th
    private String tenthSchoolName;
    private String tenthBoard;
    private String tenthSchoolLocation;
    private String tenthRollNumber;
    private Integer tenthPassingYear;
    private Double tenthPercentage;

//    private MultipartFile tenthMarksCard;
    private String tenthMarksCardName;

    private String tenthMarksCardContentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] tenthMarksCard;

//12th
     private String twelfthInstitutionName;
    private String twelfthBoardUniversity;
    private String twelfthLocation;
    private String twelfthRegistrationNumber;
    private Integer twelfthPassingYear;
    private Double twelfthPercentage;

    private String twelfthMarksCardName;
    private String twelfthMarksCardContentType;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] twelfthMarksCard;

    //degree
    private String degreeName;
    private String specialization;
    private String collegeName;
    private String universityName;
    private String usnNumber;
    private String degreeLocation;
    private Integer degreeStartYear;
    private Integer degreeEndYear;
    private Double degreePercentage;
    private String degreeCertificateName;
    private String degreeCertificateContentType;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] degreeCertificate;


 //Masters
    @Column(nullable = true)
    private String mastersDegree;

    @Column(nullable = true)
    private String mastersSpecialization;

    @Column(nullable = true)
    private String mastersCollege;

    @Column(nullable = true)
    private String mastersUniversity;

    @Column(nullable = true)
    private String mastersRegistrationNumber;

    @Column(nullable = true)
    private String mastersLocation;

    @Column(nullable = true)
    private Integer mastersStartYear;

    @Column(nullable = true)
    private Integer mastersEndYear;

    @Column(nullable = true)
    private Double mastersPercentage;

    @Column(nullable = true)
    private String mastersMarksCardName;

    @Column(nullable = true)
    private String mastersMarksCardContentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB",nullable = true)
    private byte[] mastersMarksCard;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}