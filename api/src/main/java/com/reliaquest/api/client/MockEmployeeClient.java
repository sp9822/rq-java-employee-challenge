package com.reliaquest.api.client;

import com.reliaquest.api.client.model.request.CreateMockEmployeeInput;
import com.reliaquest.api.client.model.request.DeleteMockEmployeeInput;
import com.reliaquest.api.client.model.response.MockEmployee;
import com.reliaquest.api.client.model.response.Response;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mock-employee", url = "${mock.base-url}")
public interface MockEmployeeClient {

    @GetMapping
    Response<List<MockEmployee>> getEmployees();

    @GetMapping("/{id}")
    ResponseEntity<Response<MockEmployee>> getEmployee(@PathVariable("id") UUID id);

    @PostMapping
    Response<MockEmployee> createEmployee(@RequestBody CreateMockEmployeeInput input);

    @DeleteMapping
    Response<Boolean> deleteEmployee(@RequestBody DeleteMockEmployeeInput input);
}