package com.verify_x.services;

import com.verify_x.dto.ReportDashboardResponse;
import com.verify_x.dto.ReportResponse;

import java.util.List;

public interface ReportService {
//
//    ReportDashboardResponse getDashboard();

    List<ReportResponse> getReports();

    byte[] exportCsv();
}