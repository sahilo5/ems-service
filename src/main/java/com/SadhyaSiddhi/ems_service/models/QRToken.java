package com.SadhyaSiddhi.ems_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "qr_tokens")
public class QRToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Random generated token value
    @Column(nullable = false, unique = true)
    private String token;

    // Expiry time of this token
    @Column(nullable = false)
    private LocalDateTime expiryTime;

    // Created by which admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
}
