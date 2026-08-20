package com.verify_x.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDashboardResponse {

    private long total;
    private long freshers;
    private long experienced;
    private long pending;
    private long approved;
    private long rejected;
}