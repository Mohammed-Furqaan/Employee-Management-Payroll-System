package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.LeaveRequest;
import com.payroll.employeemanagement.entity.LeaveStatus;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.payroll.employeemanagement.entity.LeaveType;
import com.payroll.employeemanagement.specification.LeaveRequestSpecification;
import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing Leave Request workflows.
 */
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
        // 1. Resolve and verify that the applying employee exists
        if (leaveRequest.getEmployee() == null || leaveRequest.getEmployee().getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee details must be provided");
        }
        Long employeeId = leaveRequest.getEmployee().getEmployeeId();
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        leaveRequest.setEmployee(employee);

        // 2. Date validations
        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new IllegalArgumentException("Leave start date cannot be after the end date");
        }

        // 3. Set workflow initial values
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setApprovedBy(null); // No approver yet

        // 4. Save and return
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest approveOrRejectLeave(Long leaveId, LeaveStatus targetStatus, Long managerId) {
        // 1. Check if the leave request exists
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        // 2. Validate current status (State Machine Guard: only PENDING requests can be changed)
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot update status. The leave request is already in a finalized state: " + leaveRequest.getStatus());
        }

        // 3. Validate target status (must be APPROVED or REJECTED)
        if (targetStatus == LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Cannot transition a leave request back to PENDING");
        }

        // 4. Retrieve and verify the approver
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver employee not found with ID: " + managerId));

        // 5. Enforce Self-Approval Prevention Rule
        if (leaveRequest.getEmployee().getEmployeeId().equals(managerId)) {
            throw new IllegalStateException("Self-approval is forbidden. Employees cannot approve or reject their own leave requests.");
        }

        // 6. Enforce Role Verification Rule
        String roleName = manager.getRole() != null ? manager.getRole().getRoleName() : "";
        if (!roleName.equals("ROLE_ADMIN") && !roleName.equals("ROLE_MANAGER")) {
            throw new IllegalStateException("Unauthorized. Only Admin or Manager profiles can approve/reject leave requests.");
        }

        // 7. Update status and save
        leaveRequest.setStatus(targetStatus);
        leaveRequest.setApprovedBy(manager);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequest> getFilteredLeaves(Long employeeId, LeaveType leaveType, LeaveStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<LeaveRequest> spec = Specification.where(LeaveRequestSpecification.hasEmployeeId(employeeId))
                .and(LeaveRequestSpecification.hasLeaveType(leaveType))
                .and(LeaveRequestSpecification.hasStatus(status))
                .and(LeaveRequestSpecification.isAfterOrEqualStartDate(startDate))
                .and(LeaveRequestSpecification.isBeforeOrEqualEndDate(endDate));
        return leaveRequestRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeavesByEmployeeId(Long employeeId) {
        // Verify employee exists first
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        return leaveRequestRepository.findByEmployeeEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveById(Long leaveId) {
        return leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));
    }
}
