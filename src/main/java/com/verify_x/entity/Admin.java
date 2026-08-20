package com.verify_x.entity;

import com.verify_x.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 100)
    private String fullName;

    @Column(length = 20)
    private String mobileNumber;

    @Column(length = 255)
    private String location;


    @Column(length = 100)
    private String designation;

    @Column(length = 150)
    private String company;

    @Column(length = 100)
    private String employeeId;

    @Column(length = 100)
    private String department;

    @Column(length = 150)
    private String reportingManager;

    @Column(length = 50)
    private String workMode;

    private LocalDate joiningDate;


    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePhoto;

    //account status
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private LocalDateTime lastLogin;



    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}