package com.SadhyaSiddhi.ems_service.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "leaves")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType type;   // e.g. SICK, CASUAL, EARNED

    @Column(nullable = false)
    private String reason;    // why leave is applied

    @ElementCollection
    @CollectionTable(name = "leave_dates", joinColumns = @JoinColumn(name = "leave_id"))
    @Column(name = "leave_date")
    private List<LocalDate> dates;  // multiple days allowed

    private int noOfDays;

    private String description; // extra notes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    private String rejectionMessage; // filled if rejected

    private LocalDate createdAt = LocalDate.now();

    // Link to the employee applying
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
