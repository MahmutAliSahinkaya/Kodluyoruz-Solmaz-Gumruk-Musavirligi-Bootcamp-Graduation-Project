package com.ticketapp.dto;

import com.ticketapp.model.enums.Vehicle;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TravelDto implements Serializable {
    private Long id;
    private Vehicle vehicle;
    private BigDecimal ticketPrice;
    private String fromStation;
    private String toStation;
    private Integer seatCapacity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Set<Integer> purchasedSeats;
    private Boolean isCanceled;
}
