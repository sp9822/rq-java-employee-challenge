package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.MockEmployeeClient;
import com.reliaquest.api.client.model.response.MockEmployee;
import com.reliaquest.api.client.model.response.Response;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmployeeServiceTest {

    @Test
    void getAllEmployees_returnsList() {
        var client = Mockito.mock(MockEmployeeClient.class);
        var svc = new EmployeeService(client);
        var resp = new Response<List<MockEmployee>>(getSampleMockEmployeeList(), Response.Status.HANDLED, null);
        when(client.getEmployees()).thenReturn(resp);

        var list = svc.getAllEmployees();
        assertThat(list).hasSize(2);
    }

    private List<MockEmployee> getSampleMockEmployeeList() {
        return List.of(
                new MockEmployee(null, "Alice", 100, 30, "Engineer", "a@x.com"),
                new MockEmployee(null, "Bob", 200, 40, "Lead", "b@x.com"));
    }
}