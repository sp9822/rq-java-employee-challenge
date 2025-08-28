package com.reliaquest.api.service;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final MockEmployeeClient client;

    public List<Employee> getAllEmployees() {
        var response = client.getEmployees();
        return response != null && response.getData() != null ? response.getData() : List.of();
    }

    public List<Employee> searchEmployeesByName(String searchString) {
        return getAllEmployees().stream()
                .filter(e -> e.getName() != null && e.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeeById(UUID id) {
        try {
            var response = client.getEmployee(id);
            return Optional.ofNullable(response).map(MockEmployeeClient.ResponseWrapper::getData);
        } catch (Exception ex) {
            log.warn("Employee id {} not found", id);
            return Optional.empty();
        }
    }

    public int getHighestSalary() {
        return getAllEmployees().stream()
                .filter(e -> e.getSalary() != null)
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getSalary, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Optional<Employee> createEmployee(CreateEmployeeInput input) {
        var response = client.createEmployee(input);
        return Optional.ofNullable(response).map(MockEmployeeClient.ResponseWrapper::getData);
    }

    public Optional<String> deleteEmployeeById(UUID id) {
        var employeeOpt = getEmployeeById(id);
        if (employeeOpt.isEmpty() || employeeOpt.get().getName() == null) {
            return Optional.empty();
        }
        client.deleteEmployee(new MockEmployeeClient.DeleteRequest(employeeOpt.get().getName()));
        return Optional.of(employeeOpt.get().getName());
    }
}


