package com.hapifyme.api.tests;

import com.hapifyme.api.models.RegisterRequest;
import com.hapifyme.api.models.RegisterResponse;
import com.hapifyme.api.utils.ApiPoller;
import com.hapifyme.api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenPollingTest {

    @Test(enabled = false)
    public void retrieveTokenWithAwaitilityTest() {

        String email = DataGenerator.randomEmail();
        RegisterRequest request =
                new RegisterRequest("John", "Doe", email, "Accident2020!");

        Response registerResponse =
                RestAssured
                        .given()
                        .contentType("application/json")
                        .body(request)
                        .post(BASE_URL + "/user/register.php");

        RegisterResponse regBody = registerResponse.as(RegisterResponse.class);

        String apiKey = regBody.getApi_key();
        String userId = regBody.getUser_id();

        String confirmationToken = ApiPoller.waitForToken(() -> {
            Response tokenResponse =
                    RestAssured
                            .given()
                            .queryParam("user_id", userId)
                            .queryParam("api_key", apiKey)
                            .get(BASE_URL + "/user/retrieve_token.php");

            return tokenResponse.jsonPath().getString("confirmation_token");
        });

        assertNotNull(confirmationToken);
    }
}
