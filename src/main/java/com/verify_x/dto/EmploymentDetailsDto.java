package com.verify_x.dto;

import com.verify_x.enums.EmploymentStatus;
import com.verify_x.enums.OfferLetterStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentDetailsDto {

    /*
     * Previous Employment Details
     */

    private String previousCompanyName;

    private String previousDesignation;

    private Double totalExperience;

    private Double lastCTC;

    private LocalDate lastWorkingDay;

    private String uanNumber;

    /*
     * Current Employment Details
     */

    private EmploymentStatus employmentStatus;

    private String currentCompany;

    private String currentDesignation;

    private Double currentCTC;

    private Integer noticePeriod;

    /*
     * Offer Letter Details
     */

    private OfferLetterStatus offerLetterStatus;

//    private String offerCompanyName;
//
//    private Double offeredCTC;
//
//    private LocalDate joiningDate;
//
//    private String offerReferenceNumber;

}