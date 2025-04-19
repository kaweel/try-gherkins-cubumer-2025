package com.gherkin.try_gherkins_cubumer.integration.steps;

import com.gherkin.try_gherkins_cubumer.AuthRequest;
import com.gherkin.try_gherkins_cubumer.AuthResponse;
import com.github.tomakehurst.wiremock.WireMockServer;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import wiremock.com.google.common.collect.ImmutableList;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
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

public class LoginSteps {

    private static WireMockServer mockServer;
    private static RestTemplate restTemplate;
    private static HttpHeaders httpHeaders;
    private AuthRequest authRequest;
    private HttpEntity<AuthRequest> httpEntity;
    private AuthResponse authResponse;
    private ResponseEntity<AuthResponse> responseEntity;
    private HttpClientErrorException httpClientErrorException;

    @BeforeAll
    public static void beforeAllScenarioRun() {
        System.out.println("Starting mock server...");
        mockServer = new WireMockServer(options().port(9090)
                .usingFilesUnderDirectory("src/test/resources/wiremock"));
        mockServer.start();
        restTemplate = new RestTemplate();
    }

    @AfterAll
    public static void afterAllScenarioFinish() {
        System.out.println("Stopping mock server...");
        if (mockServer != null && mockServer.isRunning()) {
            mockServer.stop();
        }
    }

    @Before
    public void beforeEachScenarioRun() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        authRequest = new AuthRequest("kaweel", "h@ndS0m3");
        authResponse = new AuthResponse(1, "Kawee Handsome",
                ImmutableList.of("Mr.Ngamwongwan", "Who wanna be good guys"));
    }

    @Given("the user has an invalid username")
    public void the_user_has_an_invalid_username() {
        authRequest.setUsername("usr");
    }

    @When("the user sends a login request")
    public void the_user_sends_a_login_request() {
        httpEntity = new HttpEntity<>(authRequest, httpHeaders);
        try {
            responseEntity = restTemplate.postForEntity("http://127.0.0.1:9090/login", httpEntity, AuthResponse.class);
        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("the system should return an error response")
    public void the_system_should_return_an_error_response() {
        assertNull(responseEntity);
        assertEquals(httpClientErrorException.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(httpClientErrorException.getResponseBodyAs(AuthResponse.class));
    }

    @Given("the user has an invalid password")
    public void the_user_has_an_invalid_password() {
        authRequest.setPassword("u@1y");
    }

    @Given("the user has valid credential")
    public void the_user_has_valid_credential() {
        authRequest.setUsername("kaweel");
        authRequest.setPassword("h@ndS0m3");
    }

    @Then("the system should return a success response")
    public void the_system_should_return_a_success_response() {
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), authResponse);
    }
}
