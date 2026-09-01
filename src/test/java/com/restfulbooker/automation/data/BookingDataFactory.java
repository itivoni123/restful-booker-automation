package com.restfulbooker.automation.data;

import com.restfulbooker.automation.api.model.BookingData;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class BookingDataFactory {

    private BookingDataFactory() {
    }

    public static BookingData createBooking(int roomId) {
        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        int dateOffset =
                ThreadLocalRandom.current().nextInt(0, 3000);

        LocalDate checkin =
                LocalDate.of(2035, 1, 1).plusDays(dateOffset);

        LocalDate checkout =
                checkin.plusDays(2);

        return new BookingData(
                roomId,
                "Auto",
                "Test-" + suffix,
                true,
                null,
                null,
                new BookingData.BookingDates(
                        checkin.toString(),
                        checkout.toString()
                )
        );
    }
}
