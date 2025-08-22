package com.SadhyaSiddhi.ems_service.services;


import com.SadhyaSiddhi.ems_service.exceptions.AttendanceCompletedException;
import com.SadhyaSiddhi.ems_service.exceptions.InvalidQrTokenException;
import com.SadhyaSiddhi.ems_service.models.Attendance;
import com.SadhyaSiddhi.ems_service.models.AttendanceStatus;
import com.SadhyaSiddhi.ems_service.models.MarkedBy;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repositories.AttendanceRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class AttendanceService {

    private final UserRepository userRepository;
    private final String JWT_SECRET = "yoursecret"; // replace with your injected secret

    @Autowired
    private AttendanceRepository attendanceRepository;

    public AttendanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Generate QR Token for admin dashboard (rotates every 15s)
     */
    public String generateQrToken() {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject("attendance-qr")
                .setId(java.util.UUID.randomUUID().toString())
                .claim("slot", now.getEpochSecond() / 15)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(15)))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
    }

    /**
     * Validate QR token & mark attendance for current user
     */
    public ApiResponse<Object> markAttendance(String qrToken, String inOrOut) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(qrToken)
                    .getBody();
        } catch (Exception e) {
            throw new InvalidQrTokenException("QR token is invalid or expired.");
        }

        if (!"attendance-qr".equals(claims.getSubject())) {
            throw new InvalidQrTokenException("Invalid QR token type.");
        }

        // Identify current logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (inOrOut.equalsIgnoreCase("checkin")) {
            if (attendance != null && attendance.getCheckInTime() != null) {
                throw new AttendanceCompletedException("You have already checked in today.");
            }

            if (attendance == null) {
                attendance = new Attendance();
                attendance.setUser(user);
                attendance.setDate(today);
                attendance.setStatus(AttendanceStatus.PRESENT);
                attendance.setMarkedBy(MarkedBy.SELF_QR);
            }
            attendance.setCheckInTime(LocalDateTime.now());
            attendanceRepository.save(attendance);

            return new ApiResponse<>(
                    true,
                    "Check-in marked successfully at " + attendance.getCheckInTime(),
                    null,
                    HttpStatus.OK.value(),
                    LocalDateTime.now()
            );

        } else if (inOrOut.equalsIgnoreCase("checkout")) {
            if (attendance == null || attendance.getCheckInTime() == null) {
                throw new AttendanceCompletedException("You must check in before checking out.");
            }
            if (attendance.getCheckOutTime() != null) {
                throw new AttendanceCompletedException("You have already checked out today.");
            }

            attendance.setCheckOutTime(LocalDateTime.now());
            attendanceRepository.save(attendance);

            return new ApiResponse<>(
                    true,
                    "Check-out marked successfully at " + attendance.getCheckOutTime(),
                    null,
                    HttpStatus.OK.value(),
                    LocalDateTime.now()
            );
        } else {
            throw new IllegalArgumentException("Invalid request: must be 'checkin' or 'checkout'.");
        }
    }

}
