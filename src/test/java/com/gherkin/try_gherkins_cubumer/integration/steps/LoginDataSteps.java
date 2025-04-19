package com.gherkin.try_gherkins_cubumer.integration.steps;

import com.gherkin.try_gherkins_cubumer.AuthRequest;
import com.gherkin.try_gherkins_cubumer.AuthResponse;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import wiremock.com.google.common.collect.ImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class LoginDataSteps {

    private static GenericContainer<?> mockServer;
    private static RestTemplate restTemplate;
    private static String baseUrl;
    private HttpHeaders httpHeaders;
    private AuthRequest authRequest;
    private HttpEntity<AuthRequest> httpEntity;
    private AuthResponse authResponse;
    private ResponseEntity<AuthResponse> responseEntity;
    private HttpClientErrorException httpClientErrorException;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void beforeAllScenarioRun() {
        System.out.println("Starting mock server...");
        mockServer = new GenericContainer<>(DockerImageName.parse("wiremock/wiremock:3.12.1"))
                .withExposedPorts(8080)
                .withFileSystemBind("src/test/resources/wiremock", "/home/wiremock", BindMode.READ_ONLY)
                .waitingFor(Wait.forHealthcheck()); 
        mockServer.start();
        baseUrl = "http://" + mockServer.getHost() + ":" + mockServer.getMappedPort(8080);
        restTemplate = new RestTemplate();
    }

    @AfterAll
    public static void afterAllScenarioFinish() {
        System.out.println("Stopping mock server...");
        if (mockServer != null && mockServer.isHealthy()) {
            mockServer.stop();
        }
    }

    @Before
    public void beforeEachScenarioRun() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        authRequest = new AuthRequest();
        authResponse = new AuthResponse(1, "Kawee Handsome",
                ImmutableList.of("Mr.Ngamwongwan", "Who wanna be good guys"));
    }

    @Given("the user has the following credentials username {string} and password {string}")
    public void the_user_has_the_following_credentials_username_and_password(String username, String password) {
        authRequest.setUsername(username);
        authRequest.setPassword(password);
    }

    @When("the user sends a data login request")
    public void the_user_sends_a_data_login_request() {
        httpEntity = new HttpEntity<>(authRequest, httpHeaders);
        try {
            responseEntity = restTemplate.postForEntity(baseUrl + "/login", httpEntity,
                    AuthResponse.class);
        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("the system should return response status {string}")
    public void the_system_should_return_response_status(String status) {
        switch (status) {
            case "OK":
                assertNotNull(responseEntity);
                assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
                assertEquals(responseEntity.getBody(), authResponse);
                break;
            default:
                assertNull(responseEntity);
                assertEquals(httpClientErrorException.getStatusCode(), HttpStatus.UNAUTHORIZED);
                assertNull(httpClientErrorException.getResponseBodyAs(AuthResponse.class));
                break;
        }
    }
}
