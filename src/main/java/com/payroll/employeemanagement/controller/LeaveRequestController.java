package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.LeaveApplyRequest;
import com.payroll.employeemanagement.dto.LeaveResponse;
import com.payroll.employeemanagement.entity.LeaveRequest;
import com.payroll.employeemanagement.entity.LeaveStatus;
import com.payroll.employeemanagement.entity.LeaveType;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.security.CustomUserDetails;
import com.payroll.employeemanagement.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller exposing Leave Request API endpoints using DTOs.
 * Integrates contextual security binding and SpEL authorizations.
 */
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final DtoMapper dtoMapper;

    /**
     * POST /api/leaves : Submit a leave request.
     * Accessible by any authenticated employee.
     */
    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(
            @Valid @RequestBody LeaveApplyRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(userDetails.getEmployee())
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        LeaveRequest createdRequest = leaveRequestService.applyLeave(leaveRequest);
        return new ResponseEntity<>(dtoMapper.toLeaveResponse(createdRequest), HttpStatus.CREATED);
    }

    /**
     * GET /api/leaves : Retrieve all leave requests (Paginated & Filtered).
     * Accessible by ADMIN and MANAGER roles only.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<LeaveResponse>> getAllLeaves(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<LeaveRequest> leaves = leaveRequestService.getFilteredLeaves(employeeId, leaveType, status, startDate, endDate, pageable);
        return ResponseEntity.ok(leaves.map(dtoMapper::toLeaveResponse));
    }

    /**
     * GET /api/leaves/{id} : Retrieve a single leave request by ID.
     * Accessible by ADMIN, MANAGER, or the Employee who created the request.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeaveById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LeaveRequest leaveRequest = leaveRequestService.getLeaveById(id);

        String roleName = userDetails.getEmployee().getRole().getRoleName();
        boolean isOwner = leaveRequest.getEmployee().getEmployeeId().equals(userDetails.getEmployee().getEmployeeId());

        if (!roleName.equals("ROLE_ADMIN") && !roleName.equals("ROLE_MANAGER") && !isOwner) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(dtoMapper.toLeaveResponse(leaveRequest));
    }

    /**
     * GET /api/leaves/employee/{employeeId} : Retrieve leaves submitted by a specific employee.
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #employeeId == principal.employee.employeeId")
    public ResponseEntity<Page<LeaveResponse>> getLeavesByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        Page<LeaveRequest> leaves = leaveRequestService.getFilteredLeaves(employeeId, leaveType, status, startDate, endDate, pageable);
        return ResponseEntity.ok(leaves.map(dtoMapper::toLeaveResponse));
    }

    /**
     * PUT /api/leaves/{id}/status : Approve or reject a leave request.
     * Accessible by ADMIN and MANAGER roles only.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LeaveResponse> approveOrRejectLeave(
            @PathVariable Long id,
            @RequestParam LeaveStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long managerId = userDetails.getEmployee().getEmployeeId();
        LeaveRequest updatedRequest = leaveRequestService.approveOrRejectLeave(id, status, managerId);
        return ResponseEntity.ok(dtoMapper.toLeaveResponse(updatedRequest));
    }
}
