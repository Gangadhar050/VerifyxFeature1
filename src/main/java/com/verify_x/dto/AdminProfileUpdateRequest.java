package com.verify_x.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminProfileUpdateRequest {

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
}