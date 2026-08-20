package com.verify_x.services;

import com.verify_x.dto.EmploymentDetailsDto;

import java.util.List;

public interface EmploymentService {


    // Create Employment Details
    void saveEmploymentDetails(EmploymentDetailsDto dto);

  //  Update Employment Details
    void updateEmploymentDetails(EmploymentDetailsDto dto);

    // Get Employment By Candidate Id
    EmploymentDetailsDto getEmploymentDetailsByCandidateId(Long candidateId);

    // Get Employment By Email
    EmploymentDetailsDto getEmploymentDetailsByEmail(String email);

    // Search Employment Details
    List<EmploymentDetailsDto> searchEmploymentDetails(String keyword);

    // Delete Employment Details
    void deleteEmploymentDetails(Long candidateId);

}