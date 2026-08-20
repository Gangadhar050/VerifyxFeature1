package com.verify_x.dto;

import com.verify_x.enums.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UanVerificationRequestDto {

    private VerificationStatus status;
}