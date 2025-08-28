package com.reliaquest.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reliaquest.api.controller.impl.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmployeeController.class)
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private EmployeeService employeeService;

    @Test
    void getAllEmployees_ok() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(new Employee()));
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk());
    }

    @Test
    void getEmployeeById_notFound() throws Exception {
        when(employeeService.getEmployeeById(UUID.fromString("00000000-0000-0000-0000-000000000000"))).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/employee/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}


