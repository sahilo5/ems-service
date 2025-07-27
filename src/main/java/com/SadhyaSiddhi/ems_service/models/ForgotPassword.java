package com.SadhyaSiddhi.ems_service.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpId;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date ExpirationTime;

    @ManyToOne
    private UserEntity user;

}

