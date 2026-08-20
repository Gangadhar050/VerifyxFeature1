package com.verify_x.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminProfileResponse {

    private Long id;

    private String email;

    private String fullName;

    private String mobileNumber;

    private String location;

    private String designation;

    private String company;

    private String employeeId;

    private String department;

    private String reportingManager;

    private String workMode;

    private LocalDate joiningDate;

    private Boolean active;

    private LocalDateTime lastLogin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}