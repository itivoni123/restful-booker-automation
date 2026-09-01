package com.restfulbooker.automation.api.tests;

import com.restfulbooker.automation.api.client.AuthApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationApiTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void shouldAuthenticateWithValidCredentials() {
        Response response = authApiClient.login("admin", "password");

        assertEquals(200, response.statusCode());

        String token = response.jsonPath().getString("token");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldRejectInvalidCredentials() {
        Response response = authApiClient.login(
                "invalid-user",
                "invalid-password"
        );

        assertNotEquals(200, response.statusCode());
        assertNull(response.jsonPath().getString("token"));
    }
}
