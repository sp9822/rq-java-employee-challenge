package com.reliaquest.api.controller.impl;

import com.reliaquest.api.client.model.request.CreateMockEmployeeInput;
import com.reliaquest.api.client.model.response.MockEmployee;
import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<MockEmployee, CreateMockEmployeeInput> {

    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<MockEmployee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<MockEmployee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok(employeeService.searchEmployeesByName(searchString));
    }

    @Override
    public ResponseEntity<MockEmployee> getEmployeeById(String id) {
        try {
            return employeeService
                    .getEmployeeById(UUID.fromString(id))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.getHighestSalary());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.getTop10HighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<MockEmployee> createEmployee(@Valid CreateMockEmployeeInput employeeInput) {
        return employeeService
                .createEmployee(employeeInput)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        try {
            return employeeService
                    .deleteEmployeeById(UUID.fromString(id))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}