package com.restfulbooker.automation.integration;

import com.restfulbooker.automation.api.client.AuthApiClient;
import com.restfulbooker.automation.api.client.BookingApiClient;
import com.restfulbooker.automation.api.model.BookingData;
import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.data.BookingDataFactory;
import com.restfulbooker.automation.ui.base.BaseUiTest;
import com.restfulbooker.automation.ui.pages.AdminPage;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApiToUiBookingTest extends BaseUiTest {

    private final AuthApiClient authApiClient =
            new AuthApiClient();

    private final BookingApiClient bookingApiClient =
            new BookingApiClient();

    @Test
    void shouldDisplayApiCreatedBookingInAdminUi() {
        Response loginResponse =
                authApiClient.login("admin", "password");

        assertEquals(200, loginResponse.statusCode());

        String token =
                loginResponse.jsonPath().getString("token");

        assertNotNull(token);
        assertFalse(token.isBlank());

        BookingData booking =
                BookingDataFactory.createBooking(1);

        Response createResponse =
                bookingApiClient.createBooking(
                        booking,
                        token
                );

        assertEquals(201, createResponse.statusCode());

        int bookingId =
                createResponse.jsonPath().getInt("bookingid");

        assertTrue(bookingId > 0);

        AdminPage adminPage =
                new AdminPage(page);

        page.navigate(
                ConfigManager.getBaseUrl() + "/admin"
        );

        adminPage.login("admin", "password");

        adminPage.openRoom(booking.roomid());

        /*
         * The booking ID returned by the API is passed into the
         * UI lookup. No hardcoded booking ID is used.
         */
        var bookingRow =
                adminPage.bookingById(bookingId);

        assertThat(bookingRow).isVisible();

        assertThat(bookingRow)
                .containsText(booking.firstname());

        assertThat(bookingRow)
                .containsText(booking.lastname());

        assertThat(bookingRow)
                .containsText(
                        String.valueOf(booking.depositpaid())
                );

        assertThat(bookingRow)
                .containsText(
                        booking.bookingdates().checkin()
                );

        assertThat(bookingRow)
                .containsText(
                        booking.bookingdates().checkout()
                );
    }
}
