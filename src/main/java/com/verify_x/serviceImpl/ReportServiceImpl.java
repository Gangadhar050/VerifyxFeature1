package com.verify_x.serviceImpl;

import com.verify_x.dto.ReportDashboardResponse;
import com.verify_x.dto.ReportResponse;
import com.verify_x.entity.Candidate;
import com.verify_x.entity.CandidateDocument;
import com.verify_x.enums.CandidateType;
import com.verify_x.enums.DocumentStatus;
import com.verify_x.repository.CandidateDocumentRepository;
import com.verify_x.repository.CandidateRepository;
import com.verify_x.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CandidateRepository candidateRepository;
    private final CandidateDocumentRepository candidateDocumentRepository;


    @Override
    public List<ReportResponse> getReports() {

        List<Candidate> candidates = candidateRepository.findAll();
        List<ReportResponse> reports = new ArrayList<>();

        for (Candidate candidate : candidates) {

            List<CandidateDocument> documents =
                    candidateDocumentRepository.findByCandidateId(candidate.getId());

            String status = "PENDING";

            if (!documents.isEmpty()) {
                status = documents.get(0).getStatus().name();
            }

            ReportResponse report = ReportResponse.builder()
                    .candidateId(candidate.getId())
                    .fullName(candidate.getUsername())
                    .candidateType(candidate.getCandidateType().name())
                    .status(status)
                    .email(candidate.getEmail())
                    .phoneNumber(candidate.getPhoneNumber())
                   // .appliedRole(candidate.getAppliedRole())
                    .build();

            reports.add(report);
        }

        return reports;
    }

    @Override
    public byte[] exportCsv() {

        List<ReportResponse> reports = getReports();

        StringBuilder csv = new StringBuilder();

        csv.append("Candidate ID,Full Name,Candidate Type,Status,Applied Role,Email,Phone Number\n");

        for (ReportResponse report : reports) {
            csv.append(report.getCandidateId()).append(",");
            csv.append(report.getFullName()).append(",");
            csv.append(report.getCandidateType()).append(",");
            csv.append(report.getStatus()).append(",");
            csv.append(report.getAppliedRole()).append(",");
            csv.append(report.getEmail()).append(",");
            csv.append(report.getPhoneNumber()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

}