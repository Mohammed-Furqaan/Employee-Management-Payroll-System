package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.*;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceImplTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private Employee employee;
    private Employee manager;
    private Role managerRole;
    private Role employeeRole;

    @BeforeEach
    void setUp() {
        managerRole = Role.builder().roleId(1L).roleName("ROLE_MANAGER").build();
        employeeRole = Role.builder().roleId(2L).roleName("ROLE_EMPLOYEE").build();

        employee = Employee.builder()
                .employeeId(10L)
                .name("Alice")
                .email("alice@company.com")
                .role(employeeRole)
                .build();

        manager = Employee.builder()
                .employeeId(20L)
                .name("Bob Manager")
                .email("bob@company.com")
                .role(managerRole)
                .build();
    }

    @Test
    @DisplayName("Should successfully apply for leave in PENDING status")
    void applyLeave_Success() {
        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .leaveType(LeaveType.VACATION)
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(5))
                .build();

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(i -> {
            LeaveRequest lr = i.getArgument(0);
            lr.setLeaveId(1L);
            return lr;
        });

        LeaveRequest created = leaveRequestService.applyLeave(request);

        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(LeaveStatus.PENDING);
        assertThat(created.getApprovedBy()).isNull();
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("Should reject leave application when start date is after end date")
    void applyLeave_InvalidDateRange() {
        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .leaveType(LeaveType.CASUAL)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(5)) // before start date
                .build();

        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> leaveRequestService.applyLeave(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Leave start date cannot be after the end date");

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow manager to approve a pending leave request")
    void approveLeave_Success() {
        LeaveRequest pendingLeave = LeaveRequest.builder()
                .leaveId(100L)
                .employee(employee)
                .leaveType(LeaveType.SICK)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .status(LeaveStatus.PENDING)
                .build();

        when(leaveRequestRepository.findById(100L)).thenReturn(Optional.of(pendingLeave));
        when(employeeRepository.findById(20L)).thenReturn(Optional.of(manager));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(i -> i.getArgument(0));

        LeaveRequest result = leaveRequestService.approveOrRejectLeave(100L, LeaveStatus.APPROVED, 20L);

        assertThat(result.getStatus()).isEqualTo(LeaveStatus.APPROVED);
        assertThat(result.getApprovedBy()).isEqualTo(manager);
    }

    @Test
    @DisplayName("Should forbid self-approval (employee approving their own leave)")
    void approveLeave_SelfApprovalForbidden() {
        LeaveRequest pendingLeave = LeaveRequest.builder()
                .leaveId(100L)
                .employee(employee)
                .status(LeaveStatus.PENDING)
                .build();

        when(leaveRequestRepository.findById(100L)).thenReturn(Optional.of(pendingLeave));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> leaveRequestService.approveOrRejectLeave(100L, LeaveStatus.APPROVED, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Self-approval is forbidden");

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject status change if leave request is already in a finalized state (duplicate approval guard)")
    void approveLeave_AlreadyFinalized() {
        LeaveRequest approvedLeave = LeaveRequest.builder()
                .leaveId(100L)
                .employee(employee)
                .status(LeaveStatus.APPROVED) // already approved
                .approvedBy(manager)
                .build();

        when(leaveRequestRepository.findById(100L)).thenReturn(Optional.of(approvedLeave));

        assertThatThrownBy(() -> leaveRequestService.approveOrRejectLeave(100L, LeaveStatus.REJECTED, 20L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already in a finalized state");

        verify(leaveRequestRepository, never()).save(any());
    }
}
