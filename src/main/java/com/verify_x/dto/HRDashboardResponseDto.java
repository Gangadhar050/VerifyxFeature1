package com.verify_x.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HRDashboardResponseDto {

    private long total;
    private long freshers;
    private long experienced;
    private long pending;
    private long approved;
    private long rejected;

}