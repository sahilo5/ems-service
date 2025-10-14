package com.SadhyaSiddhi.ems_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "advance_log")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdvanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "advance_id")
    private Advances advance;

    @Column(nullable = false)
    private Double paidAmount;

    private LocalDateTime paidDate;

    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvanceStatus status = AdvanceStatus.PENDING;
}

