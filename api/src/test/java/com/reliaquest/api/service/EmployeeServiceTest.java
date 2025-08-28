package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.model.Employee;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmployeeServiceTest {

    @Test
    void getAllEmployees_returnsList() {
        var client = Mockito.mock(MockEmployeeClient.class);
        var svc = new EmployeeService(client);
        var resp = new MockEmployeeClient.ResponseWrapper<List<Employee>>();
        resp.setData(List.of(
                new Employee(null, "Alice", 100, 30, "Engineer", "a@x.com"),
                new Employee(null, "Bob", 200, 40, "Lead", "b@x.com")
        ));
        when(client.getEmployees()).thenReturn(resp);

        var list = svc.getAllEmployees();
        assertThat(list).hasSize(2);
    }
}


