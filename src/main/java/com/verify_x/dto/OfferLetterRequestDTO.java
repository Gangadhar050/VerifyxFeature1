package com.verify_x.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferLetterRequestDTO {

    @NotBlank(message = "Company name is required")
    @Size(
            max = 200,
            message = "Company name cannot exceed 200 characters"
    )
    private String companyName;

    @Size(
            max = 150,
            message = "Designation cannot exceed 150 characters"
    )
    private String designation;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "CTC cannot be negative"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "Invalid CTC format"
    )
    private BigDecimal ctc;

    @NotNull(message = "Offer date is required")
    private LocalDate offerDate;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @Size(
            max = 100,
            message = "Reference number cannot exceed 100 characters"
    )
    private String referenceNumber;
}