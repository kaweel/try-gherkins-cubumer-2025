package com.gherkin.try_gherkins_cubumer.integration.steps;

import com.gherkin.try_gherkins_cubumer.AuthRequest;
import com.gherkin.try_gherkins_cubumer.AuthResponse;

import io.cucumber.datatable.DataTable;
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

public class LoginDataTableSteps {

    private static RestTemplate restTemplate;
    private static String baseUrl;
    private HttpHeaders httpHeaders;
    private AuthRequest authRequest;
    private HttpEntity<AuthRequest> httpEntity;
    private AuthResponse authResponse;
    private ResponseEntity<AuthResponse> responseEntity;
    private HttpClientErrorException httpClientErrorException;

    @BeforeAll
    public static void beforeAllScenarioRun() {
        baseUrl = "http://127.0.0.1:9090";
        restTemplate = new RestTemplate();
    }

    @Before
    public void beforeEachScenarioRun() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        authRequest = new AuthRequest();
        authResponse = new AuthResponse(1, "Kawee Handsome",
                ImmutableList.of("Mr.Ngamwongwan", "Who wanna be good guys"));
    }

    @Given("the user has the following each credentials")
    public void the_user_has_the_following_each_credentials_username_and_password(DataTable dataTable) {
        var data = dataTable.asMap(String.class, String.class);
        authRequest.setUsername(data.get("username"));
        authRequest.setPassword(data.get("password"));
    }

    @When("the user sends each data login request")
    public void the_user_sends_each_data_login_request() {
        httpEntity = new HttpEntity<>(authRequest, httpHeaders);
        try {
            responseEntity = restTemplate.postForEntity(baseUrl + "/login", httpEntity,
                    AuthResponse.class);
        } catch (HttpClientErrorException e) {
            httpClientErrorException = e;
        }
    }

    @Then("the system should return each response status {string}")
    public void the_system_should_return_each_response_status(String status) {
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
