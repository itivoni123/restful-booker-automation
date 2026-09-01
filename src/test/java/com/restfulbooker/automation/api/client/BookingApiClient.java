package com.restfulbooker.automation.api.client;

import com.restfulbooker.automation.api.model.BookingData;
import com.restfulbooker.automation.config.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingApiClient {

    public Response createBooking(BookingData booking, String token) {
        return given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(toPayload(booking))
                .when()
                .post("/api/booking");
    }

    public Response getBooking(int bookingId, String token) {
        return given()
                .baseUri(ConfigManager.getBaseUrl())
                .cookie("token", token)
                .when()
                .get("/api/booking/" + bookingId);
    }

    public Response updateBooking(
            int bookingId,
            BookingData booking,
            String token
    ) {
        Map<String, Object> payload = toPayload(booking);
        payload.put("bookingid", bookingId);

        return given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(payload)
                .when()
                .put("/api/booking/" + bookingId);
    }

    public Response updateBookingWithoutToken(
            int bookingId,
            BookingData booking
    ) {
        Map<String, Object> payload = toPayload(booking);
        payload.put("bookingid", bookingId);

        return given()
                .baseUri(ConfigManager.getBaseUrl())
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put("/api/booking/" + bookingId);
    }

    private Map<String, Object> toPayload(BookingData booking) {
        Map<String, Object> bookingDates = new LinkedHashMap<>();
        bookingDates.put(
                "checkin",
                booking.bookingdates().checkin()
        );
        bookingDates.put(
                "checkout",
                booking.bookingdates().checkout()
        );

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("roomid", booking.roomid());
        payload.put("firstname", booking.firstname());
        payload.put("lastname", booking.lastname());
        payload.put("depositpaid", booking.depositpaid());

        if (booking.email() != null) {
            payload.put("email", booking.email());
        }

        if (booking.phone() != null) {
            payload.put("phone", booking.phone());
        }

        payload.put("bookingdates", bookingDates);

        return payload;
    }
}
