package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.LeaveRequest;
import com.payroll.employeemanagement.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing LeaveRequest entities.
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {

    /**
     * Retrieves all leave requests submitted by a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of leave requests
     */
    List<LeaveRequest> findByEmployeeEmployeeId(Long employeeId);

    /**
     * Retrieves all leave requests filtered by status (e.g. PENDING, APPROVED).
     * Useful for administrators/managers review dashboards.
     * 
     * @param status Status of the leave requests to filter
     * @return List of leave requests
     */
    List<LeaveRequest> findByStatus(LeaveStatus status);
}
