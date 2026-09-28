package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Attendance;
import com.payroll.employeemanagement.entity.AttendanceStatus;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.exception.ResourceAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.AttendanceRepository;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing Employee Attendance check-ins and check-outs.
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    // Late arrival threshold set to 09:30 AM
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 30);

    @Override
    @Transactional
    public Attendance checkIn(Long employeeId) {
        // 1. Verify employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        LocalDate today = LocalDate.now();

        // 2. Prevent duplicate check-in
        Optional<Attendance> existingRecord = attendanceRepository.findByEmployeeEmployeeIdAndDate(employeeId, today);
        if (existingRecord.isPresent()) {
            throw new ResourceAlreadyExistsException("Employee already checked in today at: " + existingRecord.get().getCheckIn());
        }

        // 3. Capture current time and evaluate late status
        LocalTime checkInTime = LocalTime.now();
        AttendanceStatus status = checkInTime.isAfter(LATE_THRESHOLD) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;

        // 4. Build and save
        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkIn(checkInTime)
                .checkOut(null)
                .status(status)
                .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance checkOut(Long employeeId) {
        // 1. Verify employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        LocalDate today = LocalDate.now();

        // 2. Retrieve today's check-in record
        Attendance attendance = attendanceRepository.findByEmployeeEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot check out. No check-in record found for today."));

        // 3. Prevent duplicate check-out
        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException("Employee has already checked out today at: " + attendance.getCheckOut());
        }

        // 4. Capture current time and save
        LocalTime checkOutTime = LocalTime.now();
        attendance.setCheckOut(checkOutTime);

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceHistoryByEmployeeId(Long employeeId) {
        // Confirm employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        return attendanceRepository.findByEmployeeEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> getTodaysAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeEmployeeIdAndDate(employeeId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendanceForDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}
