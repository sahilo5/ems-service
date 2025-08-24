package com.SadhyaSiddhi.ems_service.services;


import com.SadhyaSiddhi.ems_service.dto.AttendanceRequest;
import com.SadhyaSiddhi.ems_service.exceptions.AttendanceCompletedException;
import com.SadhyaSiddhi.ems_service.exceptions.InvalidQrTokenException;
import com.SadhyaSiddhi.ems_service.models.Attendance;
import com.SadhyaSiddhi.ems_service.models.AttendanceStatus;
import com.SadhyaSiddhi.ems_service.models.MarkedBy;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.payload.AttendanceDayResponse;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final UserRepository userRepository;
    private final String JWT_SECRET = "yoursecret"; // replace with your injected secret

    @Autowired
    private AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(UserRepository userRepository) {
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
                .claim("slot", now.getEpochSecond() /30)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(30)))
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

    public ApiResponse<Object> markAttendanceManual(String username, String inOrOut) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElse(null);

        if (inOrOut.equalsIgnoreCase("checkin")) {
            if (attendance != null && attendance.getCheckInTime() != null) {
                throw new AttendanceCompletedException("User already checked in today.");
            }

            if (attendance == null) {
                attendance = new Attendance();
                attendance.setUser(user);
                attendance.setDate(today);
                attendance.setStatus(AttendanceStatus.PRESENT);
                attendance.setMarkedBy(MarkedBy.ADMIN);
            }
            attendance.setCheckInTime(LocalDateTime.now());
            attendanceRepository.save(attendance);

            return new ApiResponse<>(true,
                    "Admin marked check-in for " + user.getUsername(),
                    null,
                    HttpStatus.OK.value(),
                    LocalDateTime.now());

        } else if (inOrOut.equalsIgnoreCase("checkout")) {
            if (attendance == null || attendance.getCheckInTime() == null) {
                throw new AttendanceCompletedException("User must check in before checking out.");
            }
            if (attendance.getCheckOutTime() != null) {
                throw new AttendanceCompletedException("User already checked out today.");
            }

            attendance.setCheckOutTime(LocalDateTime.now());
            attendance.setMarkedBy(MarkedBy.ADMIN);
            attendanceRepository.save(attendance);

            return new ApiResponse<>(true,
                    "Admin marked check-out for " + user.getUsername(),
                    null,
                    HttpStatus.OK.value(),
                    LocalDateTime.now());
        }

        throw new IllegalArgumentException("Invalid request: must be 'checkin' or 'checkout'.");
    }

    public ApiResponse<Object> updateAttendance(String username, LocalDate date, LocalDateTime checkIn, LocalDateTime checkOut) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Attendance attendance = attendanceRepository.findByUserAndDate(user, date)
                .orElse(new Attendance());

        attendance.setUser(user);
        attendance.setDate(date);
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setMarkedBy(MarkedBy.ADMIN);

        if (checkIn != null) attendance.setCheckInTime(checkIn);
        if (checkOut != null) attendance.setCheckOutTime(checkOut);

        attendanceRepository.save(attendance);

        return new ApiResponse<>(
                true,
                "Attendance updated successfully for " + username + " on " + date,
                null,
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );
    }


    public List<ApiResponse<Object>> markBulkAttendance(List<Map<String, String>> bulkRequests) {
        List<ApiResponse<Object>> lst = new ArrayList<>();

        for (Map<String, String> req : bulkRequests) {
            String username = req.get("username");
            String inOrOut = req.get("inOrOut");

            try {
                ApiResponse<Object> response = markAttendanceManual(username, inOrOut);

                lst.add(new ApiResponse<>(
                        true,
                        response.getMessage(),
                        username,
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                ));
            } catch (Exception e) {
                String errorMessage;

                if ("User already checked out today.".equals(e.getMessage())) {
                    errorMessage = "User already checked out today.";
                } else if ("User must check in before checking out.".equals(e.getMessage())) {
                    errorMessage = "User must check in before checking out.";
                } else {
                    errorMessage = e.getMessage();
                }

                lst.add(new ApiResponse<>(
                        false,
                        errorMessage,
                        username,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()
                ));
            }
        }

        return lst;
    }

    public List<AttendanceDayResponse> getAttendanceBetweenDates(AttendanceRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Attendance> records = attendanceRepository.findByUserAndDateBetween(
                user, request.getStartDate(), request.getEndDate());

        return records.stream().map(record -> {
            long hours = 0;
            if (record.getCheckInTime() != null && record.getCheckOutTime() != null) {
                hours = Duration.between(record.getCheckInTime(), record.getCheckOutTime()).toHours();
            }
            return new AttendanceDayResponse(
                    record.getDate(),
                    record.getCheckInTime() != null ? record.getCheckInTime().toLocalTime() : null,
                    record.getCheckOutTime() != null ? record.getCheckOutTime().toLocalTime() : null,
                    hours,
                    record.getStatus().name()
            );
        }).collect(Collectors.toList());
    }


}
