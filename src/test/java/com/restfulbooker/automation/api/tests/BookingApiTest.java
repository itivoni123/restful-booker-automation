package com.restfulbooker.automation.api.tests;

import com.restfulbooker.automation.api.client.AuthApiClient;
import com.restfulbooker.automation.api.client.BookingApiClient;
import com.restfulbooker.automation.api.model.BookingData;
import com.restfulbooker.automation.data.BookingDataFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingApiTest {

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final BookingApiClient bookingApiClient = new BookingApiClient();

    private String token;

    @BeforeEach
    void authenticate() {
        Response response =
                authApiClient.login("admin", "password");

        assertEquals(200, response.statusCode());

        token = response.jsonPath().getString("token");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldCreateAndRetrieveBooking() {
        BookingData booking =
                BookingDataFactory.createBooking(1);

        Response createResponse =
                bookingApiClient.createBooking(booking, token);

        assertEquals(201, createResponse.statusCode());

        int bookingId =
                createResponse.jsonPath().getInt("bookingid");

        assertTrue(bookingId > 0);

        Response getResponse =
                bookingApiClient.getBooking(bookingId, token);

        assertEquals(200, getResponse.statusCode());

        assertEquals(
                booking.firstname(),
                getResponse.jsonPath().getString("firstname")
        );

        assertEquals(
                booking.lastname(),
                getResponse.jsonPath().getString("lastname")
        );

        assertEquals(
                booking.roomid(),
                getResponse.jsonPath().getInt("roomid")
        );

        assertEquals(
                booking.depositpaid(),
                getResponse.jsonPath().getBoolean("depositpaid")
        );

        assertEquals(
                booking.bookingdates().checkin(),
                getResponse.jsonPath()
                        .getString("bookingdates.checkin")
        );

        assertEquals(
                booking.bookingdates().checkout(),
                getResponse.jsonPath()
                        .getString("bookingdates.checkout")
        );
    }

    @Test
    void shouldUpdateBookingAndPersistChanges() {
        BookingData original =
                BookingDataFactory.createBooking(1);

        Response createResponse =
                bookingApiClient.createBooking(original, token);

        assertEquals(201, createResponse.statusCode());

        int bookingId =
                createResponse.jsonPath().getInt("bookingid");

        String updatedCheckout =
                LocalDate.parse(
                        original.bookingdates().checkout()
                ).plusDays(1).toString();

        BookingData updated = new BookingData(
                original.roomid(),
                "Updated",
                original.lastname(),
                original.depositpaid(),
                original.email(),
                original.phone(),
                new BookingData.BookingDates(
                        original.bookingdates().checkin(),
                        updatedCheckout
                )
        );

        Response updateResponse =
                bookingApiClient.updateBooking(
                        bookingId,
                        updated,
                        token
                );

        assertEquals(200, updateResponse.statusCode());

        Response getResponse =
                bookingApiClient.getBooking(bookingId, token);

        assertEquals(200, getResponse.statusCode());

        assertEquals(
                "Updated",
                getResponse.jsonPath().getString("firstname")
        );

        assertEquals(
                updatedCheckout,
                getResponse.jsonPath()
                        .getString("bookingdates.checkout")
        );

        assertEquals(
                original.lastname(),
                getResponse.jsonPath().getString("lastname")
        );

        assertEquals(
                original.roomid(),
                getResponse.jsonPath().getInt("roomid")
        );
    }

    @Test
    void shouldRejectProtectedUpdateWithoutToken() {
        BookingData original =
                BookingDataFactory.createBooking(1);

        Response createResponse =
                bookingApiClient.createBooking(original, token);

        assertEquals(201, createResponse.statusCode());

        int bookingId =
                createResponse.jsonPath().getInt("bookingid");

        BookingData attemptedUpdate =
                new BookingData(
                        original.roomid(),
                        "Unauthorized",
                        original.lastname(),
                        original.depositpaid(),
                        original.email(),
                        original.phone(),
                        original.bookingdates()
                );

        Response unauthorizedResponse =
                bookingApiClient.updateBookingWithoutToken(
                        bookingId,
                        attemptedUpdate
                );

        assertTrue(
                unauthorizedResponse.statusCode() == 401
                        || unauthorizedResponse.statusCode() == 403
        );

        Response getResponse =
                bookingApiClient.getBooking(bookingId, token);

        assertEquals(200, getResponse.statusCode());

        assertEquals(
                original.firstname(),
                getResponse.jsonPath().getString("firstname")
        );

        assertNotEquals(
                "Unauthorized",
                getResponse.jsonPath().getString("firstname")
        );
    }
}
