package com.ticketapp.exception;

public class TravelNotFoundException extends RuntimeException{
    private static final String TRAVEL_NOT_FOUND="Böyle bir sefer mevcut değil.";
    public TravelNotFoundException() {
        super(TRAVEL_NOT_FOUND);
    }
}
