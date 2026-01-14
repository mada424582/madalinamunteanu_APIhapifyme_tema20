package com.hapifyme.api.tests;

import com.hapifyme.api.models.*;
import com.hapifyme.api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;
import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginTest {

    @Test (enabled = false)
    public void loginAndGetProfileTest() {

        // Test data
        String email = DataGenerator.randomEmail();
        String password = "Accident2020!";
        String firstName = "John";
        String lastName = "Doe";

        // Register
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        Response registerResponse = RestAssured
                .given()
                .log().all() // log request
                .contentType("application/json")
                .body(registerRequest)
                .post(BASE_URL + "/user/register.php")
                .then()
                .log().all() // log response
                .statusCode(201)
                .extract()
                .response();

        RegisterResponse regBody = registerResponse.as(RegisterResponse.class);
        String confirmationToken = regBody.getConfirmation_token();
        String username = regBody.getUsername();
        String apiKey = regBody.getApi_key();

        // Confirm account
        RestAssured
                .given()
                .log().all() // log request
                .queryParam("token", confirmationToken)
                .get(BASE_URL + "/user/confirm_email.php")
                .then()
                .log().all() // log response
                .statusCode(200);

        // Login
        LoginRequest loginRequest = new LoginRequest(username, password);

        Response loginResponse = RestAssured
                .given()
                .log().all() // log request
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post(BASE_URL + "/user/login.php")
                .then()
                .log().all() // log response
                .statusCode(200)
                .extract()
                .response();

        LoginResponse loginBody = loginResponse.as(LoginResponse.class);
        String bearerToken = loginBody.getToken();

        // Assert token exists
        assertNotNull(bearerToken, "Bearer token should not be null after login");

        // Get user profile using API key
        Response profileResponse = RestAssured
                .given()
                .log().all() // log request
                .header("Authorization", apiKey) // folosește API Key-ul din register
                .queryParam("user_id", regBody.getUser_id())
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .log().all() // log response
                .statusCode(200)
                .extract()
                .response();

        ProfileResponse profile = profileResponse.as(ProfileResponse.class);

        // Validate profile data
        assertEquals(profile.getUser().getEmail(), email, "Email nu corespunde");
        assertEquals(profile.getUser().getFirst_name(), firstName, "First name nu corespunde");
        assertEquals(profile.getUser().getLast_name(), lastName, "Last name nu corespunde");
        assertEquals(profile.getUser().getUsername(), username, "Username nu corespunde");

        UpdateProfileRequest updateRequest =
                new UpdateProfileRequest("Johnny", "DoeUpdated", regBody.getUser_id(), email);

        Response updateResponse = RestAssured
                .given()
                .log().all()
                .contentType("application/json")
                .header("Authorization", apiKey)
                .queryParam("user_id", regBody.getUser_id())
                .body(updateRequest)
                .when()
                .put(BASE_URL + "/user/update_profile.php")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(
                "success",
                updateResponse.jsonPath().getString("status"),
                "Update profile should succeed"
        );

        //DELETE
        Response deleteResponse = RestAssured
                .given()
                .log().all()
                .header("Authorization", "Bearer " + bearerToken)
                .when()
                .delete(BASE_URL + "/user/delete_profile.php")
                .then()
                .log().all()
                .statusCode(200)  // verificăm că ștergerea a avut succes
                .extract()
                .response();

        Response profileAfterDelete = RestAssured
                .given()
                .log().all()
                .header("Authorization", "Bearer " + bearerToken)
                .queryParam("user_id", regBody.getUser_id())
                .when()
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .log().all()
                .extract()
                .response();

        // Negative assertion
        int status = profileAfterDelete.getStatusCode();
        assertEquals(401, status, "Profile should not be accessible after delete");

        String errorMessage = profileAfterDelete.jsonPath().getString("message");
        assertFalse(errorMessage.equals("Unauthorized") || errorMessage.equals("Invalid or inactive API key"));


    }
}
