package com.reliaquest.api.service;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.client.model.request.CreateMockEmployeeInput;
import com.reliaquest.api.client.model.request.DeleteMockEmployeeInput;
import com.reliaquest.api.client.model.response.MockEmployee;
import com.reliaquest.api.client.model.response.Response;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final MockEmployeeClient client;

    public List<MockEmployee> getAllEmployees() {
        Response<List<MockEmployee>> response = client.getEmployees();
        return response == null ? Collections.emptyList() : response.data();
    }

    public List<MockEmployee> searchEmployeesByName(String searchString) {
        List<MockEmployee> employees = getAllEmployees();
        return employees.stream()
                .filter(e -> e != null
                        && e.getName() != null
                        && e.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<MockEmployee> getEmployeeById(UUID id) {
        try {
            ResponseEntity<Response<MockEmployee>> responseResponseEntity = client.getEmployee(id);
            return Optional.ofNullable(responseResponseEntity)
                    .filter(re -> re != null && re.getBody() != null)
                    .map(re -> re.getBody().data());
        } catch (Exception ex) {
            log.warn("MockEmployee id {} not found", id);
            return Optional.empty();
        }
    }

    public int getHighestSalary() {
        List<MockEmployee> employees = getAllEmployees();
        return employees.stream()
                .filter(e -> e != null && e.getSalary() != null)
                .map(MockEmployee::getSalary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        List<MockEmployee> employees = getAllEmployees();
        return employees.stream()
                .filter(e -> e != null && e.getSalary() != null)
                .sorted(Comparator.comparing(MockEmployee::getSalary, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed())
                .limit(10)
                .map(MockEmployee::getName)
                .collect(Collectors.toList());
    }

    public Optional<MockEmployee> createEmployee(CreateMockEmployeeInput input) {
        Response<MockEmployee> response = client.createEmployee(input);
        return Optional.ofNullable(response).map(Response::data);
    }

    public Optional<String> deleteEmployeeById(UUID id) {
        Optional<MockEmployee> employeeOpt = getEmployeeById(id);
        if (employeeOpt.isEmpty()) {
            return Optional.empty();
        }
        String mockEmployeeName = employeeOpt.get().getName();
        return deleteEmployee(new DeleteMockEmployeeInput(mockEmployeeName));
    }

    private Optional<String> deleteEmployee(@Valid DeleteMockEmployeeInput deleteMockEmployeeInput) {
        Response<Boolean> response = client.deleteEmployee(deleteMockEmployeeInput);
        if (response == null || !Boolean.TRUE.equals(response.data())) {
            return Optional.empty();
        }
        return Optional.of(deleteMockEmployeeInput.getName());
    }
}