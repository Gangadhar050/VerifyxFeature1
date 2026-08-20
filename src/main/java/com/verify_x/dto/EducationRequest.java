package com.verify_x.dto;

import com.verify_x.enums.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

    // ---------- 10th ----------

    private String tenthSchoolName;
    private String tenthBoard;
    private String tenthSchoolLocation;
    private String tenthRollNumber;
    private Integer tenthPassingYear;
    private Double tenthPercentage;

    private MultipartFile tenthMarksCard;

    // ---------- 12th ----------

    private String twelfthInstitutionName;
    private String twelfthBoardUniversity;
    private String twelfthLocation;
    private String twelfthRegistrationNumber;
    private Integer twelfthPassingYear;
    private Double twelfthPercentage;

    private MultipartFile twelfthMarksCard;

    // ---------- Degree ----------

    private String degreeName;
    private String specialization;
    private String collegeName;
    private String universityName;
    private String usnNumber;
    private String degreeLocation;
    private Integer degreeStartYear;
    private Integer degreeEndYear;
    private Double degreePercentage;

    private MultipartFile degreeCertificate;

    // ---------- Master's ----------

    private String mastersDegree;
    private String mastersSpecialization;
    private String mastersCollege;
    private String mastersUniversity;
    private String mastersRegistrationNumber;
    private String mastersLocation;
    private Integer mastersStartYear;
    private Integer mastersEndYear;
    private Double mastersPercentage;

//    private MultipartFile mastersMarksCard;

    private MultipartFile mastersMarksCard;




    //Skills
//    private Set<TechnicalSkill> technicalSkills;
//    private List<TechnicalSkill> technicalSkills;
}