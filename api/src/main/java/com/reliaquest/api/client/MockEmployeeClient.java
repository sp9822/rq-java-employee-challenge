package com.reliaquest.api.client;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mock-employee", url = "${mock.base-url}")
public interface MockEmployeeClient {

    @GetMapping
    ResponseWrapper<List<Employee>> getEmployees();

    @GetMapping("/{id}")
    ResponseWrapper<Employee> getEmployee(@PathVariable("id") UUID id);

    @PostMapping
    ResponseWrapper<Employee> createEmployee(@RequestBody CreateEmployeeInput input);

    @DeleteMapping
    ResponseWrapper<Boolean> deleteEmployee(@RequestBody DeleteRequest input);

    record DeleteRequest(String name) {}

    class ResponseWrapper<T> {
        private T data;
        private String status;
        private String error;

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}


