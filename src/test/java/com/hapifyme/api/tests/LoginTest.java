package com.hapifyme.api.tests;

import com.hapifyme.api.models.*;
import com.hapifyme.api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;

import java.util.Map;

public class LoginTest {

    @Test(enabled = false)
    public void fullUserFlowTest() {

        // 1 Generare date user
        String firstName = "John";
        String lastName = "Doe";
        String email = DataGenerator.randomEmail();
        String password = "Accident2020!";
        String username = "user" + System.currentTimeMillis();

        // 2 Register
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password, username);

        Response registerResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(registerRequest)
                .post(BASE_URL + "/user/register.php")
                .then()
                .statusCode(201)
                .extract()
                .response();

        RegisterResponse regBody = registerResponse.as(RegisterResponse.class);
        String apiKey = regBody.getApiKey();
        String userId = regBody.getUserId();
        System.out.println("API KEY DIN REGISTER = " + apiKey);
        System.out.println("USER ID = " + userId);

        // Simulam că user-ul e confirmat
        System.out.println("Simulating email confirmation... skipping retrieve_token.php");

        // 5 Login
        LoginRequest loginRequest = new LoginRequest(username, password);

        Response loginResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(loginRequest)
                .post(BASE_URL + "/user/login.php")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String bearerToken = loginResponse.as(LoginResponse.class).getToken();
        Assert.assertNotNull(bearerToken, "Login token should exist");

        // 6 Get profile
        Response profileResponse = RestAssured
                .given()
                .queryParam("user_id", userId)
                .queryParam("api_key", apiKey)
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .statusCode(200)
                .extract()
                .response();

        ProfileResponse profile = profileResponse.as(ProfileResponse.class);
        Assert.assertEquals(profile.getUser().getEmail(), email);
        Assert.assertEquals(profile.getUser().getFirstName(), firstName);
        Assert.assertEquals(profile.getUser().getLastName(), lastName);
        Assert.assertEquals(profile.getUser().getUsername(), username);

        // 7 Update profile
        String newFirstName = "Jane";
        String newLastName = "Smith";

        RestAssured
                .given()
                .queryParam("user_id", userId)
                .queryParam("api_key", apiKey)
                .contentType("application/json")
                .body(Map.of(
                        "first_name", newFirstName,
                        "last_name", newLastName
                ))
                .post(BASE_URL + "/user/update_profile.php")
                .then()
                .statusCode(200);

        // 8 Get updated profile
        Response updatedProfileResponse = RestAssured
                .given()
                .queryParam("user_id", userId)
                .queryParam("api_key", apiKey)
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .statusCode(200)
                .extract()
                .response();

        ProfileResponse updatedProfile = updatedProfileResponse.as(ProfileResponse.class);
        Assert.assertEquals(updatedProfile.getUser().getFirstName(), newFirstName);
        Assert.assertEquals(updatedProfile.getUser().getLastName(), newLastName);

        // 9 Delete profile
        RestAssured
                .given()
                .queryParam("user_id", userId)
                .queryParam("api_key", apiKey)
                .delete(BASE_URL + "/user/delete_profile.php")
                .then()
                .statusCode(200);

        // 10 Negative check: profile should no longer exist
        RestAssured
                .given()
                .queryParam("user_id", userId)
                .queryParam("api_key", apiKey)
                .get(BASE_URL + "/user/get_profile.php")
                .then()
                .statusCode(404);

        System.out.println("Test finished successfully!");
    }
}
