package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.RoleResponse;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing Role lookup endpoints using DTOs.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;
    private final DtoMapper dtoMapper;

    /**
     * GET /api/roles : Retrieve all available user roles.
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleRepository.findAll().stream()
                .map(dtoMapper::toRoleResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }
}
