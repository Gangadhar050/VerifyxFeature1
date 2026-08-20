package com.verify_x.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UanVerificationDto {

    private String uanNumber;

    private String status;

    private String verifiedMessage;
}