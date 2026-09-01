package com.restfulbooker.automation.ui.pages;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.restfulbooker.automation.config.ConfigManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

import java.util.concurrent.atomic.AtomicReference;

public class AdminPage {

    private final Page page;

    private final AtomicReference<String> bookingResponseBody =
            new AtomicReference<>();

    public AdminPage(Page page) {
        this.page = page;

        page.onResponse(response -> {
            if (isBookingListResponse(response)) {
                bookingResponseBody.set(response.text());
            }
        });
    }

    public void login(
            String username,
            String password
    ) {
        page.getByPlaceholder("Enter username")
                .fill(username);

        page.getByPlaceholder("Password")
                .fill(password);

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Login")
        ).click();

        page.waitForURL("**/admin/rooms");
    }

    public void openRoom(int roomId) {
        bookingResponseBody.set(null);

        page.navigate(
                ConfigManager.getBaseUrl()
                        + "/admin/room/"
                        + roomId
        );

        page.locator(".detail")
                .first()
                .waitFor();
    }

    public Locator bookingById(int bookingId) {
        String responseBody =
                bookingResponseBody.get();

        if (responseBody == null) {
            throw new IllegalStateException(
                    "Booking list response was not captured"
            );
        }

        JsonObject root =
                JsonParser.parseString(responseBody)
                        .getAsJsonObject();

        JsonArray bookings =
                root.getAsJsonArray("bookings");

        for (int index = 0;
             index < bookings.size();
             index++) {

            JsonObject booking =
                    bookings.get(index)
                            .getAsJsonObject();

            if (booking.get("bookingid").getAsInt()
                    == bookingId) {

                return page.locator(".detail")
                        .nth(index);
            }
        }

        throw new IllegalStateException(
                "Booking ID "
                + bookingId
                + " was not found in the booking data "
                + "loaded by the Admin UI"
        );
    }

    private boolean isBookingListResponse(
            Response response
    ) {
        return response.status() == 200
                && response.url()
                .matches(
                        ".*/api/booking\\?roomid=\\d+.*"
                );
    }
}
