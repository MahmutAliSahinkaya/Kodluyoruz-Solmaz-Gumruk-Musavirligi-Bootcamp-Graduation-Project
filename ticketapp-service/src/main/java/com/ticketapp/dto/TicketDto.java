package com.ticketapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TicketDto implements Serializable {
    private Long id;
    private Integer seatNumber;
    private TravelDto travel;
    private PassengerDto passenger;

    private PaymentDto payment;
}
