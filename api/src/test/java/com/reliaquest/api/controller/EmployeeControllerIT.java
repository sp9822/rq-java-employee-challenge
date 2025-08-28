package com.reliaquest.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIT {

    @LocalServerPort int port;

    @Autowired TestRestTemplate restTemplate;
    @Autowired ObjectMapper objectMapper;

    static MockWebServer mockServer;

    @BeforeAll
    static void startServer() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        mockServer.shutdown();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("mock.base-url", () -> String.format("http://localhost:%d/api/v1/employee", mockServer.getPort()));
    }

    

    @Test
    void getAllEmployees_returnsList() throws Exception {
        var payload = new Response<List<Employee>>();
        payload.data = List.of(
                new Employee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com"),
                new Employee(UUID.randomUUID(), "Bob", 200, 40, "Lead", "b@x.com")
        );
        payload.status = "Successfully processed request.";

        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(payload))
                .setResponseCode(200));

        ResponseEntity<String> resp = restTemplate.getForEntity(baseUrl() + "/api/v1/employee", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var json = objectMapper.readTree(resp.getBody());
        if (json.isObject() && json.has("data")) {
            json = json.get("data");
        }
        assertThat(json.isArray()).isTrue();
        assertThat(json.size()).isEqualTo(2);
    }

    @Test
    void getEmployeeById_returnsEmployee() throws Exception {
        var emp = new Employee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var payload = new Response<Employee>();
        payload.data = emp;
        payload.status = "Successfully processed request.";
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(payload))
                .setResponseCode(200));

        ResponseEntity<String> resp = restTemplate.getForEntity(baseUrl() + "/api/v1/employee/" + emp.getId(), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var json = objectMapper.readTree(resp.getBody());
        if (json.isObject() && json.has("data")) {
            json = json.get("data");
        }
        assertThat(json.get("employee_name").asText()).isEqualTo("Alice");
    }

    @Test
    void createEmployee_returnsCreated() throws Exception {
        var emp = new Employee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var payload = new Response<Employee>();
        payload.data = emp;
        payload.status = "Successfully processed request.";
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(payload))
                .setResponseCode(200));

        var input = new CreateEmployeeInput();
        input.setName("Alice");
        input.setSalary(100);
        input.setAge(30);
        input.setTitle("Engineer");

        ResponseEntity<String> resp = restTemplate.postForEntity(baseUrl() + "/api/v1/employee", input, String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var json = objectMapper.readTree(resp.getBody());
        if (json.isObject() && json.has("data")) {
            json = json.get("data");
        }
        assertThat(json.get("employee_name").asText()).isEqualTo("Alice");
    }

    @Test
    void deleteEmployee_returnsName() throws Exception {
        var emp = new Employee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var findPayload = new Response<Employee>();
        findPayload.data = emp;
        findPayload.status = "Successfully processed request.";
        var deletePayload = new Response<Boolean>();
        deletePayload.data = true;
        deletePayload.status = "Successfully processed request.";
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(findPayload))
                .setResponseCode(200));
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(deletePayload))
                .setResponseCode(200));

        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl() + "/api/v1/employee/" + emp.getId(), HttpMethod.DELETE, new HttpEntity<Void>(new HttpHeaders()), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo("Alice");
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    static class Response<T> {
        public T data;
        public String status;
        public String error;
    }
}


