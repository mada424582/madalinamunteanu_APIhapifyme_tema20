package com.hapifyme.api.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserApiClient {
    public static final String BASE_URL = "https://test.hapifyme.com/api";
    public Response confirmEmail(String token){
        return RestAssured
                .given()
                .queryParam("token", token)
                .get(BASE_URL + "/user/confirm_email.php");
    }
}
