package com.restfulbooker.automation.api.client;

import com.restfulbooker.automation.config.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthApiClient {

    public Response login(String username, String password) {
        return given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", username,
                        "password", password
                ))
                .when()
                .post("/api/auth/login");
    }
}
