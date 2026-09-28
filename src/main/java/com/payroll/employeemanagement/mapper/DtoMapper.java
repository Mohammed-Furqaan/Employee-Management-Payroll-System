package com.payroll.employeemanagement.mapper;

import com.payroll.employeemanagement.dto.*;
import com.payroll.employeemanagement.entity.*;
import org.springframework.stereotype.Component;

/**
 * Utility mapper component converting JPA entities to safe API Response DTOs.
 */
@Component
public class DtoMapper {

    public DepartmentResponse toDepartmentResponse(Department department) {
        if (department == null) return null;
        return DepartmentResponse.builder()
                .departmentId(department.getDepartmentId())
                .name(department.getName())
                .headEmployeeId(department.getHeadEmployee() != null ? department.getHeadEmployee().getEmployeeId() : null)
                .headEmployeeName(department.getHeadEmployee() != null ? department.getHeadEmployee().getName() : null)
                .build();
    }

    public RoleResponse toRoleResponse(Role role) {
        if (role == null) return null;
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build();
    }

    public EmployeeResponse toEmployeeResponse(Employee employee) {
        if (employee == null) return null;
        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .name(employee.getName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .hireDate(employee.getHireDate())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .roleId(employee.getRole() != null ? employee.getRole().getRoleId() : null)
                .roleName(employee.getRole() != null ? employee.getRole().getRoleName() : null)
                .managerId(employee.getManager() != null ? employee.getManager().getEmployeeId() : null)
                .managerName(employee.getManager() != null ? employee.getManager().getName() : null)
                .build();
    }

    public SalaryResponse toSalaryResponse(Salary salary) {
        if (salary == null) return null;
        return SalaryResponse.builder()
                .salaryId(salary.getSalaryId())
                .employeeId(salary.getEmployee() != null ? salary.getEmployee().getEmployeeId() : null)
                .employeeName(salary.getEmployee() != null ? salary.getEmployee().getName() : null)
                .basicPay(salary.getBasicPay())
                .allowances(salary.getAllowances())
                .deductions(salary.getDeductions())
                .netPay(salary.getNetPay())
                .month(salary.getMonth())
                .year(salary.getYear())
                .build();
    }

    public LeaveResponse toLeaveResponse(LeaveRequest leaveRequest) {
        if (leaveRequest == null) return null;
        return LeaveResponse.builder()
                .leaveId(leaveRequest.getLeaveId())
                .employeeId(leaveRequest.getEmployee() != null ? leaveRequest.getEmployee().getEmployeeId() : null)
                .employeeName(leaveRequest.getEmployee() != null ? leaveRequest.getEmployee().getName() : null)
                .leaveType(leaveRequest.getLeaveType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .status(leaveRequest.getStatus())
                .approvedById(leaveRequest.getApprovedBy() != null ? leaveRequest.getApprovedBy().getEmployeeId() : null)
                .approvedByName(leaveRequest.getApprovedBy() != null ? leaveRequest.getApprovedBy().getName() : null)
                .build();
    }

    public AttendanceResponse toAttendanceResponse(Attendance attendance) {
        if (attendance == null) return null;
        return AttendanceResponse.builder()
                .attendanceId(attendance.getAttendanceId())
                .employeeId(attendance.getEmployee() != null ? attendance.getEmployee().getEmployeeId() : null)
                .employeeName(attendance.getEmployee() != null ? attendance.getEmployee().getName() : null)
                .date(attendance.getDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .status(attendance.getStatus())
                .build();
    }
}
