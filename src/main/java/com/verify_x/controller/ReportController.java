package com.verify_x.controller;

import com.verify_x.dto.ReportDashboardResponse;
import com.verify_x.dto.ReportResponse;
import com.verify_x.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

//    @GetMapping("/dashboard")
//    public ResponseEntity<ReportDashboardResponse> getDashboard() {
//        return ResponseEntity.ok(reportService.getDashboard());
//    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportResponse>> getReports() {
        return ResponseEntity.ok(reportService.getReports());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {

        byte[] csv = reportService.exportCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}