package com.verify_x.jwt;

import com.verify_x.enums.Role;
import lombok.*;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPrincipal {
    private Long userId;
    private String email;
    private String username;
    private Role role;

}
