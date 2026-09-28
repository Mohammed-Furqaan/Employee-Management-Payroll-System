package com.payroll.employeemanagement.specification;

import com.payroll.employeemanagement.entity.LeaveRequest;
import com.payroll.employeemanagement.entity.LeaveStatus;
import com.payroll.employeemanagement.entity.LeaveType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Specifications for dynamic filtering of LeaveRequest entities using the JPA Criteria API.
 */
public class LeaveRequestSpecification {

    /**
     * Filters leave requests applied by a specific employee.
     */
    public static Specification<LeaveRequest> hasEmployeeId(Long employeeId) {
        return (root, query, cb) -> {
            if (employeeId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("employee").get("employeeId"), employeeId);
        };
    }

    /**
     * Filters leave requests by leave type (SICK, CASUAL, VACATION).
     */
    public static Specification<LeaveRequest> hasLeaveType(LeaveType leaveType) {
        return (root, query, cb) -> {
            if (leaveType == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("leaveType"), leaveType);
        };
    }

    /**
     * Filters leave requests by approval status (PENDING, APPROVED, REJECTED).
     */
    public static Specification<LeaveRequest> hasStatus(LeaveStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filters leave requests that start on or after a specific date.
     */
    public static Specification<LeaveRequest> isAfterOrEqualStartDate(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
        };
    }

    /**
     * Filters leave requests that end on or before a specific date.
     */
    public static Specification<LeaveRequest> isBeforeOrEqualEndDate(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("endDate"), endDate);
        };
    }
}
