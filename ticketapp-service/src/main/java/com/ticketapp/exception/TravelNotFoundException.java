package com.ticketapp.exception;

public class TravelNotFoundException extends RuntimeException{
    private static final String TRIP_NOT_FOUND="Böyle bir sefer mevcut değil.";
    public TravelNotFoundException() {
        super(TRIP_NOT_FOUND);
    }
}
