package com.verify_x.entity;

import com.verify_x.enums.CandidateType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pending_registrations",
        indexes = {
                @Index(name = "idx_pending_email", columnList = "email"),
                @Index(name = "idx_pending_phone", columnList = "phoneNumber")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 100)
    private String username;

    @Column(nullable = false, unique = false)
    private String email;

    @Column(nullable = false, unique = false, length = 10)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String appliedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateType candidateType;

    @Column(nullable = false)
    private String emailOtpHash;
//
//    @Column(nullable = true)
//    private String mobileOtpHash;

    @Column(nullable = false)
    private LocalDateTime otpExpiresAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
