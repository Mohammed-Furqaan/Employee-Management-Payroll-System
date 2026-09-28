package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.LeaveRequest;
import com.payroll.employeemanagement.entity.LeaveStatus;
import com.payroll.employeemanagement.entity.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining operations for Leave Request workflows.
 */
public interface LeaveRequestService {

    /**
     * Submits a new leave request. Sets initial status to PENDING.
     * 
     * @param leaveRequest The leave request details
     * @return The saved leave request
     */
    LeaveRequest applyLeave(LeaveRequest leaveRequest);

    /**
     * Approves or rejects a pending leave request.
     * Enforces status transition and self-approval prevention rules.
     * 
     * @param leaveId Unique ID of the leave request
     * @param status Target status (must be APPROVED or REJECTED)
     * @param managerId Unique ID of the manager performing the action
     * @return The updated leave request
     */
    LeaveRequest approveOrRejectLeave(Long leaveId, LeaveStatus status, Long managerId);

    /**
     * Retrieves all leave requests. (Typically for ADMIN/MANAGER).
     * 
     * @return List of all leave requests
     */
    List<LeaveRequest> getAllLeaves();

    /**
     * Retrieves a paginated and filtered list of leave requests based on search criteria.
     * 
     * @param employeeId Optional employee ID filter
     * @param leaveType Optional leave type filter
     * @param status Optional leave status filter
     * @param startDate Optional date filter (leaves starting on or after)
     * @param endDate Optional date filter (leaves ending on or before)
     * @param pageable Pagination and sorting parameters
     * @return Page of matching LeaveRequest entities
     */
    Page<LeaveRequest> getFilteredLeaves(Long employeeId, LeaveType leaveType, LeaveStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Retrieves all leave requests for a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of leave requests associated with the employee
     */
    List<LeaveRequest> getLeavesByEmployeeId(Long employeeId);

    /**
     * Retrieves a single leave request by its ID.
     * 
     * @param leaveId Unique ID of the leave request
     * @return The leave request details
     */
    LeaveRequest getLeaveById(Long leaveId);
}
