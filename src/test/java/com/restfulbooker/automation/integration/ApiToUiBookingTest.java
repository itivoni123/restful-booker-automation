package com.restfulbooker.automation.integration;

import com.restfulbooker.automation.api.client.AuthApiClient;
import com.restfulbooker.automation.api.client.BookingApiClient;
import com.restfulbooker.automation.api.model.BookingData;
import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.data.BookingDataFactory;
import com.restfulbooker.automation.ui.base.BaseUiTest;
import com.restfulbooker.automation.ui.pages.BookingPage;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ApiToUiBookingTest extends BaseUiTest {

    private final AuthApiClient authApiClient =
            new AuthApiClient();

    private final BookingApiClient bookingApiClient =
            new BookingApiClient();

    @Test
    void shouldReflectApiCreatedBookingInUiAvailability() {
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

        LocalDate checkin =
                LocalDate.parse(
                        booking.bookingdates().checkin()
                );

        LocalDate checkout =
                LocalDate.parse(
                        booking.bookingdates().checkout()
                );

        BookingPage bookingPage =
                new BookingPage(page);

        page.navigate(ConfigManager.getBaseUrl());

        bookingPage.searchAvailableRooms(
                uiDate(checkin),
                uiDate(checkout)
        );

        assertThat(
                bookingPage.roomReservationLink(1)
        ).not().isVisible();
    }

    private static String uiDate(LocalDate date) {
        return date.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }
}
