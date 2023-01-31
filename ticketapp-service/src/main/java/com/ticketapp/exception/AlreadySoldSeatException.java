package com.ticketapp.exception;

public class AlreadySoldSeatException extends RuntimeException{
    public AlreadySoldSeatException(Integer seatNo) {
        super(seatNo.toString() + " numaralı koltuk daha önce satın alınmış, bu bilet satın alınamadı.");
    }
}
