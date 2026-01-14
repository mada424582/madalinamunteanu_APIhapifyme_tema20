package com.hapifyme.api.tests;

import com.hapifyme.api.models.*;
import com.hapifyme.api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertTrue;

public class NewLoginTest {

    @Test
    public void fullUserFlowTest() {

        // Create: register user
        String firstName = "John";
        String lastName = "Doe";
        String email = DataGenerator.randomEmail();
        String password = "Accident2020!";

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        Response registerResponse = RestAssured
                .given()
                .log().all()
                .contentType("application/json")
                .body(registerRequest)
                .post(BASE_URL + "/user/register.php")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();

        RegisterResponse regBody = registerResponse.as(RegisterResponse.class);
        String apiKey = regBody.getApi_key();
        String userId = regBody.getUser_id();

        // Async check: poll for confirmation_token (simplificat)
        String confirmationToken = regBody.getConfirmation_token();
        assertNotNull(confirmationToken, "Confirmation token should exist");

        // Confirm account
        RestAssured
                .given()
                .log().all()
                .queryParam("token", confirmationToken)
                .get(BASE_URL + "/user/confirm_email.php")
                .then()
                .log().all()
                .statusCode(200);

        // Authenticate: login
        LoginRequest loginRequest = new LoginRequest(regBody.getUsername(), password);

        Response loginResponse = RestAssured
                .given()
                .log().all()
                .contentType("application/json")
                .body(loginRequest)
                .post(BASE_URL + "/user/login.php")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        String bearerToken = loginResponse.as(LoginResponse.class).getToken();
        assertNotNull(bearerToken, "Bearer token should not be null");

        // Read & Validate: get profile
        Response profileResponse = RestAssured
                .given()
                .log().all()
                .header("Authorization", apiKey)
                .queryParam("user_id", userId)
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        ProfileResponse profile = profileResponse.as(ProfileResponse.class);
        assertEquals(profile.getUser().getEmail(), email);
        assertEquals(profile.getUser().getFirst_name(), firstName);
        assertEquals(profile.getUser().getLast_name(), lastName);
        assertEquals(profile.getUser().getUsername(), regBody.getUsername());

        // Update profile
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("Johnny", "DoeUpdated", userId, email);

        Response updateResponse = RestAssured
                .given()
                .log().all()
                .header("Authorization", apiKey)
                .queryParam("user_id", userId)
                .contentType("application/json")
                .body(updateRequest)
                .put(BASE_URL + "/user/update_profile.php")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        assertEquals("success", updateResponse.jsonPath().getString("status"));

        // Delete profile
        RestAssured
                .given()
                .log().all()
                .header("Authorization", "Bearer " + bearerToken)
                .delete(BASE_URL + "/user/delete_profile.php")
                .then()
                .log().all()
                .statusCode(200);

        // Negative check: profile should not be accessible
        Response profileAfterDelete = RestAssured
                .given()
                .log().all()
                .header("Authorization", "Bearer " + bearerToken)
                .queryParam("user_id", userId)
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .log().all()
                .extract()
                .response();

        int status = profileAfterDelete.getStatusCode();

        assertTrue(
                status == 401 || status == 404,
                "Expected 401 or 404 after delete, but got " + status
        );

    }
}

