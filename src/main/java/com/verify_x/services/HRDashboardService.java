package com.verify_x.services;

import com.verify_x.dto.HRCandidateDashboardDto;
import com.verify_x.dto.HRDashboardResponseDto;
import com.verify_x.entity.Candidate;

import java.util.List;

public interface HRDashboardService {

    //Returns the dashboard summary.
    HRDashboardResponseDto getDashboardReport();

    //Returns all candidates for the Recent Applications table.
    List<HRCandidateDashboardDto> getCandidates();

}