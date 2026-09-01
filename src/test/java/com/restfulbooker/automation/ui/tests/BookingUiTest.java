package com.restfulbooker.automation.ui.tests;

import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.ui.base.BaseUiTest;
import com.restfulbooker.automation.ui.pages.BookingPage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class BookingUiTest extends BaseUiTest {

    @Test
    void shouldBookAvailableRoom() {
        BookingPage bookingPage = new BookingPage(page);

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        LocalDate checkin = uniqueFutureDate();
        LocalDate checkout = checkin.plusDays(2);

        page.navigate(ConfigManager.getBaseUrl());

        bookingPage.searchAvailableRooms(
                uiDate(checkin),
                uiDate(checkout)
        );
        bookingPage.selectFirstAvailableRoom();
        bookingPage.startReservation();

        bookingPage.completeBookingForm(
                "Auto",
                "Test" + suffix,
                "auto-" + suffix + "@example.com",
                "07123456789"
        );

        bookingPage.submitBooking();

        assertThat(
                bookingPage.bookingConfirmation()
        ).containsText("Booking Confirmed");
    }

    @Test
    void shouldRejectBookingWithMissingFirstname() {
        BookingPage bookingPage = new BookingPage(page);

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        LocalDate checkin = uniqueFutureDate();
        LocalDate checkout = checkin.plusDays(2);

        page.navigate(ConfigManager.getBaseUrl());

        bookingPage.searchAvailableRooms(
                uiDate(checkin),
                uiDate(checkout)
        );
        bookingPage.selectFirstAvailableRoom();
        bookingPage.startReservation();

        bookingPage.completeBookingForm(
                "",
                "Test" + suffix,
                "auto-" + suffix + "@example.com",
                "07123456789"
        );

        bookingPage.submitBooking();

        assertThat(
                bookingPage.validationError()
        ).containsText("Firstname should not be blank");

        assertThat(
                bookingPage.bookingConfirmation()
        ).not().isVisible();
    }

    private static LocalDate uniqueFutureDate() {
        int offset =
                ThreadLocalRandom.current().nextInt(0, 3000);

        return LocalDate.of(2035, 1, 1)
                .plusDays(offset);
    }

    private static String uiDate(LocalDate date) {
        return date.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }

}