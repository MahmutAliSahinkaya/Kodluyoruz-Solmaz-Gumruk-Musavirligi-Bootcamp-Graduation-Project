package com.ticketapp.exception;

public class SeatNotFoundException extends RuntimeException{
    private static final String SEAT_NOT_FOUND="Bu seferde böyle bir koltuk mevcut değil.";
    public SeatNotFoundException() {
        super(SEAT_NOT_FOUND);
    }
}
