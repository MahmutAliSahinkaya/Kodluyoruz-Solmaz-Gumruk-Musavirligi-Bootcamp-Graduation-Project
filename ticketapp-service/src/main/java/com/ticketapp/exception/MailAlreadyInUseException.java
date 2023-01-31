package com.ticketapp.exception;

public class MailAlreadyInUseException extends RuntimeException{
    private static final String MAIL_ALREADY_IN_USE= "Bu mailde zaten bir kullanıcı zaten mevcut.";


    public MailAlreadyInUseException() {
        super(MAIL_ALREADY_IN_USE);
    }
}
