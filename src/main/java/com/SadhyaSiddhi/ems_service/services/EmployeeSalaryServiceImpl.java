package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.AdvanceDto;
import com.SadhyaSiddhi.ems_service.dto.AdvanceLogDto;
import com.SadhyaSiddhi.ems_service.dto.AttendanceRequest;
import com.SadhyaSiddhi.ems_service.dto.OtherPaymentDto;
import com.SadhyaSiddhi.ems_service.dto.SalaryConfigDto;
import com.SadhyaSiddhi.ems_service.dto.SalaryLogDto;
import com.SadhyaSiddhi.ems_service.dto.SalarySummaryDto;
import com.SadhyaSiddhi.ems_service.exceptions.ResourceNotFoundException;
import com.SadhyaSiddhi.ems_service.exceptions.SettingNotConfiguredProperlyException;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.*;
import com.SadhyaSiddhi.ems_service.payload.AttendanceDayResponse;
import com.SadhyaSiddhi.ems_service.repositories.AdvanceLogRepository;
import com.SadhyaSiddhi.ems_service.repositories.AdvancesRepository;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryConfigRepository;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryLogRepository;
import com.SadhyaSiddhi.ems_service.repositories.OtherPaymentsRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.SchemaOutputResolver;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {

    private final EmployeeSalaryConfigRepository configRepository;
    private final EmployeeSalaryLogRepository logRepository;
    private final UserRepository userRepository;

    private final AdvancesRepository advancesRepository;
    private final AdvanceLogRepository advanceLogRepository;

    private final OtherPaymentsRepository otherPaymentsRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AppSettingService appSettingService;

    @Override
    public List<SalaryConfigDto> getAllConfigs() {
        List<EmployeeSalaryConfig> configs = configRepository.findAllActiveConfigs();

        return configs.stream().map(config -> {
            SalaryConfigDto dto = new SalaryConfigDto();

            dto.setId(config.getId());
            dto.setBaseSalary(config.getBaseAmount());

            if (config.getUser() != null) {
                dto.setEmployeeName(config.getUser().getFirstName() + " " + config.getUser().getLastName());
                dto.setUsername(config.getUser().getUsername());
            } else {
                dto.setEmployeeName("Unknown");
            }

            if (config.getSalaryTier() != null) {
                dto.setCategory(config.getSalaryTier().name());
            } else {
                dto.setCategory("UNASSIGNED");
            }

            return dto;
        }).toList();
    }

    @Override
    public String updateConfig(Long id, String tier, Double baseAmount, Boolean active) {
        EmployeeSalaryConfig config = configRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Config not found with id " + id));

        if (tier != null) {
            try {
                config.setSalaryTier(SalaryTier.valueOf(tier.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid tier. Allowed: JUNIOR, MID, SENIOR");
            }
        }
        if (baseAmount != null) config.setBaseAmount(baseAmount);
        if (active != null) config.setActive(active);

        configRepository.save(config);

        return "Config updated successfully";
    }

    private SalaryLogDto mapToDto(EmployeeSalaryLog log) {
        SalaryLogDto dto = new SalaryLogDto();
        dto.setId(log.getId());
        dto.setSalaryMonth(log.getSalaryMonth());
        dto.setPayDay(log.getPayDay());
        dto.setAmountPaid(log.getAmountPaid());
        dto.setStatus(log.getStatus().name());
        dto.setRemarks(log.getRemarks());

        if (log.getConfig() != null && log.getConfig().getUser() != null) {
            UserEntity user = log.getConfig().getUser();
            if(user.isActive()) {
                dto.setUsername(user.getUsername());
                dto.setEmployeeName(user.getFirstName() + " " + user.getLastName());
            } else {
                dto.setUsername(user.getUsername());
                dto.setEmployeeName("Inactive User");
            }
        }

        return dto;
    }

    private AdvanceDto mapToDto(Advances advance) {
        AdvanceDto dto = new AdvanceDto();
        dto.setId(advance.getId());
        dto.setAdvanceDate(advance.getAdvanceDate());
        dto.setTitle(advance.getTitle());
        dto.setRemark(advance.getRemark());
        dto.setAmount(advance.getAmount());
        dto.setStatus(advance.getStatus() != null ? advance.getStatus().name() : null);
        if (advance.getConfig() != null && advance.getConfig().getUser() != null) {
            dto.setEmployeeName(advance.getConfig().getUser().getFirstName() + " " + advance.getConfig().getUser().getLastName());
            dto.setUsername(advance.getConfig().getUser().getUsername());
        }
        return dto;
    }

    private AdvanceLogDto mapToAdvanceLogDto(AdvanceLog log) {
        AdvanceLogDto dto = new AdvanceLogDto();
        dto.setId(log.getId());
        dto.setAdvanceId(log.getAdvance() != null ? log.getAdvance().getId() : null);
        dto.setPaidAmount(log.getPaidAmount());
        dto.setPaidDate(log.getPaidDate());
        dto.setRemarks(log.getRemarks());
        dto.setStatus(log.getStatus() != null ? log.getStatus().name() : null);
        return dto;
    }

    private OtherPaymentDto mapToOtherPaymentDto(OtherPayments payment) {
        OtherPaymentDto dto = new OtherPaymentDto();
        dto.setId(payment.getId());
        dto.setType(payment.getType() != null ? payment.getType().name() : null);
        dto.setRemark(payment.getRemark());
        dto.setAmount(payment.getAmount());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setDate(payment.getDate());
        dto.setStatus(payment.getStatus());
        if (payment.getConfig() != null && payment.getConfig().getUser() != null) {
            dto.setEmployeeName(payment.getConfig().getUser().getFirstName() + " " + payment.getConfig().getUser().getLastName());
            dto.setUsername(payment.getConfig().getUser().getUsername());
        }
        return dto;
    }

    @Override
    public List<SalaryLogDto> getAllLogs() {
        return logRepository.findAllActiveLogs()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<SalaryLogDto> getLogsByUsernameAndMonth(String username, String salaryMonth) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        EmployeeSalaryConfig config = configRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user: " + username));

        List<EmployeeSalaryLog> logs = logRepository.findLogsByConfigAndMonthAndDone(config.getId(), salaryMonth);

        return logs.stream()
                .map(this::mapToDto)
                .toList();
    }


    @Override
    public String addSalaryLog(String username, String salaryAmount, Double amountPaid, String statusStr, String remarks) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!user.isActive()) {
            throw new UserNotFoundException("Employee is inactive: " + username);
        }


        EmployeeSalaryConfig config = configRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user " + username));

        SalaryStatus status = SalaryStatus.PENDING;
        if (statusStr != null) {
            try {
                status = SalaryStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status. Allowed: PENDING, PAID, FAILED");
            }
        }
        EmployeeSalaryLog log = new EmployeeSalaryLog();
        log.setConfig(config);
        log.setSalaryMonth(salaryAmount);
        log.setAmountPaid(amountPaid);
        log.setStatus(status);
        if (status == SalaryStatus.DONE) {
            log.setPayDay(LocalDateTime.now());
        }
        log.setRemarks(remarks);

        logRepository.save(log);

        return "Salary log added successfully";
    }

    @Override
    public SalarySummaryDto getSalarySummary(String username, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!user.isActive()) {
            throw new UserNotFoundException("Employee is inactive: " + username);
        }


        EmployeeSalaryConfig config = configRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user " + username));

        double perDayAmount = config.getBaseAmount();
        double bonus = 0;

        // Attendance records
        AttendanceRequest req = new AttendanceRequest(username, startDate, endDate);
        List<AttendanceDayResponse> records = attendanceService.getAttendanceBetweenDates(req);

        // Holidays from AppSetting
        AppSetting setting = appSettingService.getSetting(1L);
        String holidayListJson = setting.getData();
        List<LocalDate> holidaysInMonth = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(holidayListJson);

            for (JsonNode node : root) {
                String dateStr = node.get("date").asText();
                LocalDate holidayDate = LocalDate.parse(dateStr);
                if (holidayDate.getYear() == ym.getYear() && holidayDate.getMonthValue() == ym.getMonthValue()) {
                    holidaysInMonth.add(holidayDate);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse holiday list", e);
        }

        int holidays = holidaysInMonth.size();

        // Sundays
        int sundays = 0;
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            if (d.getDayOfWeek() == DayOfWeek.SUNDAY) sundays++;
        }

        int daysInMonth = ym.lengthOfMonth();
        int workingDays = daysInMonth - sundays - holidays;

        // Attendance categorization
        int presents = 0, lates = 0, halfDays = 0, leaves = 0;
        for (AttendanceDayResponse r : records) {
            switch (r.getStatus()) {
                case "PRESENT" -> presents++;
                case "LATE" -> lates++;
                case "HALF_DAY" -> halfDays++;
                case "LEAVE" -> leaves++;
            }
        }

        int presentDays = presents + lates + halfDays;
        double totalMonthSalary = presentDays * perDayAmount;
        double totalSalary = workingDays * perDayAmount;

        // Deductions
        double leaveDeduction = leaves * perDayAmount;
        double halfDayDeduction = halfDays * (perDayAmount / 2);
        double lateDeduction = lates * (perDayAmount * 0.05);
        double deductionsTotal = leaveDeduction + halfDayDeduction + lateDeduction;

        double calculatedSalary = totalMonthSalary - deductionsTotal;
        double net = calculatedSalary + bonus;

        return SalarySummaryDto.builder()
                .employeeName(user.getFirstName() + " " + user.getLastName())
                .username(user.getUsername())
                .perDayAmount(perDayAmount)
                .totalSalary(totalSalary)
                .presentDays(presentDays)
                .workingDays(workingDays)
                .leaveDeduction(leaveDeduction)
                .halfDayDeduction(halfDayDeduction)
                .lateDeduction(lateDeduction)
                .deductionsTotal(deductionsTotal)
                .calculatedSalary(calculatedSalary)
                .bonus(bonus)
                .net(net)
                .month(month)
                .daysInMonth(daysInMonth)
                .totalMonthSalary(totalMonthSalary)
                .build();
    }

    @Override
    public Map<String, Double> getYearlyNetSalary(String username, int year) {
        Map<String, Double> yearlySalaries = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            String monthStr = ym.format(formatter); // "2025-01"

            try {
                SalarySummaryDto summary = getSalarySummary(username, monthStr);
                String monthName = ym.getMonth().name().substring(0, 1).toUpperCase()
                        + ym.getMonth().name().substring(1).toLowerCase();
                yearlySalaries.put(monthName, summary.getNet());
            } catch (Exception e) {
                // If no data (like future months or no config), set net = 0
                String monthName = ym.getMonth().name().substring(0, 1).toUpperCase()
                        + ym.getMonth().name().substring(1).toLowerCase();
                yearlySalaries.put(monthName, 0.0);
            }
        }
        return yearlySalaries;
    }

    @Override
    public AdvanceDto createAdvance(AdvanceDto advanceDto) {
        if (advanceDto.getUsername() == null || advanceDto.getUsername().isEmpty()) {
            throw new ResourceNotFoundException("Username is required to create advance");
        }
        UserEntity user = userRepository.findByUsername(advanceDto.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + advanceDto.getUsername()));
        EmployeeSalaryConfig config = configRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user " + advanceDto.getUsername()));
        Advances advance = new Advances();
        advance.setConfig(config);
        advance.setAdvanceDate(advanceDto.getAdvanceDate());
        advance.setTitle(advanceDto.getTitle());
        advance.setRemark(advanceDto.getRemark());
        advance.setAmount(advanceDto.getAmount());
        advance.setStatus(advanceDto.getStatus() != null ? AdvanceStatus.valueOf(advanceDto.getStatus()) : AdvanceStatus.PENDING);
        advance.setCreatedAt(LocalDateTime.now());
        Advances saved = advancesRepository.save(advance);
        return mapToDto(saved);
    }

    @Override
    public AdvanceDto updateAdvance(Long id, AdvanceDto advanceDto) {
        Advances existing = advancesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Advance not found with id " + id));
        if (advanceDto.getUsername() != null && !advanceDto.getUsername().isEmpty()) {
            UserEntity user = userRepository.findByUsername(advanceDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + advanceDto.getUsername()));
            EmployeeSalaryConfig config = configRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user " + advanceDto.getUsername()));
            existing.setConfig(config);
        }
        existing.setAdvanceDate(advanceDto.getAdvanceDate());
        existing.setTitle(advanceDto.getTitle());
        existing.setRemark(advanceDto.getRemark());
        existing.setAmount(advanceDto.getAmount());
        existing.setStatus(advanceDto.getStatus() != null ? AdvanceStatus.valueOf(advanceDto.getStatus()) : existing.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        Advances saved = advancesRepository.save(existing);
        return mapToDto(saved);
    }

    @Override
    public void deleteAdvance(Long id) {
        advancesRepository.deleteById(id);
    }

    @Override
    public List<AdvanceDto> getAllAdvances() {
        return advancesRepository.findAll().stream().map(advance -> {
            AdvanceDto dto = new AdvanceDto();
            dto.setId(advance.getId());
            if (advance.getConfig() != null && advance.getConfig().getUser() != null) {
                UserEntity user = advance.getConfig().getUser();
                dto.setEmployeeName(user.getFirstName() + " " + user.getLastName());
                dto.setUsername(user.getUsername());
            }
            dto.setAdvanceDate(advance.getAdvanceDate());
            dto.setTitle(advance.getTitle());
            dto.setRemark(advance.getRemark());
            dto.setAmount(advance.getAmount());
            dto.setStatus(advance.getStatus() != null ? advance.getStatus().name() : null);
            return dto;
        }).toList();
    }

    @Override
    public AdvanceDto getAdvanceById(Long id) {
        Advances advance = advancesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Advance not found with id " + id));
        return mapToDto(advance);
    }

    @Override
    public AdvanceLogDto logAdvancePayment(Long advanceId, AdvanceLogDto logDto) {
        Advances advance = advancesRepository.findById(advanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Advance not found with id " + advanceId));
        AdvanceLog log = new AdvanceLog();
        log.setAdvance(advance);
        log.setPaidAmount(logDto.getPaidAmount());
        log.setPaidDate(LocalDateTime.now());
        log.setRemarks(logDto.getRemarks());
        log.setStatus(logDto.getStatus() != null ? AdvanceStatus.valueOf(logDto.getStatus()) : AdvanceStatus.PAID);
        AdvanceLog savedLog = advanceLogRepository.save(log);
        advance.setStatus(AdvanceStatus.PAID);
        advancesRepository.save(advance);
        return mapToAdvanceLogDto(savedLog);
    }

    @Override
    public List<AdvanceLogDto> getAdvanceLogs(Long advanceId) {
        return advanceLogRepository.findByAdvanceId(advanceId).stream()
            .map(this::mapToAdvanceLogDto)
            .toList();
    }

    @Override
    public OtherPaymentDto createOtherPayment(OtherPaymentDto dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUsername()));
        EmployeeSalaryConfig config = configRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Salary config not found for user " + dto.getUsername()));
        OtherPayments payment = new OtherPayments();
        payment.setType(dto.getType() != null ? OtherPaymentType.valueOf(dto.getType()) : OtherPaymentType.OTHERS);
        payment.setRemark(dto.getRemark());
        payment.setAmount(dto.getAmount());
        payment.setDate(dto.getDate());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus("PAID");
        payment.setConfig(config);
        OtherPayments saved = otherPaymentsRepository.save(payment);
        return mapToOtherPaymentDto(saved);
    }

    @Override
    public OtherPaymentDto updateOtherPayment(Long id, OtherPaymentDto dto) {
        OtherPayments payment = otherPaymentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OtherPayment not found with id " + id));
        payment.setType(dto.getType() != null ? OtherPaymentType.valueOf(dto.getType()) : payment.getType());
        payment.setRemark(dto.getRemark());
        payment.setAmount(dto.getAmount());
        payment.setDate(dto.getDate());
        payment.setStatus("PAID");
        OtherPayments saved = otherPaymentsRepository.save(payment);
        return mapToOtherPaymentDto(saved);
    }

    @Override
    public void deleteOtherPayment(Long id) {
        otherPaymentsRepository.deleteById(id);
    }

    @Override
    public List<OtherPaymentDto> getAllOtherPayments() {
        return otherPaymentsRepository.findAll().stream()
            .map(this::mapToOtherPaymentDto)
            .toList();
    }

}
