package com.restfulbooker.automation.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class BookingPage {

    private final Page page;

    public BookingPage(Page page) {
        this.page = page;
    }

    public void searchAvailableRooms(
            String checkin,
            String checkout
    ) {
        Locator dateInputs = page.locator(".dateWrapper input");

        dateInputs.nth(0).fill(checkin);
        dateInputs.nth(1).fill(checkout);

        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Check Availability")
        ).click();

        page.locator("a[href^='/reservation/']")
                .first()
                .waitFor();
    }

    public void selectFirstAvailableRoom() {
        Locator roomBookingLink =
                page.locator("a[href^='/reservation/']").first();

        roomBookingLink.waitFor();
        roomBookingLink.click();
    }

    public void startReservation() {
        page.locator("#doReservation").click();
    }

    public void completeBookingForm(
            String firstname,
            String lastname,
            String email,
            String phone
    ) {
        page.getByLabel("Firstname").fill(firstname);
        page.getByLabel("Lastname").fill(lastname);
        page.getByLabel("Email").fill(email);
        page.getByLabel("Phone").fill(phone);
    }

    public void submitBooking() {
        page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions()
                        .setName("Reserve Now")
        ).click();
    }


    public Locator roomReservationLink(int roomId) {
        return page.locator(
                "a[href^='/reservation/" + roomId + "']"
        );
    }

    public Locator bookingConfirmation() {
        return page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions()
                        .setName("Booking Confirmed")
        );
    }

    public Locator validationError() {
        return page.locator(".alert.alert-danger");
    }
}
