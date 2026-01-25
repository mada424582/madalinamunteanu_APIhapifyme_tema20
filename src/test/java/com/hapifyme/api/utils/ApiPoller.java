package com.hapifyme.api.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static com.hapifyme.api.utils.UserApiClient.BASE_URL;

public class ApiPoller {
    public static String waitForToken(String userId, int timeoutSeconds) {
        long start = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - start < timeout) {
            Response response = RestAssured
                    .given()
                    .queryParam("user_id", userId)
                    .get(BASE_URL + "/user/retrieve_token.php")
                    .then()
                    .extract()
                    .response();

            String token = response.jsonPath().getString("confirmation_token");
            if (token != null && !token.isEmpty()) {
                return token;
            }

            try {
                Thread.sleep(1000); // așteaptă 1 sec înainte de retry
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Confirmation token not available within timeout");
    }
}
