package com.verify_x.dto;


import com.verify_x.enums.CandidateType;
import com.verify_x.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentUserDto {

    private Long userId;

    private String username;

    private String email;

    private Role role;

    private CandidateType candidateType;
}