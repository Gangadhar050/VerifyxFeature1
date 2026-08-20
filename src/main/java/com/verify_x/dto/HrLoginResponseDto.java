package com.verify_x.dto;

import com.verify_x.enums.CandidateType;
import com.verify_x.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrLoginResponseDto {

    private String accessToken;

    private String tokenType;


//    private String username;

    private String email;

    private Role role;

}
