package com.reliaquest.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.client.model.request.CreateMockEmployeeInput;
import com.reliaquest.api.client.model.response.MockEmployee;
import com.reliaquest.api.client.model.response.Response;

import java.util.List;
import java.util.UUID;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
class MockEmployeeControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

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
    void getAllMockEmployees_returnsList() throws Exception {
        var payload = new Response<>(
                List.of(
                        new MockEmployee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com"),
                        new MockEmployee(UUID.randomUUID(), "Bob", 200, 40, "Lead", "b@x.com")),
                Response.Status.HANDLED,
                null);

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
    void getMockEmployeeById_returnsMockEmployee() throws Exception {
        var emp = new MockEmployee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var payload = new Response<>(emp, Response.Status.HANDLED, null);
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(payload))
                .setResponseCode(200));

        ResponseEntity<String> resp =
                restTemplate.getForEntity(baseUrl() + "/api/v1/employee/" + emp.getId(), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var json = objectMapper.readTree(resp.getBody());
        if (json.isObject() && json.has("data")) {
            json = json.get("data");
        }
        assertThat(json.get("employee_name").asText()).isEqualTo("Alice");
    }

    @Test
    void createMockEmployee_returnsCreated() throws Exception {
        var emp = new MockEmployee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var payload = new Response<>(emp, Response.Status.HANDLED, null);
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(payload))
                .setResponseCode(200));

        var input = new CreateMockEmployeeInput();
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
    void deleteMockEmployee_returnsName() throws Exception {
        var emp = new MockEmployee(UUID.randomUUID(), "Alice", 100, 30, "Engineer", "a@x.com");
        var findPayload = new Response<>(emp, Response.Status.HANDLED, null);
        var deletePayload = new Response<>(true, Response.Status.HANDLED, null);
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(findPayload))
                .setResponseCode(200));
        mockServer.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(deletePayload))
                .setResponseCode(200));

        ResponseEntity<String> resp = restTemplate.exchange(
                baseUrl() + "/api/v1/employee/" + emp.getId(),
                HttpMethod.DELETE,
                new HttpEntity<Void>(new HttpHeaders()),
                String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo("Alice");
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}