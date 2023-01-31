package com.ticketapp.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String USER_NOT_FOUND = "Bu maile kayıtlı bir kullanıcı mevcut değil.";
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
