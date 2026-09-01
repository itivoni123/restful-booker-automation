package com.restfulbooker.automation.api.model;

public record BookingData(
        int roomid,
        String firstname,
        String lastname,
        boolean depositpaid,
        String email,
        String phone,
        BookingDates bookingdates
) {
    public record BookingDates(
            String checkin,
            String checkout
    ) {
    }
}
