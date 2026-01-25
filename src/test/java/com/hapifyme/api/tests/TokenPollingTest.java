package com.hapifyme.api.tests;

import com.hapifyme.api.models.RegisterRequest;
import com.hapifyme.api.models.RegisterResponse;
import com.hapifyme.api.utils.ApiPollerOLD;
import com.hapifyme.api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;

public class TokenPollingTest {

    @Test(enabled = false)
    public void retrieveTokenWithAwaitilityTest() {

        String email = DataGenerator.randomEmail();
        String username = "user" + System.currentTimeMillis();
        RegisterRequest request =
                new RegisterRequest("John", "Doe", email, "Accident2020!", username);

        Response registerResponse =
                RestAssured
                        .given()
                        .contentType("application/json")
                        .body(request)
                        .post(BASE_URL + "/user/register.php");

        RegisterResponse regBody = registerResponse.as(RegisterResponse.class);

        String apiKey = regBody.getApiKey();
        String userId = regBody.getUserId();

        String confirmationToken = ApiPollerOLD.waitForToken(() -> {
            Response tokenResponse =
                    RestAssured
                            .given()
                            .queryParam("user_id", userId)
                            .queryParam("api_key", apiKey)
                            .get(BASE_URL + "/user/retrieve_token.php");

            return tokenResponse.jsonPath().getString("confirmation_token");
        });

        Assert.assertNotNull(
                confirmationToken,
                "Confirmation token was not generated in time"
        );
    }
}

